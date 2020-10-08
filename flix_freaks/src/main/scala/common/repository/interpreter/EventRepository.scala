package repository.interpreter

import java.time.Instant

import config.DatabaseConfig
import domain._
import repository.EventRepository
import slick.jdbc.PostgresProfile.api._
import slick.lifted.{TableQuery, Tag}

import scala.concurrent.Future

object EventSlickRepository extends EventRepository with DatabaseConfig {

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

  class LogTable(tag: Tag) extends Table[EventLog](tag, "log") {
    def id = column[Int]("id", O.PrimaryKey, O.AutoInc)

    def created = column[Instant]("created")

    def userId = column[String]("user_id")

    def contentId = column[String]("content_id")

    def event = column[EventType]("event")(eventStateMapper)

    def sessionId = column[String]("session_id")

    override def * = (id, created, userId, contentId, event, sessionId) <> ((EventLog.apply _).tupled, EventLog.unapply)
  }

  val table = TableQuery[LogTable]

  def insert(log: EventLog): Future[Int] = db.run(table += log)

  def getByUserId(userId: UserId): Future[Seq[EventLog]] = db.run(table.filter(_.userId === userId.value).result)

  def getByContentId(contentId: ContentId): Future[Seq[EventLog]] = db.run(table.filter(_.contentId === contentId.value).result)

  def getByEventType(eventType: EventType): Future[Seq[EventLog]] = db.run(table.filter(_.event === eventType).result)
}
