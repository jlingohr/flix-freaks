package main.scala.recommender.service.interpreter

import domain.{EventType, UserId}
import main.scala.recommender.repository.{EventRepository, RatingRepository}
import main.scala.recommender.service.PopularityRecommenderService

import scala.concurrent.{ExecutionContext, Future}

case class RecommendedItem(movieId: String, numRatings: Int, avgRating: BigDecimal)

class PopularityRecommenderSlickService(ratingRepository: RatingRepository,
                                       logRepository: EventRepository)
                                       (implicit ec: ExecutionContext)
  extends PopularityRecommenderService[BigDecimal, RecommendedItem] {

  override def predictScore(userId: UserId, itemId: String): Future[Option[BigDecimal]] = {
    val avgRating = ratingRepository.getAvgRating(userId, itemId)

    avgRating
  }

  override def recommendItems(userId: UserId, num: Int): Future[Seq[RecommendedItem]] = {
    val popularItems = ratingRepository.getNotRatedBy(userId, num)
    popularItems.map { items =>
      items.map {
        case (id, count, rating) => RecommendedItem(id, count, rating.getOrElse(BigDecimal(0)))
      }
    }
  }

  override def recommendItemsFromLog(num: Int): Future[Seq[(String, Int)]] = {
    val items = logRepository.filterContentByEvent(EventType.apply("play"), num)
    items
  }

  override def recommendItemsByRatings(userId: UserId, activeUserItems: Seq[_], num: Int): Future[Seq[RecommendedItem]] = ???

  override def predictScoreByRatings(itemId: String): Future[Option[BigDecimal]] = {
    val item = ratingRepository.getAvgRatingForItem(itemId)
    item.map(_.getOrElse(BigDecimal(0)))
  }

}

