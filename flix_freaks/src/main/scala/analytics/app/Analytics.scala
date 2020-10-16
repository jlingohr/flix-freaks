package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akkahttp.QueryRoute
import analytics.service.AnalyticService
import cats.implicits.catsStdInstancesForFuture
import com.typesafe.config.ConfigFactory
import main.scala.common.db.{AbstractModel, DatabaseSupportFutureImpl}
import main.scala.common.db.SlickSupport.dbioTransformation
import repository.interpreter.{EventSlickRepository, GenreSlickRepository, MovieSlickRepository, RatingSlickRepository}
import service.interpretor.AnalyticServiceInterpreter
import slick.backend.DatabaseConfig
import slick.jdbc.JdbcProfile
import com.rms.miu.slickcats.DBIOInstances._
import slick.dbio.DBIO

import scala.concurrent.Future
import scala.io.StdIn


object Analytics extends App {
  implicit val system: ActorSystem[Nothing] = ActorSystem(Behaviors.empty,"flix-analytics")
  implicit val executionContext = system.executionContext

  private val config = ConfigFactory.load()
  private val databaseConfig = config.getConfig("database")
  private lazy val localDb = DatabaseConfig.forConfig[JdbcProfile]("database")

  private lazy val databaseModel = new AbstractModel {
    override val databaseProfile: JdbcProfile = localDb.profile
  }

  private object databaseSupport extends DatabaseSupportFutureImpl[databaseModel.type] {
    override def db: JdbcProfile#Backend#Database = db
    override val model: databaseModel.type = databaseModel
  }
  lazy val evalDb = dbioTransformation(databaseSupport)

  val ratingRepository = new RatingSlickRepository
  val movieRepository = new MovieSlickRepository
  val eventRepository = new EventSlickRepository
  val genreRepository = new GenreSlickRepository
  val service: AnalyticService[Future] =
    new AnalyticServiceInterpreter[Future, DBIO](ratingRepository, movieRepository, eventRepository, genreRepository, evalDb)

  val routes = new QueryRoute(service)

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes.routes)

  println(s"Server online at http://localhost:8080/\n Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())

}
