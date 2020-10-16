package service.interpretor

import analytics.service.AnalyticService
import cats.{Monad, MonadError, ~>}
import domain._
import main.scala.common.model.MovieGenre
import repository._
import cats.implicits._


class AnalyticServiceInterpreter[F[_], DbEffect[_]](ratingService: RatingRepository[DbEffect],
                                                    movieService: MovieRepository[DbEffect],
                                                    logService: EventRepository[DbEffect],
                                                    genreService: GenreRepository[DbEffect],
                                                   evalDb: DbEffect ~> F)
                                                   (implicit mMonadError: MonadError[F, Throwable],
                                                   dbEffectMonad: Monad[DbEffect])
  extends AnalyticService[F] {

  override def getUserAnalytics(userId: UserId): F[UserAnalytics] = {
    val userRatings = evalDb(ratingService.getByUser(userId))
    val movies = userRatings.flatMap { ur =>
      evalDb(movieService.findAllByIds(ur.map(_.movieId).toSet))
    }
    val moviesWithGenres = movies.flatMap { movies =>
      evalDb(movieService.moviesWithGenres(movies.map(_.movieId).toSet))
    }
    val log = evalDb(logService.getByUserId(userId))
    val userAnalytics = for {
      ur <- userRatings
      l <- log
      m <- movies
      mwg <- moviesWithGenres
      ratings = ur.map(rating => rating.movieId -> rating).toMap
    } yield UserAnalytics(userId,
      buildGenreStatistics(mwg, ratings),
      ratings.size,
      buildMovieDTOs(m, ratings),
      l)

    userAnalytics
  }

  override def getContentAnalytics(contentId: ContentId): F[ContentAnalytics] = ???

  private def buildMovieDTOs(movies: Seq[Movie],
                             ratings: Map[String, Rating]) = {
    movies.map(movie => MovieDTO(movie.movieId, movie.title, ratings.get(movie.movieId)))
  }

  //TODO probably a more efficient way to do this
  private def buildGenreStatistics(moviesWithGenres: Seq[(Movie, MovieGenre)],
                                  ratings: Map[String, Rating]): GenreStatistics = {
    val userAvg = if (ratings.nonEmpty) {
      val ratingSum = ratings.foldLeft(BigDecimal(0.0)) {
        case (acc, (id, rating)) => acc + rating.rating
      }
      ratingSum / ratings.size.toFloat
    } else {
      BigDecimal(0.0)
    }

    val genreCounts = moviesWithGenres.map {
      case (_, movieGenre) => movieGenre.genreId
    }.groupBy(identity)
      .mapValues(x => BigDecimal(x.size))

    val genreRatings = moviesWithGenres.foldLeft(Map[Int, BigDecimal]()) {
      case (acc, (movie, movieGenre)) => {
        val rating = ratings.get(movie.movieId).map(_.rating - userAvg).getOrElse(BigDecimal(0.0))
        val updated = acc.getOrElse(movieGenre.genreId, BigDecimal(0)) + rating
        acc.updated(movieGenre.genreId, updated)
      }
    }

    val maxValue = genreRatings.valuesIterator.max.max(1.0)
    val maxCount = genreCounts.valuesIterator.max.max(BigDecimal(1.0))

    val genreRatingList = genreRatings.map {
      case (k, v) => (k, "rating", v / maxValue)
    }.toList

    val genreCountList = genreCounts.map {
      case (k, v) => (k, "count", v / maxCount)
    }.toList

    val genres = genreRatingList ::: genreCountList

    GenreStatistics(genres, userAvg)
  }
}



