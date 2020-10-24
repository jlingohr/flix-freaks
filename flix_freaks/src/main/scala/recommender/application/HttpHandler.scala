package main.scala.recommender.application

import cats.{Monad, MonadError, ~>}
import main.scala.recommender.domain.{AssociationRule, ChartRecommendation, EventCount, Jaccard, Pearson, RecommendedItem, SimilarUsersCalculation, SimilarityMethod}
import main.scala.recommender.repository.{EventRepository, RatingRepository, RecommendationRepository}
import main.scala.recommender.service.PopularityRecommenderService
import repository.MovieRepository
import cats.implicits._
import common.domain.auth.UserId
import common.domain.ratings.UserRating
import common.domain.recommendations.SeededRecommendation
import main.scala.recommender.service.interpreter.SimilarityCalculation._

import scala.common.domain.movies.{Movie, MovieId, Title}

class HttpHandler[F[_], DbEffect[_]](popularityRecommenderService: PopularityRecommenderService[F, BigDecimal, RecommendedItem, EventCount],
                        movieRepository: MovieRepository[DbEffect],
                        ratingRepository: RatingRepository[DbEffect, UserRating],
                        recsRepository: RecommendationRepository[DbEffect, SeededRecommendation],
                        eventRepository: EventRepository[DbEffect],
                        evalDb: DbEffect ~> F)
                                 (implicit mMonadError: MonadError[F, Throwable],
                                  dbEffectMonad: Monad[DbEffect])
  extends RestService[F, SimilarityMethod, RecommendedItem, ChartRecommendation, SimilarUsersCalculation, SeededRecommendation, AssociationRule] {

  override def getAssociationRulesFor(contentId: MovieId, take: Int): F[Seq[SeededRecommendation]] = {
    evalDb(recsRepository.getBySourceId(contentId, take))
  }

  override def recsUsingAssociationRules(userId: UserId, take: Int): F[Seq[AssociationRule]] = {
    val events = evalDb(eventRepository.getEventsForUser(userId))
    val rules = for {
      e <- events
      r <- evalDb(recsRepository.getBySourceIn(e.take(20).toSet))
    } yield formatRules(r)

    rules
  }

  // TODO should cache this result on a daily basis so as not to always make an expensive call
  override def chart(take: Int): F[Seq[ChartRecommendation]] = {
    val sortedItems = popularityRecommenderService.recommendItemsFromLog(take)
    val chart = for {
      si <- sortedItems
      ms <- evalDb(movieRepository.findAllByIds(si.map(_.contentId).toSet))
    } yield buildChart(si, ms)

    chart
  }

  override def similarUsers(userId: UserId, method: SimilarityMethod): F[SimilarUsersCalculation] = {
    val ratings = evalDb(ratingRepository.filterUser(userId))
    val similarUsers = for {
      r <- ratings
      simUsers <- evalDb(ratingRepository.filterMoviesIn(r.map(_.movieId).toSet))
      ds <- evalDb(ratingRepository.filterUsersIn(simUsers.map(_._2).toSet))
    } yield computeScores(userId, r.length, simUsers, ds, method)

    similarUsers
  }

  override def similarContent(contentId: MovieId, take: Int): F[RecommendedItem] = ???

  override def recsContentBased(userId: UserId, take: Int): F[RecommendedItem] = ???

  override def recsCF(userId: UserId, take: Int): F[RecommendedItem] = ???

  override def recsSVD(userId: UserId, take: Int): F[RecommendedItem] = ???

  override def recsFWLS(userId: UserId, take: Int): F[RecommendedItem] = ???

  override def recsBPR(userId: UserId, take: Int): F[RecommendedItem] = ???

  override def recsPopular(userId: UserId, take: Int=60): F[Seq[RecommendedItem]] = {
    val topNum = popularityRecommenderService.recommendItems(userId, take)
    topNum
  }

  def buildChart(items: Seq[EventCount], movies: Seq[Movie]): Seq[ChartRecommendation] = {
    val movieChart = movies.map(movie => (movie.movieId -> movie.title)).toMap
    val sortedItems =
      items
        .map {
          item => {
            val movieId = item.contentId
            val title = movieChart.getOrElse(movieId, Title(""))
            ChartRecommendation(movieId, title)
          }
        }
    sortedItems
  }

  def computeScores(userId: UserId,
                    numRated: Int,
                    simUsers: Seq[(MovieId, UserId)],
                    filteredUsers: Seq[UserRating],
                    method: SimilarityMethod): SimilarUsersCalculation = {
    // Based on current users' ratings, retrieve all users who also
    // rated one or more of those films
    val users =
      simUsers
        .foldLeft(Map[UserId, Int]()) {
          case (acc, (movieId, userId)) =>
            val oldVal = acc.getOrElse(userId, 0)
            acc.updated(userId, oldVal + 1)
        }
        .filter(_._2 > 1)
    val allUserIds = users.keySet

    val dataset: Map[UserId, Map[MovieId, BigDecimal]] = {
      filteredUsers
        .foldLeft(Map[UserId, Map[MovieId, BigDecimal]]()) {
          case (acc, rating) =>
            if (allUserIds.contains(rating.userId)) {
              val existingValue = acc.getOrElse(rating.userId, Map[MovieId, BigDecimal]())
              val newValue = existingValue + (rating.movieId -> rating.rating)
              acc + (rating.userId -> newValue)
            } else {
              acc
            }
        }
    }

    val similarity: Map[UserId, BigDecimal] =
      allUserIds
        .foldLeft(Map[UserId, BigDecimal]()) {
          case (acc, uid) =>
            val s = method match {
              case Jaccard => jaccard(dataset, userId, uid)
              case Pearson => pearson(dataset, userId, uid)
            }

            if (s > BigDecimal(0.2)) {
              acc + (uid -> s.setScale(2, BigDecimal.RoundingMode.HALF_UP))
            } else {
              acc
            }
        }

    val topN = similarity.map {
      case (k,v) => (k,v)
    }.toSeq
      .sortBy(_._2)
      .reverse
      .take(10)

    SimilarUsersCalculation(userId, numRated, method, topN, topN)
  }

  def formatRules(rules: Seq[(MovieId, Option[BigDecimal])]): Seq[AssociationRule] = {
    rules.map(tup => AssociationRule(tup._1, tup._2))
  }
}





