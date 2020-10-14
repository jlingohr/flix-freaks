package main.scala.recommender.repository.interpreter

import config.DatabaseConfig
import main.scala.common.domain.SeededRecommendation
import main.scala.recommender.repository.RecommendationRepository
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future
import main.scala.common.model.SeededRecs._

import scala.concurrent.ExecutionContext.Implicits.global

//case class RecommendationWithAvgConf(target: String, confidence: BigDecimal)

class RecommendationSlickRepository
  extends RecommendationRepository[SeededRecommendation]
    with DatabaseConfig{

  override def getBySourceId(contentId: String, take: Int=6): Future[Seq[SeededRecommendation]] = {
    val query =
      seededRecs
        .filter(_.source === contentId)
        .sortBy(_.confidence.desc)
        .take(take)
    db.run(query.result)
  }

  override def getBySourceIn(seeds: Set[String]): Future[Seq[(String, Option[BigDecimal])]] = {
    val query =
      seededRecs
        .filter(t => t.source.inSet(seeds) && !t.target.inSet(seeds))
        .groupBy(_.target)
        .map {
          case (id, res) => (id, res.map(_.confidence).avg)
        }
        .sortBy(_._2.desc)
    db.run(query.result)
  }
}

object RecommendationRepository extends RecommendationSlickRepository