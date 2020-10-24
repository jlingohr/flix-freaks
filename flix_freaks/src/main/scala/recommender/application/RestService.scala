package main.scala.recommender.application

import common.domain.auth.UserId
import main.scala.recommender.domain.RecommendedItem


trait RestService[F[_], Method, RecommenderResponse, ChartRecommendation, SimilarUsersCalculation, AssociationRule, SeededRecommendation]
  extends SimilarityCalculation[F, Method, RecommenderResponse, SimilarUsersCalculation]
  with AssociationCalculation[F, AssociationRule, SeededRecommendation]
  with PersonalizedCalculation[F, RecommenderResponse] {

  def chart(take: Int=10): F[Seq[ChartRecommendation]]

  def recsPopular(userId: UserId, take: Int=60): F[Seq[RecommendedItem]]

}
