package main.scala.builder.service

import domain.{EventLog, EventType, Rating, UserId}

import scala.concurrent.Future

trait ImplicitRatingCalculation {

  def calculateDelay(ageInDays: Long): BigDecimal = 1 / ageInDays

  def calculateImplicitRatingsWithDecay(userId: UserId): Future[Map[String, BigDecimal]]

  def calculateImplicitRatingsForUser(userId: UserId): Future[Map[String, BigDecimal]]

}
