package main.scala.recommender.application

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId


trait SimilarityCalculation[F[_], Method, RecommenderResponse, SimilarUsersCalculation] {

  def similarUsers(userId: UserId, method: Method): F[SimilarUsersCalculation]

  def similarContent(contentId: MovieId, take: Int=6): F[RecommenderResponse]

}
