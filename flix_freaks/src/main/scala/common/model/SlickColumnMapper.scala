package common.model

import slick.jdbc.PostgresProfile.api._
import common.domain.auth.UserId
import common.domain.events.SessionId
import common.domain.genres.{GenreId, GenreName}
import io.estatico.newtype.ops.toCoercibleIdOps
import slick.jdbc.JdbcType

import scala.common.domain.movies.{MovieId, Title}

object SlickColumnMapper {

  implicit lazy val movieIdMapper: JdbcType[MovieId] = implicitly[JdbcType[String]].coerce[JdbcType[MovieId]]
  implicit lazy val titleMapper: JdbcType[Title] = implicitly[JdbcType[String]].coerce[JdbcType[Title]]

  implicit lazy val genreIdMapper: JdbcType[GenreId] = implicitly[JdbcType[Int]].coerce[JdbcType[GenreId]]
  implicit lazy val genreNameMapper: JdbcType[GenreName] = implicitly[JdbcType[String]].coerce[JdbcType[GenreName]]

  implicit lazy val userIdMapper: JdbcType[UserId] = implicitly[JdbcType[String]].coerce[JdbcType[UserId]]
  implicit lazy val sessionIdMapper: JdbcType[SessionId] = implicitly[JdbcType[String]].coerce[JdbcType[SessionId]]


}
