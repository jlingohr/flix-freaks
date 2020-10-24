package main.scala.common.repository.interpreter

import common.domain.recommendations.SeededRecommendation
import config.DatabaseConfig
import main.scala.common.repository.SeededRecRepository
import main.scala.common.model.SeededRecs._
import slick.jdbc.PostgresProfile.api._


class SeededRecSlickInterpreter extends SeededRecRepository[DBIO, SeededRecommendation, Int] with DatabaseConfig {
  override def save(rec: SeededRecommendation): DBIO[Int] =
    seededRecs += rec
}
