package main.scala.recommender.repository

import domain.UserId

import scala.concurrent.Future

trait RatingRepository[Rating] {

  def filterUser(userId: UserId): Future[Seq[Rating]]

  def filterMoviesIn(movies: Set[String]): Future[Seq[(String, String)]]

  def filterUsersIn(users: Set[String]): Future[Seq[Rating]]

  def getAvgRating(userId: UserId, itemId: String): Future[Option[BigDecimal]]

  def getNotRatedBy(userId: UserId, take: Int): Future[Seq[(String, Int, Option[BigDecimal])]]

  def getAvgRatingForItem(itemId: String): Future[Option[Option[BigDecimal]]]

}
