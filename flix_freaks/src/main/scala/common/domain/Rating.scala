package domain

import java.time.Instant

case class Rating(userId: String,
                  movieId: String,
                  rating: Float,
                  timeStamp: Instant,
                  isExplicit: Boolean = true)
