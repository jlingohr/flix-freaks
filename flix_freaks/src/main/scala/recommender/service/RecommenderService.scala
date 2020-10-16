package main.scala.recommender.service

import domain.UserId


trait RecommenderService[F[_], Score, RecommendedItem] {

  def predictScore(userId: UserId, itemId: String): F[Option[Score]]

  def recommendItems(userId: UserId, num: Int = 6): F[Seq[RecommendedItem]]

}
