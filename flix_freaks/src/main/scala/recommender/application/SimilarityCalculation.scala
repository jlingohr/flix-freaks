package main.scala.recommender.application

import domain.UserId

trait SimilarityCalculation[F[_], Method, RecommenderResponse, SimilarUsersCalculation] {

  def similarUsers(userId: UserId, method: Method): F[SimilarUsersCalculation]

  def similarContent(contentId: String, take: Int=6): F[RecommenderResponse]

}
