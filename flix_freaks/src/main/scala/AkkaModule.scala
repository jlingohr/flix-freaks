package scala

import akka.actor.ActorSystem
import cats.implicits.catsStdInstancesForFuture
import com.typesafe.config.Config
import main.scala.common.db.{AbstractModel, DatabaseSupportFutureImpl}
import main.scala.common.db.SlickSupport.dbioTransformation
import main.scala.flix_freaks.service.MovieService
import main.scala.flix_freaks.service.interpreter.MovieServiceInterpreter
import repository.interpreter.{GenreSlickRepository, MovieSlickRepository}
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import com.rms.miu.slickcats.DBIOInstances._
import main.scala.flix_freaks.akkahttp.akkahttp.MovieQueryRoute
import main.scala.recommender.akkahttp.RecommenderQueryRoute
import main.scala.recommender.application.HttpHandler
import main.scala.recommender.repository.interpreter.{EventSlickRepository, RatingSlickRepository, RecommendationSlickRepository}
import main.scala.recommender.service.interpreter.PopularityRecommenderSlickService
import akka.http.scaladsl.server.Directives.concat
import slick.jdbc.JdbcBackend.Database

import scala.concurrent.{ExecutionContext, Future}

class AkkaModule(config: Config)(implicit system: ActorSystem, ec: ExecutionContext) {

  private lazy val localDb = Database.forConfig("database") 

  private lazy val databaseModel = new AbstractModel {
    override val databaseProfile: JdbcProfile = slick.jdbc.PostgresProfile
  }

  private object databaseSupport extends DatabaseSupportFutureImpl[databaseModel.type] {
    override def db: JdbcProfile#Backend#Database = db
    override val model: databaseModel.type = databaseModel
  }
  lazy val evalDb = dbioTransformation(databaseSupport)

  private def initMovieService = {
    val movieRepository = new MovieSlickRepository
    val genreRepository = new GenreSlickRepository
    val movieService: MovieService[Future] = {
      new MovieServiceInterpreter[Future, DBIO](movieRepository, genreRepository, evalDb)
    }
    val movieRoutes = new MovieQueryRoute(movieService)
    movieRoutes
  }

  private def initRecService = {
    val movieRepository = new MovieSlickRepository
    val ratingRepository = new RatingSlickRepository
    val recsRepository = new RecommendationSlickRepository
    val eventRepository = new EventSlickRepository
    val popRecService = new PopularityRecommenderSlickService(ratingRepository, eventRepository, evalDb)
    val recommenderService = new HttpHandler(popRecService, movieRepository, ratingRepository, recsRepository, eventRepository, evalDb)
    val recRoutes = new RecommenderQueryRoute(recommenderService)
    recRoutes
  }

  lazy val routes = concat(initMovieService.routes, initRecService.routes)

  def close = localDb.close()


}

