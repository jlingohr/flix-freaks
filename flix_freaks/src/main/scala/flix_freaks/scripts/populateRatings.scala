package scripts

import java.nio.file.Paths
import java.time.Instant

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl._
import akka.stream.scaladsl.{Sink, Source}
import domain.Rating
import main.scala.common.repository.RatingTable
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.language.postfixOps


object populateRatings extends App {
  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val db = Database.forConfig("database")
  val profile = slick.jdbc.PostgresProfile
  val session = SlickSession.forDbAndProfile(db, profile)
  val ratingsTable = TableQuery[RatingTable]

  val uri = "src/main/resources/ratings.dat"

  def parseRating(res: String): Rating = {
    val r = res.split("::")
    val userId = r(0)
    val movieId = r(1)
    val rating = r(2)
    val timestamp = r(3)

    Rating(userId, movieId, rating.toFloat, Instant.ofEpochMilli(timestamp.toLong * 1000))
  }

  def batchInsert(ratings: Seq[Rating]) = db run (ratingsTable ++= ratings)

  val insertSink = Sink.foreach(batchInsert)

  val path = Paths.get(uri).toAbsolutePath
  val getLines = () => scala.io.Source.fromFile(path.toString).getLines()

  val readLines = Source.fromIterator(getLines)
    .filter(_.split("::").length == 4)
    .map(l => parseRating(l))

  val streamFuture = readLines.mapAsync(1) { rating =>
    db.run(ratingsTable += rating)
  }.runWith(Sink.ignore)
  Await.result(streamFuture, Duration.Inf)
}
