package main.scala.common.model

import java.time.Instant

import domain._
import slick.jdbc.PostgresProfile.api._
import slick.lifted.Tag


object EventMapper {
  implicit val eventStateMapper = MappedColumnType.base[EventType, String](
    {
      case genreView => "genreView";
      case details => "details";
      case moreDetails => "moreDetails";
      case addToList => "addToList";
      case play => "play";
    },
    {
      case "genreView" => genreView;
      case "details" => details;
      case "moreDetails" => moreDetails;
      case "addToList" => addToList;
      case "play" => play;
    }
  )
}

class LogTable(tag: Tag) extends Table[EventLog](tag, "log") {
  import EventMapper._

  def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

  def created = column[Instant]("created")

  def userId = column[String]("user_id")

  def contentId = column[String]("content_id")

  def event = column[EventType]("event")(eventStateMapper)

  def sessionId = column[String]("session_id")

  override def * = (id, created, userId, contentId, event, sessionId) <> ((EventLog.apply _).tupled, EventLog.unapply)
}

object Events {
  val events = TableQuery[LogTable]
}
