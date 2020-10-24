package main.scala.builder.repository

import common.domain.auth.UserId
import common.domain.events.EventType
import common.domain.ratings.UserRating

import scala.concurrent.Future

trait RatingRepository[Result] {

  def saveRatings(ratings: Seq[UserRating], userId: UserId, eventType: EventType): Future[Result]

}
