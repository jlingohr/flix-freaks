package main.scala.common.repository

import java.time.Instant

import domain.{EventLog, EventType, addToList, details, genreView, moreDetails, play}
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._


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
