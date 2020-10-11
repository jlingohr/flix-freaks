package main.scala.recommender.application

import domain.UserId

import scala.concurrent.Future

trait AssociationCalculation[AssociationRule, SeededRecommendation] {

  def getAssociationRulesFor(contentId: String, take: Int = 6): Future[Seq[AssociationRule]]

  // Since there is no concept of a 'basket' we will treat items 'bought' together as movies
  // watched in the same session or maybe within some timeframe t (i.e. 1 week).
  def recsUsingAssociationRules(userId: UserId, take: Int = 6): Future[Seq[SeededRecommendation]]

}
