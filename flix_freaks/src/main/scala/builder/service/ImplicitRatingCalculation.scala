package main.scala.builder.service

import domain.UserId

trait ImplicitRatingCalculation[F[_]] {

  def calculateDelay(ageInDays: Long): BigDecimal = 1 / ageInDays

  def calculateImplicitRatingsWithDecay(userId: UserId): F[Map[String, BigDecimal]]

  def calculateImplicitRatingsForUser(userId: UserId): F[Map[String, BigDecimal]]

}
