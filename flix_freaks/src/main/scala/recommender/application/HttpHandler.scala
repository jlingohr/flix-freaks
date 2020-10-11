package main.scala.recommender.application

import domain.{Movie, Rating, UserId}
import main.scala.common.domain.SeededRecommendation
import main.scala.recommender.domain.{ChartRecommendation, EventCount, Jaccard, Pearson, RecommendedItem, SimilarUsersCalculation, SimilarityMethod}
import main.scala.recommender.repository.RatingRepository
import main.scala.recommender.service.PopularityRecommenderService
import repository.MovieRepository

import scala.concurrent.{ExecutionContext, Future}
import main.scala.recommender.service.interpreter.SimilarityCalculation._



class HttpHandler(popularityRecommenderService: PopularityRecommenderService[BigDecimal, RecommendedItem, EventCount],
                 movieRepository: MovieRepository,
                 ratingRepository: RatingRepository[Rating])
                 (implicit ec: ExecutionContext)
  extends RestService[SimilarityMethod, Any, ChartRecommendation, SimilarUsersCalculation, SeededRecommendation, RecommendedItem] {

  // TODO should cache this result on a daily basis so as not to always make an expensive call
  override def chart(take: Int): Future[Seq[ChartRecommendation]] = {
    val sortedItems = popularityRecommenderService.recommendItemsFromLog(take)
    val chart = for {
      si <- sortedItems
      ms <- movieRepository.findAllByIds(si.map(_.contentId).toSet)
    } yield buildChart(si, ms)

    chart
  }

  override def similarUsers(userId: UserId, method: SimilarityMethod): Future[SimilarUsersCalculation] = {
    val ratings = ratingRepository.filterUser(userId)
    val similarUsers = for {
      r <- ratings
      simUsers <- ratingRepository.filterMoviesIn(r.map(_.movieId).toSet)
      ds <- ratingRepository.filterUsersIn(simUsers.map(_._2).toSet)
    } yield computeScores(userId, r.length, simUsers, ds, method)

    similarUsers
  }

  override def similarContent(contentId: String, take: Int): Future[Any] = ???

  override def recsContentBased(userId: UserId, take: Int): Future[Any] = ???

  override def recsCF(userId: UserId, take: Int): Future[Any] = ???

  override def recsSVD(userId: UserId, take: Int): Future[Any] = ???

  override def recsFWLS(userId: UserId, take: Int): Future[Any] = ???

  override def recsBPR(userId: UserId, take: Int): Future[Any] = ???

  override def recsPopular(userId: UserId, take: Int): Future[Any] = ???

  def buildChart(items: Seq[EventCount], movies: Seq[Movie]): Seq[ChartRecommendation] = {
    val movieChart = movies.map {
      case movie => (movie.movieId -> movie.title)
    }.toMap
    val sortedItems =
      items
        .map {
          case item => {
            val movieId = item.contentId
            val title = movieChart.getOrElse(movieId, "")
            ChartRecommendation(movieId, title)
          }

        }
    sortedItems
  }

  def computeScores(userId: UserId,
                    numRated: Int,
                    simUsers: Seq[(String, String)],
                    filteredUsers: Seq[Rating],
                    method: SimilarityMethod): SimilarUsersCalculation = {
    // Based on current users' ratings, retrieve all users who also
    // rated one or more of those films
    val users =
      simUsers
        .foldLeft(Map[String, Int]()) {
          case (acc, (movieId, userId)) =>
            val oldVal = acc.getOrElse(userId, 0)
            acc.updated(userId, oldVal + 1)
        }
        .filter(_._2 > 1)
    val allUserIds = users.keySet

    val dataset: Map[String, Map[String, BigDecimal]] = {
      filteredUsers
        .foldLeft(Map[String, Map[String, BigDecimal]]()) {
          case (acc, rating) =>
            if (allUserIds.contains(rating.userId)) {
              val existingValue = acc.getOrElse(rating.userId, Map[String, BigDecimal]())
              val newValue = existingValue + (rating.movieId -> rating.rating)
              acc + (rating.userId -> newValue)
            } else {
              acc
            }
        }
    }

    val similarity: Map[String, BigDecimal] =
      allUserIds
        .foldLeft(Map[String, BigDecimal]()) {
          case (acc, uid) =>
            val s = method match {
              case Jaccard => jaccard(dataset, userId, UserId(uid))
              case Pearson => pearson(dataset, userId, UserId(uid))
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

  override def getAssociationRulesFor(contentId: String, take: Int): Future[Seq[SeededRecommendation]] = ???

  override def recsUsingAssociationRules(userId: UserId, take: Int): Future[Seq[RecommendedItem]] = ???
}



