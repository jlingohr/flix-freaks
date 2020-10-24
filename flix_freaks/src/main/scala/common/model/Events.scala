package main.scala.common.model

import java.time.Instant

import common.domain.auth.UserId
import common.domain.events.{AddToList, Details, EventLog, EventType, GenreView, MoreDetails, Play, SessionId}
import io.estatico.newtype.ops.toCoercibleIdOps
import slick.jdbc.JdbcType
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag

import scala.common.domain.movies.{MovieId, Title}


object EventMapper {
  implicit val eventStateMapper = MappedColumnType.base[EventType, String](
    {
      case GenreView => "GenreView";
      case Details => "Details";
      case MoreDetails => "MoreDetails";
      case AddToList => "AddToList";
      case Play => "Play";
    },
    {
      case "GenreView" => GenreView;
      case "Details" => Details;
      case "MoreDetails" => MoreDetails;
      case "AddToList" => AddToList;
      case "Play" => Play;
    }
  )
}

class LogTable(tag: Tag) extends Table[EventLog](tag, "log") {
  import EventMapper._
  import common.model.SlickColumnMapper._

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def created = column[Instant]("created")

  def userId = column[UserId]("user_id")

  def contentId = column[MovieId]("content_id")

  def event = column[EventType]("event")(eventStateMapper)

  def sessionId = column[SessionId]("session_id")

  override def * = (id, created, userId, contentId, event, sessionId) <> (EventLog.tupled, EventLog.unapply)
}

object Events {
  val events = TableQuery[LogTable]
}
