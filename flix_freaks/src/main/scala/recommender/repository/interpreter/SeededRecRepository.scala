package main.scala.recommender.repository.interpreter

import config.DatabaseConfig
import main.scala.common.domain.SeededRecommendation
import main.scala.recommender.repository.SeededRecRepository
import slick.jdbc.PostgresProfile.api._


import scala.concurrent.Future
import main.scala.recommender.model.SeededRecs._

class SeededRecSlickInterpreter extends SeededRecRepository[SeededRecommendation, Int] with DatabaseConfig {
  override def save(rec: SeededRecommendation): Future[Int] =
    db.run(seededRecs += rec)
}

object SeededRecRepository extends SeededRecSlickInterpreter
