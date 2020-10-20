package main.scala.recommender.repository.interpreter

import main.scala.common.domain.SeededRecommendation
import main.scala.recommender.repository.RecommendationRepository
import slick.jdbc.PostgresProfile.api._

import main.scala.common.model.SeededRecs._


//case class RecommendationWithAvgConf(target: String, confidence: BigDecimal)

class RecommendationSlickRepository
  extends RecommendationRepository[DBIO, SeededRecommendation] {

  override def getBySourceId(contentId: String, take: Int=6): DBIO[Seq[SeededRecommendation]] = {
    val query =
      seededRecs
        .filter(_.source === contentId)
        .sortBy(_.confidence.desc)
        .take(take)
    query.result
  }

  override def getBySourceIn(seeds: Set[String]): DBIO[Seq[(String, Option[BigDecimal])]] = {
    val query =
      seededRecs
        .filter(t => t.source.inSet(seeds) && !t.target.inSet(seeds))
        .groupBy(_.target)
        .map {
          case (id, res) => (id, res.map(_.confidence).avg)
        }
        .sortBy(_._2.desc)
    query.result
  }
}

object RecommendationRepository extends RecommendationSlickRepository