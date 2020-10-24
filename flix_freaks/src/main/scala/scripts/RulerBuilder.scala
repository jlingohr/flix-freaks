package main.scala.scripts

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import common.domain.events.Play
import common.domain.recommendations.SeededRecommendation
import main.scala.builder.repository.interpreter.LogSlickQuery
import main.scala.builder.service.interpreter.AssociationRuleBuilderInterpreter
import main.scala.common.db.SlickSupport.dbioTransformation
import main.scala.common.db.{AbstractModel, DatabaseSupportFutureImpl}
import main.scala.common.model.SeededRecs.seededRecs
import slick.backend.DatabaseConfig
import slick.dbio.DBIO
import slick.jdbc.JdbcProfile
import slick.jdbc.PostgresProfile.api._

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object RulerBuilder extends App {

  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

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

  val eventRepository = new LogSlickQuery
  val rulerBuilder = new AssociationRuleBuilderInterpreter

  def buildAssociationRules: Future[Seq[SeededRecommendation]] = {
    val associationRules = for {
      events <- evalDb(eventRepository.queryEvent(Play))
      transactions <- rulerBuilder.generateTransactions(events)
      rules <- rulerBuilder.calculateSupportConfidence(transactions, 0.01)
    } yield rules

    associationRules
  }

  val associationRules = buildAssociationRules
  val doneFuture = associationRules.flatMap { rules =>
    evalDb(
      DBIO.seq(
        seededRecs.schema.create,
        seededRecs ++= rules
      )
    )
  }
  Await.result(doneFuture, Duration.Inf)
  system.terminate()
}
