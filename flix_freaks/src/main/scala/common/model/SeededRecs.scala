package main.scala.common.model

import java.time.Instant

import common.domain.recommendations.SeededRecommendation
import io.estatico.newtype.ops.toCoercibleIdOps
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.common.domain.movies.MovieId

class SeededRecs(tag: Tag) extends Table[SeededRecommendation](tag, "seed_recs") {

  import common.model.SlickColumnMapper._

  def created = column[Instant]("created")
  def source = column[MovieId]("source")
  def target = column[MovieId]("target")
  def support = column[BigDecimal]("support")
  def confidence = column[BigDecimal]("confidence")
  def rec_type = column[String]("type")

  def * = (created, source, target, support, confidence, rec_type) <> (SeededRecommendation.tupled, SeededRecommendation.unapply)


}

object SeededRecs {
  val seededRecs = TableQuery[SeededRecs]
}