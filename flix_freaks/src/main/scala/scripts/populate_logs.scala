package main.scala.scripts

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import common.domain.auth.UserId
import common.domain.events.{AddToList, Details, EventLog, GenreView, MoreDetails, Play, SessionId}
import main.scala.common.model.LogTable
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import slick.lifted.TableQuery
import spray.json.DefaultJsonProtocol._
import spray.json._

import scala.collection.SortedMap
import scala.common.domain.movies.MovieId
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.util.Random

class User(var sessionId: Int, val userId: String, likes: SortedMap[String, Int], var events: Map[Int, List[String]]) {
  import User._


  def selectFilm(films: SortedMap[String, List[String]]) = {
    val genre = sample(this.likes)
    val interestedFilms = films.getOrElse(genre, films("action"))
    var filmId = ""
    while (filmId.equals("")) {
      val filmCandidate = interestedFilms(Random.between(0, interestedFilms.length-1))
      if (!this.events(this.sessionId).contains(filmCandidate)) {
        filmId = filmCandidate
      }
    }
    filmId
  }

  def selectGenre() = {
    sample(this.likes)
  }

  def addEvent(selectedFilm: String) = {
    this.events.updated(this.sessionId, this.events(this.sessionId).appended(selectedFilm))
  }

  def getSessionId = {
    if (Random.between(0, 100) > 90) {
      this.sessionId += 1
      this.events = this.events + (this.sessionId -> List.empty)
    }
    this.sessionId
  }
}

object User {
  def apply(userId: String, action: Int, drama: Int, comedy: Int): User = {
    val sessionId = Random.between(0, 1000000)
    val likes: SortedMap[String, Int] = SortedMap(
      "action" -> action,
      "drama" -> drama,
      "comedy" -> comedy
    )
    val events = Map(
      sessionId -> List.empty
    )

    new User(sessionId, userId, likes, events)
  }

  def selectAction = {
    val actions = SortedMap(
      "genreView" -> 15,
      "details" -> 50,
      "moreDetails" -> 24,
      "addToList" -> 10,
      "play" -> 1
    )

    sample(actions) match {
      case "genreView" => GenreView
      case "details" => Details
      case "moreDetails" => MoreDetails
      case "addToList" => AddToList
      case "play" => Play
    }
  }

  def sample(dict: SortedMap[String, Int]): String = {
    val p = Random.nextDouble() * dict.values.sum
    val it = dict.iterator
    var accum = 0.0
    while (it.hasNext) {
      val (item, itemProb) = it.next
      accum += itemProb
      if (accum >= p)
        return item
    }
    sys.error(f"this should not happend")
  }

}

object populate_logs extends App {

  import User._

  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val db = Database.forConfig("database")
  val profile = slick.jdbc.PostgresProfile
  val session = SlickSession.forDbAndProfile(db, profile)

  val table = TableQuery[LogTable]

  val source = scala.io.Source.fromFile("collector/src/main/scala/scripts/logs.json")
  val f = try source.mkString.parseJson.convertTo[Map[String, List[String]]] finally source.close()
  val films = SortedMap[String, List[String]]() ++ f
  val users = List(
    User("400001", 20, 30, 50),
    User("400002", 50, 20, 40),
    User("400003", 20, 30, 50),
    User("400004", 100, 0, 0),
    User("400005", 0, 100, 0),
    User("400006", 0, 0, 100),
  )

  val numberOfEvents = 100000
  println("Generating data")

  val logs = (0 to numberOfEvents).map { x =>
    val randomUserId = Random.between(0, users.length-1)
    val user = users(randomUserId)
    val selectedFilm = user.selectFilm(films)
    val action = selectAction

    if (action == Play) {
      user.addEvent(selectedFilm)
    }

    EventLog(0, java.time.Instant.now(), UserId(user.userId), MovieId(selectedFilm), action, SessionId(user.getSessionId.toString))
  }

  val done = db.run(table ++= logs)
  Await.result(done, Duration.Inf)
  db.close()
  system.terminate()


}
