package main.scala.recommender.repository.interpreter

import common.domain.recommendations.SeededRecommendation
import main.scala.recommender.repository.RecommendationRepository
import slick.jdbc.PostgresProfile.api._
import main.scala.common.model.SeededRecs._
import common.model.SlickColumnMapper._

import scala.common.domain.movies.MovieId


class RecommendationSlickRepository
  extends RecommendationRepository[DBIO, SeededRecommendation] {

  override def getBySourceId(contentId: MovieId, take: Int=6): DBIO[Seq[SeededRecommendation]] = {
    val query =
      seededRecs
        .filter(_.source === contentId)
        .sortBy(_.confidence.desc)
        .take(take)
    query.result
  }

  override def getBySourceIn(seeds: Set[MovieId]): DBIO[Seq[(MovieId, Option[BigDecimal])]] = {
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
