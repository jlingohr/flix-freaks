package main.scala.recommender.application

import domain.UserId

import scala.concurrent.Future

trait PersonalizedCalculation[RecommenderResponse] {

  def recsContentBased(userId: UserId, take: Int=6): Future[RecommenderResponse]

  def recsCF(userId: UserId, take: Int=6): Future[RecommenderResponse]

  def recsSVD(userId: UserId, take: Int = 6): Future[RecommenderResponse]

  def recsFWLS(userId: UserId, take: Int=6): Future[RecommenderResponse]

  def recsBPR(userId: UserId, take: Int=6): Future[RecommenderResponse]

  def recsPopular(userId: UserId, take: Int=60): Future[RecommenderResponse]

}
