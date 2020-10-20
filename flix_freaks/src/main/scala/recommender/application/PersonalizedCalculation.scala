package main.scala.recommender.application

import domain.UserId


trait PersonalizedCalculation[F[_], RecommenderResponse] {

  def recsContentBased(userId: UserId, take: Int=6): F[RecommenderResponse]

  def recsCF(userId: UserId, take: Int=6): F[RecommenderResponse]

  def recsSVD(userId: UserId, take: Int = 6): F[RecommenderResponse]

  def recsFWLS(userId: UserId, take: Int=6): F[RecommenderResponse]

  def recsBPR(userId: UserId, take: Int=6): F[RecommenderResponse]

}
