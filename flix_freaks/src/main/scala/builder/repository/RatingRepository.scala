package main.scala.builder.repository

import domain.{EventType, Rating, UserId}

import scala.concurrent.Future

trait RatingRepository[Result] {

  def saveRatings(ratings: Seq[Rating], userId: UserId, eventType: EventType): Future[Result]

}
