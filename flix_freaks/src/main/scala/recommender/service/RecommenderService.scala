package main.scala.recommender.service

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId


trait RecommenderService[F[_], Score, RecommendedItem] {

  def predictScore(userId: UserId, itemId: MovieId): F[Option[Score]]

  def recommendItems(userId: UserId, num: Int = 6): F[Seq[RecommendedItem]]

}
