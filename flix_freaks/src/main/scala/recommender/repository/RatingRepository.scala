package main.scala.recommender.repository

import domain.UserId

import scala.concurrent.Future

trait RatingRepository {

  def getAvgRating(userId: UserId, itemId: String): Future[Option[BigDecimal]]

  def getNotRatedBy(userId: UserId, take: Int): Future[Seq[(String, Int, Option[BigDecimal])]]

  def getAvgRatingForItem(itemId: String): Future[Option[Option[BigDecimal]]]

}
