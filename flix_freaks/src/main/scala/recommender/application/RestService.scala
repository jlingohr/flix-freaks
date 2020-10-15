package main.scala.recommender.application

import domain.UserId
import main.scala.recommender.domain.RecommendedItem

import scala.concurrent.Future


trait RestService[Method, RecommenderResponse, ChartRecommendation, SimilarUsersCalculation, AssociationRule, SeededRecommendation]
  extends SimilarityCalculation[Method, RecommenderResponse, SimilarUsersCalculation]
  with AssociationCalculation[AssociationRule, SeededRecommendation]
  with PersonalizedCalculation[RecommenderResponse] {

  def chart(take: Int=10): Future[Seq[ChartRecommendation]]

  def recsPopular(userId: UserId, take: Int=60): Future[Seq[RecommendedItem]]

}
