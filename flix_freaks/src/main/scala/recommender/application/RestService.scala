package main.scala.recommender.application

import scala.concurrent.Future


trait RestService[Method, RecommenderResponse, ChartRecommendation]
  extends SimilarityCalculation[Method, RecommenderResponse]
  with PersonalizedCalculation[RecommenderResponse] {

  def chart(take: Int=10): Future[Seq[ChartRecommendation]]

}
