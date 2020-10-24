package main.scala.common.model

import java.sql.Timestamp
import java.time.Instant
import java.util.Date

import common.domain.auth.UserId
import common.domain.ratings.UserRating
import io.estatico.newtype.ops.toCoercibleIdOps
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.common.domain.movies.MovieId


object DateMapper {
  implicit val dateToTimestamp = MappedColumnType.base[Date, Timestamp](
    { utilDate => new Timestamp(utilDate.getTime) },
    { sqlTimestamp => new Date(sqlTimestamp.getTime) }
  )
}

class RatingTable(tag: Tag) extends Table[UserRating](tag, "rating") {

  import common.model.SlickColumnMapper._

  def userId: Rep[UserId] = column[UserId]("user_id")
  def movieId: Rep[MovieId] = column[MovieId]("movie_id")
  def rating: Rep[BigDecimal] = column[BigDecimal]("rating")
  def ratingTimestamp: Rep[Instant] = column[Instant]("rating_timestamp") //(DateMapper.dateToTimestamp)
  def isExplicit: Rep[Boolean] = column[Boolean]("is_explicit", O.Default(true))

  def * = (userId, movieId, rating, ratingTimestamp, isExplicit) <> (UserRating.tupled, UserRating.unapply)
}

object Ratings {
  val ratings = TableQuery[RatingTable]
}
