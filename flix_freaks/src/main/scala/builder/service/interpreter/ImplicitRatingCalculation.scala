package main.scala.builder.service.interpreter

import java.time.temporal.TemporalAmount
import java.time.{Duration, Instant, Period}

import domain.{EventLog, EventType, Rating, UserId, details, moreDetails, play}
import main.scala.builder.repository.interpreter.LogSlickQuery
import main.scala.builder.service.ImplicitRatingCalculation

import scala.concurrent.{ExecutionContext, Future}

case class AggregatedUserEvent(userId: String, contentId: String, event: String, count: Int)

// TODO should be able to abstract out properly so not to depend on LogSlickQuery
class ImplicitRatingCalculationInterpreter(w1: Int = 100, w2: Int = 50, w3: Int = 15)
                                          (implicit ec: ExecutionContext)
  extends ImplicitRatingCalculation with LogSlickQuery {

  override def calculateImplicitRatingsForUser(userId: UserId): Future[Map[String, BigDecimal]] = {
    val data = queryAggregatedLogDataForUser(userId)
    val ratings = for {
      d <- data
    } yield buildRatings(d.map(t => AggregatedUserEvent(t._1, t._2, t._3, t._4)))

    ratings
  }

  override def calculateImplicitRatingsWithDecay(userId: UserId): Future[Map[String, BigDecimal]] = {
    val data = queryLogDataForUser(userId)
    val ratings = for {
      d <- data
    } yield buildRatingsWithDecay(d)

    ratings
  }

  def buildRatings(userEvents: Seq[AggregatedUserEvent]): Map[String, BigDecimal] = {
    val aggData = {
      userEvents.foldLeft(Map[String, Map[String, Int]]()) {
        case (acc, event) => {
          val inner = acc.getOrElse(event.contentId, Map[String, Int]()) + (event.event -> event.count)
          acc + (event.contentId -> inner)
        }
      }
    }

    val (maxRating, ratings) =
      aggData.foldLeft((BigDecimal(0), Map[String, BigDecimal]())) {
        case ((maxRating, acc), (contentId, data)) => {
          val sum =
            w1 * data.getOrElse("play", 0) +
              w2 * data.getOrElse("details", 0) +
              w3 * data.getOrElse("moreDetails", 0)
          val rating = BigDecimal(sum)
          val updatedMax = maxRating.max(rating)

          (updatedMax, acc + (contentId -> rating))
        }
      }

    val weightedRatings =
      ratings.map {
        case (k, v) => k -> 10 * v / maxRating
      }

    weightedRatings
  }

  def buildRatingsWithDecay(data: Seq[EventLog]): Map[String, BigDecimal] = {
    val weights: Map[EventType, Int] = Map(
      play -> w1,
      moreDetails -> w2,
      details -> w3
    )

    //TODO this is probably wrong and need to rethink Java Time classes
    val ratings =
      data
        .foldLeft(Map[String, BigDecimal]()) {
          case (acc, event) => {
            val duration = Duration.between(event.created, Instant.now())
            val age = duration.getNano / Instant.now().minus(Duration.ofDays(365)).getNano
            val decay = calculateDelay(age)
            val w = BigDecimal(weights.get(event.event).get)
            val prev = acc.getOrElse(event.contentId, BigDecimal(0))
            acc + (event.contentId -> (prev + w * decay))
          }
        }

    ratings
  }
}

