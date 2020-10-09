package main.scala.recommender.service

import domain.UserId

import scala.concurrent.Future

trait PopularityRecommenderService[Score, RecommendedItem, EventCount] extends RecommenderService[Score, RecommendedItem] {

  def recommendItemsFromLog(num: Int = 6): Future[Seq[EventCount]]

  def recommendItemsByRatings(userId: UserId, activeUserItems: Seq[_], num: Int = 6): Future[Seq[RecommendedItem]]

  def predictScoreByRatings(itemId: String): Future[Option[Score]]

}
