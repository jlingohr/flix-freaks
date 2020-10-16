package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import cats.implicits.catsStdInstancesForFuture
import com.typesafe.config.ConfigFactory
import main.scala.common.db.SlickSupport.dbioTransformation
import main.scala.common.db.{AbstractModel, DatabaseSupportFutureImpl}
import main.scala.flix_freaks.akkahttp.akkahttp.QueryRoute
import main.scala.flix_freaks.service.interpreter.MovieServiceInterpreter
import repository.interpreter.{GenreSlickRepository, MovieSlickRepository}
import slick.backend.DatabaseConfig
import slick.jdbc.JdbcProfile

import scala.concurrent.Future
import scala.io.StdIn
import main.scala.flix_freaks.service.MovieService
import slick.dbio.DBIO
import com.rms.miu.slickcats.DBIOInstances._

object FlixFreaks extends App {

  implicit val system = ActorSystem(Behaviors.empty,"flix-freaks")
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

  val movieRepository = new MovieSlickRepository
  val genreRepository = new GenreSlickRepository
  val movieService: MovieService[Future] =
    new MovieServiceInterpreter[Future, DBIO](movieRepository, genreRepository, evalDb)
  val routes = new QueryRoute(movieService)

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(routes.routes)

  println(s"Server online at http://localhost:8080/\n Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
