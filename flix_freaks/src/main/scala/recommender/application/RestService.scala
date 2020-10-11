package main.scala.recommender.application

import scala.concurrent.Future


trait RestService[Method, RecommenderResponse, ChartRecommendation, SimilarUsersCalculation, AssociationRule, SeededRecommendation]
  extends SimilarityCalculation[Method, RecommenderResponse, SimilarUsersCalculation]
  with AssociationCalculation[AssociationRule, SeededRecommendation]
  with PersonalizedCalculation[RecommenderResponse] {

  def chart(take: Int=10): Future[Seq[ChartRecommendation]]

}
