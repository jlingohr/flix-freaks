package service.interpretor

import analytics.service.AnalyticService
import domain.{ContentAnalytics, ContentId, EventLog, Genre, GenreStatistics, Movie, MovieDTO, Rating, UserAnalytics, UserId}
import repository.{EventRepository, GenreRepository, MovieGenre, MovieRepository, RatingRepository}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class AnalyticServiceInterpreter(ratingService: RatingRepository,
                                 movieService: MovieRepository,
                                 logService: EventRepository,
                                 genreService: GenreRepository) extends AnalyticService {

  override def getUserAnalytics(userId: UserId): Future[UserAnalytics] = {
    val userAnalytics = for {
      userRatings <- ratingService.getByUser(userId)
      movies <- movieService.findAllByIds(userRatings.map(_.movieId).toSet)
      log <- logService.getByUserId(userId)
      mwg <- movieService.moviesWithGenres(movies.map(_.movieId).toSet)
      ratings = userRatings.map(rating => rating.movieId -> rating).toMap
    } yield UserAnalytics(userId,
      buildGenreStatistics(mwg, ratings),
      ratings.size,
      buildMovieDTOs(movies, ratings),
      log)

    userAnalytics
  }

  override def getContentAnalytics(contentId: ContentId): Future[ContentAnalytics] = ???

  private def buildMovieDTOs(movies: Seq[Movie],
                             ratings: Map[String, Rating]) = {
    movies.map(movie => MovieDTO(movie.movieId, movie.title, ratings.get(movie.movieId)))
  }

  //TODO probably a more efficient way to do this
  private def buildGenreStatistics(moviesWithGenres: Seq[(Movie, MovieGenre)],
                                  ratings: Map[String, Rating]): GenreStatistics = {
    val userAvg = if (ratings.nonEmpty) {
      val ratingSum = ratings.foldLeft(0.0) {
        case (acc, (id, rating)) => acc + rating.rating
      }
      ratingSum / ratings.size.toFloat
    } else {
      0.0
    }

    val genreCounts = moviesWithGenres.map {
      case (_, movieGenre) => movieGenre.genreId
    }.groupBy(identity)
      .mapValues(x => BigDecimal(x.size))

    val genreRatings = moviesWithGenres.foldLeft(Map[Int, BigDecimal]()) {
      case (acc, (movie, movieGenre)) => {
        val rating = ratings.get(movie.movieId).map(_.rating - userAvg).getOrElse(0.0)
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



