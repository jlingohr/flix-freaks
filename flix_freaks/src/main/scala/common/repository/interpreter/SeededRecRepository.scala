package main.scala.common.repository.interpreter

import config.DatabaseConfig
import main.scala.common.domain.SeededRecommendation
import main.scala.common.repository.SeededRecRepository
import main.scala.common.model.SeededRecs._
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Future

class SeededRecSlickInterpreter extends SeededRecRepository[SeededRecommendation, Int] with DatabaseConfig {
  override def save(rec: SeededRecommendation): Future[Int] =
    db.run(seededRecs += rec)
}

object SeededRecRepository extends SeededRecSlickInterpreter
