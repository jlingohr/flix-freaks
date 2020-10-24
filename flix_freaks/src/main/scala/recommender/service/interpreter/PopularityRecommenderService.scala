package main.scala.recommender.service.interpreter

import cats.{Monad, MonadError, ~>}
import cats.implicits._
import common.domain.auth.UserId
import common.domain.events.EventType
import common.domain.ratings.UserRating
import main.scala.recommender.domain.{EventCount, RecommendedItem}
import main.scala.recommender.repository.{EventRepository, RatingRepository}
import main.scala.recommender.service.PopularityRecommenderService

import scala.common.domain.movies.MovieId

class PopularityRecommenderSlickService[F[_], DbEffect[_]](ratingRepository: RatingRepository[DbEffect, UserRating],
                                                           logRepository: EventRepository[DbEffect],
                                                           evalDb: DbEffect ~> F)
                                                          (implicit mMonadError: MonadError[F, Throwable],
                                                           dbEffectMonad: Monad[DbEffect])
  extends PopularityRecommenderService[F, BigDecimal, RecommendedItem, EventCount] {

  override def predictScore(userId: UserId, itemId: MovieId): F[Option[BigDecimal]] = {
    val avgRating = evalDb(ratingRepository.getAvgRating(userId, itemId))

    avgRating
  }

  override def recommendItems(userId: UserId, num: Int): F[Seq[RecommendedItem]] = {
    val popularItems = evalDb(ratingRepository.getNotRatedBy(userId, num))
    popularItems.map { items =>
      items.map {
        case (id, count, rating) => RecommendedItem(id, count, rating.getOrElse(BigDecimal(0)))
      }
    }
  }

  override def recommendItemsFromLog(num: Int): F[Seq[EventCount]] = {
    val items = evalDb(logRepository.filterContentByEvent(EventType.apply("play"), num))
    items
  }

  override def recommendItemsByRatings(userId: UserId, activeUserItems: Seq[_], num: Int): F[Seq[RecommendedItem]] = ???

  override def predictScoreByRatings(itemId: MovieId): F[Option[BigDecimal]] = {
    val item = evalDb(ratingRepository.getAvgRatingForItem(itemId))
    item.map(_.getOrElse(Some(BigDecimal(0))))
  }

}

