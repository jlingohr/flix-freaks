package common.domain

import java.time.Instant

import common.domain.auth.UserId
import io.estatico.newtype.macros.newtype

import scala.common.domain.movies.MovieId

object events {

  sealed trait EventType
  case object GenreView extends EventType
  case object Details extends EventType
  case object MoreDetails extends EventType
  case object AddToList extends EventType
  case object Play extends EventType

  object EventType {
    def apply(eventType: String): EventType = eventType match {
      case "GenreView" => GenreView
      case "Details" => Details
      case "MoreDetails" => MoreDetails
      case "AddToList" => AddToList
      case "Play" => Play
    }
  }

  @newtype case class SessionId(value: String)

  case class EventLog(id: Int = 0,
                      created: Instant,
                      userId: UserId,
                      contentId: MovieId,
                      event: EventType,
                      sessionId: SessionId)

  case class AggregatedUserEvent(userId: UserId, contentId: MovieId, event: EventType, count: Int)

}
