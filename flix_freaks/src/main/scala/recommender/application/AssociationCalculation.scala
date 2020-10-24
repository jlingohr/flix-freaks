package main.scala.recommender.application


import common.domain.auth.UserId

import scala.common.domain.movies.MovieId
import scala.concurrent.Future

trait AssociationCalculation[F[_], AssociationRule, SeededRecommendation] {

  def getAssociationRulesFor(contentId: MovieId, take: Int = 6): F[Seq[AssociationRule]]

  // Since there is no concept of a 'basket' we will treat items 'bought' together as movies
  // watched in the same session or maybe within some timeframe t (i.e. 1 week).
  def recsUsingAssociationRules(userId: UserId, take: Int = 6): F[Seq[SeededRecommendation]]

}
