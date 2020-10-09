package main.scala.recommender.application

import domain.UserId

import scala.concurrent.Future

trait SimilarityCalculation[Method, RecommenderResponse] {

  def similarUsers(userId: UserId, method: Method): Future[RecommenderResponse]

  def similarContent(contentId: String, take: Int=6): Future[RecommenderResponse]

}
