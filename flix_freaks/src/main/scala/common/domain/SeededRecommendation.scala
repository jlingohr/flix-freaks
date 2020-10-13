package main.scala.common.domain
import java.time.Instant

case class SeededRecommendation(created: Instant,
                                source: String,
                                target: String,
                                support: BigDecimal,
                                confidence: BigDecimal,
                                recommendationType: String)
