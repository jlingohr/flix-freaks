package main.scala.builder.service.interpreter

import java.time.{Duration, Instant}

import cats.{Monad, MonadError, ~>}
import main.scala.builder.repository.LogQuery
import main.scala.builder.service.ImplicitRatingCalculation
import cats.implicits._
import common.domain.auth.UserId
import common.domain.events.{AggregatedUserEvent, Details, EventLog, EventType, MoreDetails, Play}

import scala.common.domain.movies.MovieId



// TODO should be able to abstract out properly so not to depend on LogSlickQuery
class ImplicitRatingCalculationInterpreter[F[_], DbEffect[_]](eventRepository: LogQuery[DbEffect, EventLog],
                                                              w1: Int = 100,
                                                              w2: Int = 50,
                                                              w3: Int = 15,
                                                             evalDb: DbEffect ~> F)
                                                             (implicit mMonadError: MonadError[F, Throwable],
                                                              dbEffectMonad: Monad[DbEffect])
  extends ImplicitRatingCalculation[F] {

  override def calculateImplicitRatingsForUser(userId: UserId): F[Map[MovieId, BigDecimal]] = {
    val data = evalDb(eventRepository.queryAggregatedLogDataForUser(userId))
    val ratings = for {
      d <- data
    } yield buildRatings(d.map(t => AggregatedUserEvent(t._1, t._2, t._3, t._4)))

    ratings
  }

  override def calculateImplicitRatingsWithDecay(userId: UserId): F[Map[MovieId, BigDecimal]] = {
    val data = evalDb(eventRepository.queryLogDataForUser(userId))
    val ratings = for {
      d <- data
    } yield buildRatingsWithDecay(d)

    ratings
  }

  def buildRatings(userEvents: Seq[AggregatedUserEvent]): Map[MovieId, BigDecimal] = {
    val aggData = {
      userEvents.foldLeft(Map[MovieId, Map[EventType, Int]]()) {
        case (acc, event) =>
          val inner = acc.getOrElse(event.contentId, Map[EventType, Int]()) + (event.event -> event.count)
          acc + (event.contentId -> inner)
      }
    }

    val (maxRating, ratings) =
      aggData.foldLeft((BigDecimal(0), Map[MovieId, BigDecimal]())) {
        case ((maxRating, acc), (contentId, data)) =>
          val sum =
            w1 * data.getOrElse(Play, 0) +
              w2 * data.getOrElse(Details, 0) +
              w3 * data.getOrElse(MoreDetails, 0)
          val rating = BigDecimal(sum)
          val updatedMax = maxRating.max(rating)

          (updatedMax, acc + (contentId -> rating))
      }

    val weightedRatings =
      ratings.map {
        case (k, v) => k -> 10 * v / maxRating
      }

    weightedRatings
  }

  def buildRatingsWithDecay(data: Seq[EventLog]): Map[MovieId, BigDecimal] = {
    val weights: Map[EventType, Int] = Map(
      Play -> w1,
      MoreDetails -> w2,
      Details -> w3
    )

    //TODO this is probably wrong and need to rethink Java Time classes
    val ratings =
      data
        .foldLeft(Map[MovieId, BigDecimal]()) {
          case (acc, event) =>
            val duration = Duration.between(event.created, Instant.now())
            val age = duration.getNano / Instant.now().minus(Duration.ofDays(365)).getNano
            val decay = calculateDelay(age)
            val w = BigDecimal(weights(event.event))
            val prev = acc.getOrElse(event.contentId, BigDecimal(0))
            acc + (event.contentId -> (prev + w * decay))
        }

    ratings
  }
}

