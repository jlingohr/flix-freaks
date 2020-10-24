package main.scala.recommender.service

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId


trait PopularityRecommenderService[F[_], Score, RecommendedItem, EventCount]
  extends RecommenderService[F, Score, RecommendedItem] {

  def recommendItemsFromLog(num: Int = 6): F[Seq[EventCount]]

  def recommendItemsByRatings(userId: UserId, activeUserItems: Seq[_], num: Int = 6): F[Seq[RecommendedItem]]

  def predictScoreByRatings(itemId: MovieId): F[Option[Score]]

}
