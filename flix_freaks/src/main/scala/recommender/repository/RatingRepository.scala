package main.scala.recommender.repository

import domain.UserId

import scala.concurrent.Future

trait RatingRepository[F[_], Rating] {

  def filterUser(userId: UserId): F[Seq[Rating]]

  def filterMoviesIn(movies: Set[String]): F[Seq[(String, String)]]

  def filterUsersIn(users: Set[String]): F[Seq[Rating]]

  def getAvgRating(userId: UserId, itemId: String): F[Option[BigDecimal]]

  def getNotRatedBy(userId: UserId, take: Int): F[Seq[(String, Int, Option[BigDecimal])]]

  def getAvgRatingForItem(itemId: String): F[Option[Option[BigDecimal]]]

}
