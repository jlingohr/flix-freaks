package main.scala.recommender.repository

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId


trait RatingRepository[F[_], Rating] {

  def filterUser(userId: UserId): F[Seq[Rating]]

  def filterMoviesIn(movies: Set[MovieId]): F[Seq[(MovieId, UserId)]]

  def filterUsersIn(users: Set[UserId]): F[Seq[Rating]]

  def getAvgRating(userId: UserId, itemId: MovieId): F[Option[BigDecimal]]

  def getNotRatedBy(userId: UserId, take: Int): F[Seq[(MovieId, Int, Option[BigDecimal])]]

  def getAvgRatingForItem(itemId: MovieId): F[Option[Option[BigDecimal]]]

}
