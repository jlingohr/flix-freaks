package domain

import java.time.Instant

case class Rating(userId: String,
                  movieId: String,
                  rating: BigDecimal,
                  timeStamp: Instant,
                  isExplicit: Boolean = true)
