package main.scala.recommender.repository

import scala.concurrent.Future

trait RecommendationRepository[SeededRec] {
  type RecommendationWithAvgConf = (String, Option[BigDecimal])

  def getBySourceId(source: String, take: Int=6): Future[Seq[SeededRec]]
  def getBySourceIn(seeds: Set[String]): Future[Seq[RecommendationWithAvgConf]]

}
