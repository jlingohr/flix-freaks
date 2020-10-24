package common.domain

import java.time.Instant

import scala.common.domain.movies.MovieId

object recommendations {

  case class SeededRecommendation(created: Instant,
                                  source: MovieId,
                                  target: MovieId,
                                  support: BigDecimal,
                                  confidence: BigDecimal,
                                  recommendationType: String)

}
