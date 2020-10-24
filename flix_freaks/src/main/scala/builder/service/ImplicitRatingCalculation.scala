package main.scala.builder.service

import common.domain.auth.UserId

import scala.common.domain.movies.MovieId


trait ImplicitRatingCalculation[F[_]] {

  def calculateDelay(ageInDays: Long): BigDecimal = 1 / ageInDays

  def calculateImplicitRatingsWithDecay(userId: UserId): F[Map[MovieId, BigDecimal]]

  def calculateImplicitRatingsForUser(userId: UserId): F[Map[MovieId, BigDecimal]]

}
