package main.scala.recommender.service

import domain.UserId

import scala.concurrent.Future

trait RecommenderService[Score, RecommendedItem] {

  def predictScore(userId: UserId, itemId: String): Future[Option[Score]]

  def recommendItems(userId: UserId, num: Int = 6): Future[Seq[RecommendedItem]]

}
