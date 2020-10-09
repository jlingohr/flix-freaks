package main.scala.common.repository

import java.sql.Timestamp
import java.time.Instant
import java.util.Date

import domain.Rating
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._


object DateMapper {
  implicit val dateToTimestamp = MappedColumnType.base[Date, Timestamp](
    { utilDate => new Timestamp(utilDate.getTime()) },
    { sqlTimestamp => new Date(sqlTimestamp.getTime()) }
  )
}

class RatingTable(tag: Tag) extends Table[Rating](tag, "rating") {
  def userId: Rep[String] = column[String]("user_id")
  def movieId: Rep[String] = column[String]("movie_id")
  def rating: Rep[BigDecimal] = column[BigDecimal]("rating")
  def ratingTimestamp: Rep[Instant] = column[Instant]("rating_timestamp") //(DateMapper.dateToTimestamp)
  def isExplicit: Rep[Boolean] = column[Boolean]("is_explicit", O.Default(true))

  def * = (userId, movieId, rating, ratingTimestamp, isExplicit) <> ((Rating.apply _).tupled, Rating.unapply)
}

object Ratings {
  val ratings = TableQuery[RatingTable]
}
