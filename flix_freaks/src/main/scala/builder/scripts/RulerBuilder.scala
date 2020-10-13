package main.scala.builder.scripts

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.alpakka.slick.scaladsl.SlickSession
import main.scala.builder.service.interpreter.AssociationRuleBuilder._
import slick.jdbc.JdbcBackend.Database
import slick.jdbc.PostgresProfile.api._
import main.scala.common.model.SeededRecs._

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object RulerBuilder extends App {

  implicit val system = ActorSystem()
  implicit val executionContext = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val db = Database.forConfig("database")
  val profile = slick.jdbc.PostgresProfile
  val session = SlickSession.forDbAndProfile(db, profile)

  val associationRules = buildAssociationRules
  val doneFuture = associationRules.flatMap { rules =>
    db.run(
      DBIO.seq(
//      seededRecs.schema.create,
      seededRecs ++= rules
      )
    )
  }
  Await.result(doneFuture, Duration.Inf)
  system.terminate()
}
