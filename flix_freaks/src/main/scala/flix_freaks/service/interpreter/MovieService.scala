package main.scala.flix_freaks.service.interpreter

import domain.{Movie, MovieDetail}
import repository.interpreter.{GenreSlickRepository, MovieSlickRepository}

import scala.concurrent.{ExecutionContext, Future}

class MovieService()(implicit ec: ExecutionContext){

  def getMovieDetails(movieId: String): Future[Option[MovieDetail]] = {
    val movie = MovieSlickRepository.findById(movieId)
    val genres = GenreSlickRepository.findAll
    val movieWithGenres = MovieSlickRepository.movieWithGenres(movieId)

    movie.flatMap {
      case Some(movie) => {
        val movieDetail = for {
          genres <- genres
          mwg <- movieWithGenres.map(_.map(_._2).map(_.genreId)) //TODO better way than map 3 times...
        } yield Some(domain.MovieDetail(movie, genres.filter(g => mwg.contains(g.id.get))))

        movieDetail
      }
      case None => Future {
        None
      }
    }
  }

  def getMoviesByGenre(genreId: Int): Future[Seq[Movie]] = {
    val movies = MovieSlickRepository.moviesByGenre(genreId)
    movies
  }
}