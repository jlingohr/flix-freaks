package json

import java.time.Instant
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

trait SprayJsonCodes extends SprayJsonSupport with DefaultJsonProtocol {
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
        case genreView => JsString("genreView")
        case details => JsString("details")
        case moreDetails => JsString("moreDetails")
        case addToList => JsString("addToList")
        case play => JsString("play")
      }

      override def read(json: JsValue): EventType = ???
    }

  implicit val userIdFormat: RootJsonFormat[UserId] = jsonFormat1(UserId)
  implicit val contentIdFormat: RootJsonFormat[ContentId] = jsonFormat1(ContentId)
  implicit val ratingFormat: RootJsonFormat[Rating] = jsonFormat5(Rating)
  implicit val genreStatisticsFormat: RootJsonFormat[GenreStatistics] = jsonFormat2(GenreStatistics)
  implicit val movieDTOFormat: RootJsonFormat[MovieDTO] = jsonFormat3(MovieDTO)
  implicit val eventLogFormat: RootJsonFormat[EventLog] = jsonFormat6(EventLog)
  implicit val userAnalyticsFormat: RootJsonFormat[UserAnalytics] = jsonFormat5(UserAnalytics)
  implicit val contentAnalyticsFormat: RootJsonFormat[ContentAnalytics] = jsonFormat7(ContentAnalytics)
}
