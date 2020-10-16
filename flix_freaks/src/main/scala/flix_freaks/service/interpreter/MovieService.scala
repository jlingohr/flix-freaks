package main.scala.flix_freaks.service.interpreter


import cats.{Monad, ~>}
import domain.{Movie, MovieDetail}
import main.scala.flix_freaks.service.MovieService
import repository.{GenreRepository, MovieRepository}
import cats.implicits._
import cats.MonadError


class MovieServiceInterpreter[F[_], DbEffect[_]](movieRepository: MovieRepository[DbEffect],
                                                genreRepository: GenreRepository[DbEffect],
                                                evalDb: DbEffect ~> F)
                                                (implicit mMonadError: MonadError[F, Throwable],
                                                 dbEffectMonad: Monad[DbEffect])
  extends MovieService[F] {

  def getMovieDetails(movieId: String): F[Option[MovieDetail]] = {
    val movie = evalDb(movieRepository.findById(movieId))
    val genres = evalDb(genreRepository.findAll)
    val movieWithGenres = evalDb(movieRepository.movieWithGenres(movieId))

    val result = (movie, genres, movieWithGenres).mapN { (movie, gen, movieWithGenres) =>
      movie.map { mov =>
        val mwg = movieWithGenres.map(_._2).map(_.genreId)
        MovieDetail(mov, gen.filter(g => mwg.contains(g.id.get)))
      }
    }
    result
  }

  def getMoviesByGenre(genreId: Int): F[Seq[Movie]] = {
    val movies = evalDb(movieRepository.moviesByGenre(genreId))
    movies
  }
}