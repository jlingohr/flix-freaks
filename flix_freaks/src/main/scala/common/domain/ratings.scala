package common.domain

import java.time.Instant

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId

object ratings {

  case class UserRating(userId: UserId,
                        movieId: MovieId,
                        rating: BigDecimal,
                        timeStamp: Instant,
                        isExplicit: Boolean = true)
}
