package main.scala.recommender.application

import domain.UserId

import scala.concurrent.Future

trait SimilarityCalculation[Method, RecommenderResponse, SimilarUsersCalculation] {

  def similarUsers(userId: UserId, method: Method): Future[SimilarUsersCalculation]

  def similarContent(contentId: String, take: Int=6): Future[RecommenderResponse]

}
