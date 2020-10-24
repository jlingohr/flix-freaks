package common.json

import java.time.Instant
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import common.domain.auth.UserId
import common.domain.events.{AddToList, Details, EventLog, EventType, GenreView, MoreDetails, Play, SessionId}
import common.domain.genres.{Genre, GenreId, GenreName}
import common.domain.ratings.UserRating
import spray.json.{DefaultJsonProtocol, DeserializationException, JsNumber, JsString, JsValue, JsonFormat, RootJsonFormat, deserializationError}

import scala.common.domain.movies.{Movie, MovieDetail, MovieId, Title}

trait CommonSprayCodecs extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val instantFormat: JsonFormat[Instant] =
    new JsonFormat[Instant] {
      private val formatter = DateTimeFormatter.ISO_INSTANT
      override def write(x: Instant): JsValue = JsString(formatter.format(x))

      override def read(value: JsValue): Instant = value match {
        case JsString(x) => Instant.parse(x)
        case x => throw DeserializationException(s"Wrong time format of $x")
      }
    }

  implicit val eventTypeFormat: JsonFormat[EventType] =
    new JsonFormat[EventType] {
      def write(obj: EventType): JsValue = obj match {
        case GenreView => JsString("GenreView")
        case Details => JsString("Details")
        case MoreDetails => JsString("MoreDetails")
        case AddToList => JsString("AddToList")
        case Play => JsString("Play")
      }

      override def read(json: JsValue): EventType = ???
    }

  implicit val userIdFormat: JsonFormat[UserId] =
    new JsonFormat[UserId] {
      override def write(obj: UserId): JsValue = JsString(s"${obj.value}")

      override def read(json: JsValue): UserId = UserId(json.toString)
    }

  implicit val movieIdFormat: JsonFormat[MovieId] =
    new JsonFormat[MovieId] {
      override def read(json: JsValue): MovieId = MovieId(json.toString)

      override def write(obj: MovieId): JsValue = JsString(s"${obj.value}")
    }

  implicit val movieTitleFormat: JsonFormat[Title] =
    new JsonFormat[Title] {
      override def write(obj: Title): JsValue = JsString(s"${obj.value}")

      override def read(json: JsValue): Title = Title(json.toString)
    }

  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat3(Movie)

  implicit val genreIdFormat: JsonFormat[GenreId] =
    new JsonFormat[GenreId] {
      override def write(obj: GenreId): JsValue = JsString(s"${obj.value}")

      override def read(json: JsValue): GenreId = json match {
        case JsNumber(value) => GenreId(value.toInt)
        case _ => deserializationError("Int expected")
      }
    }

  implicit val genreNameFormat: JsonFormat[GenreName] =
    new JsonFormat[GenreName] {
      override def write(obj: GenreName): JsValue = JsString(s"${obj.value}")

      override def read(json: JsValue): GenreName = GenreName(json.toString)
    }

  implicit val genreFormat: RootJsonFormat[Genre] = jsonFormat2(Genre)

  implicit val movieDetailFormat: RootJsonFormat[MovieDetail] = jsonFormat2(MovieDetail)

  implicit val ratingFormat: RootJsonFormat[UserRating] = jsonFormat5(UserRating)

  implicit val sessionIdFormat: JsonFormat[SessionId] =
    new JsonFormat[SessionId] {
      override def write(obj: SessionId): JsValue = JsString(s"${obj.value}")

      override def read(json: JsValue): SessionId = SessionId(json.toString)
    }

  implicit val eventLogFormat: RootJsonFormat[EventLog] = jsonFormat6(EventLog)

}
