package main.scala.scripts

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import domain.{Genre, Movie}
import main.scala.common.repository.{GenreTable, MovieGenre, MovieGenreTable, MovieTable}
import slick.lifted.TableQuery
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContextExecutor, Future}

object populateMovies extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty, "SingleRequest")
  implicit val executionContext: ExecutionContextExecutor = system.executionContext

  val db = Database.forConfig("database")

  val movieTable = TableQuery[MovieTable]
  val genreTable = TableQuery[GenreTable]
  val movieGenreTable = TableQuery[MovieGenreTable]

  val uri = "https://raw.githubusercontent.com/sidooms/MovieTweetings/master/latest/movies.dat"

  def downloadMovies(): Future[String] = {
    val responseFuture: Future[HttpResponse] = Http().singleRequest(HttpRequest(uri = uri))

    responseFuture
      .flatMap(_.entity.toStrict(2 seconds))
      .map(_.data.utf8String)
  }

  def parseMovie(res: String): Option[(Movie, Array[Genre])] = {
    val split = res.split("::")
    if (split.length == 3) {
      val movieId = split(0)
      val fullTitle = split(1)
      val genreString = split(2)

      val titleAndYear = fullTitle.split('(')
      val title = titleAndYear(0)
      val year = titleAndYear(1).substring(0, 4).toIntOption

      val movie = Movie(movieId, title, year)

      val genres = genreString
        .split('|')
        .map(Genre(None, _))

      Some((movie, genres))
    } else {
      None
    }

  }

  val movieData = downloadMovies()
  val moviesAndGenres = movieData.map { res =>
    res
      .split('\n')
      .flatMap(parseMovie)
  }

  try {
    val resultFuture = moviesAndGenres.flatMap { res =>
      // Bulk insert movies and genres
      val movies = {
        res.map {
          case (movie, _) =>
            movie
        }
      }

      val genres =
        res
          .flatMap {
            case (_: Movie, genres: Array[Genre]) => genres
          }.distinct

      val insertMoviesQuery = movieTable ++= movies

      val insertGenresWithInc = genreTable returning genreTable.map(_.id) into ((genre, id) => genre.copy(Some(id)))
      val insertGenresQuery = insertGenresWithInc ++= genres

      val insertAction = DBIO.seq(insertMoviesQuery, insertGenresQuery)
      val insertFuture = db.run(insertAction)
      val insertedGenres = insertFuture.flatMap { _ =>
        db.run(genreTable.result).map(_.map(genre => genre.name -> genre.id).toMap)
      }

      // Use genres with ids to update MovieGenre table
      insertedGenres.flatMap { insertedGenres =>
        val movieWithGenres = res.flatMap {
          case (movie, movieGenres) => movieGenres.map(genre => MovieGenre(movie.movieId, insertedGenres(genre.name).get))
        }

        val insertMovieGenreQuery = movieGenreTable ++= movieWithGenres
        val insertMovieGenreAction = DBIO.seq(insertMovieGenreQuery)
        db.run(insertMovieGenreAction)
      }

    }
    Await.result(resultFuture, Duration.Inf)
  } finally {
    db.close
  }

}
