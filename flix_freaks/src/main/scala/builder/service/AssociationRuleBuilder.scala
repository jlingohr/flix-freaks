package main.scala.builder.service

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

trait AssociationRuleBuilder[PlayEvent, GenerateTransactions, AssociationRule] {

  def retrievePlayEvents: Future[Seq[PlayEvent]]
  def generateTransactions(data: Seq[PlayEvent]): Future[GenerateTransactions]
  def calculateSupportConfidence(data: GenerateTransactions, confidence: Double): Future[Seq[AssociationRule]]

  def buildAssociationRules: Future[Seq[AssociationRule]] = {
    val associationRules = for {
      events <- retrievePlayEvents
      transactions <- generateTransactions(events)
      rules <- calculateSupportConfidence(transactions, 0.01)
    } yield rules

    associationRules
  }

}
