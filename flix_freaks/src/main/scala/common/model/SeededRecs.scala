package main.scala.common.model

import java.time.Instant

import main.scala.common.domain.SeededRecommendation
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

class SeededRecs(tag: Tag) extends Table[SeededRecommendation](tag, "seed_recs") {
  def created = column[Instant]("created")
  def source = column[String]("source")
  def target = column[String]("target")
  def support = column[BigDecimal]("support")
  def confidence = column[BigDecimal]("confidence")
  def rec_type = column[String]("type")

  def * = (created, source, target, support, confidence, rec_type) <> ((SeededRecommendation.apply _).tupled, SeededRecommendation.unapply)


}

object SeededRecs {
  val seededRecs = TableQuery[SeededRecs]
}