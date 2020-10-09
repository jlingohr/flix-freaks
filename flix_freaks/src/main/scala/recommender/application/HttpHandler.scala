package main.scala.recommender.application

import domain.{Movie, UserId}
import main.scala.recommender.domain.{ChartRecommendation, EventCount, RecommendedItem}
import main.scala.recommender.service.PopularityRecommenderService
import repository.MovieRepository

import scala.concurrent.{ExecutionContext, Future}


class HttpHandler(popularityRecommenderService: PopularityRecommenderService[BigDecimal, RecommendedItem, EventCount],
                 movieRepository: MovieRepository)
                 (implicit ec: ExecutionContext) extends RestService[Any, Any, ChartRecommendation] {

  // TODO should cache this result on a daily basis so as not to always make an expensive call
  override def chart(take: Int): Future[Seq[ChartRecommendation]] = {
    val sortedItems = popularityRecommenderService.recommendItemsFromLog(take)
    val chart = for {
      si <- sortedItems
      ms <- movieRepository.findAllByIds(si.map(_.contentId).toSet)
    } yield buildChart(si, ms)

    chart
  }

  override def similarUsers(userId: UserId, method: Any): Future[Any] = ???

  override def similarContent(contentId: String, take: Int): Future[Any] = ???

  override def recsContentBased(userId: UserId, take: Int): Future[Any] = ???

  override def recsCF(userId: UserId, take: Int): Future[Any] = ???

  override def recsSVD(userId: UserId, take: Int): Future[Any] = ???

  override def recsFWLS(userId: UserId, take: Int): Future[Any] = ???

  override def recsBPR(userId: UserId, take: Int): Future[Any] = ???

  override def recsPopular(userId: UserId, take: Int): Future[Any] = ???

  def buildChart(items: Seq[EventCount], movies: Seq[Movie]) = {
    val ids = items.map(_.contentId)
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
}

