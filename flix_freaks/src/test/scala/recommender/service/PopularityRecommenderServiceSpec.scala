package test.scala.recommender.service

import main.scala.recommender.domain.{EventCount, RecommendedItem}
import main.scala.recommender.repository.{EventRepository, RatingRepository}
import main.scala.recommender.service.RecommenderService
import main.scala.recommender.service.interpreter.PopularityRecommenderSlickService
import org.mockito.MockitoSugar.{mock, when}
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should
import cats.implicits._
import cats.{Eval, MonadError}
import cats.arrow.FunctionK
import domain.UserId
import org.mockito.ArgumentMatchers._
import org.mockito.cats.MockitoCats.whenF

import scala.concurrent.Future
import scala.util.{Success, Try}
import scala.concurrent.ExecutionContext.Implicits.global

import org.mockito.invocation.InvocationOnMock
import org.mockito.{ ArgumentMatchersSugar, IdiomaticMockito }



class PopularityRecommenderServiceSpec extends AnyFunSpec with IdiomaticMockito {

  private val ratingRepository = mock[RatingRepository[Future, BigDecimal]]
  private val logRepository = mock[EventRepository[Future]]

  private val recommenderService: RecommenderService[Future, BigDecimal, RecommendedItem] =
    new PopularityRecommenderSlickService[Future, Future](ratingRepository, logRepository, FunctionK.id[Future])

  val userId: UserId = UserId("asdf")
  val itemId: String = "asdf"
  val take = 3

  describe("Predicting user scores") {
    it("Should give None when the content does not exists") {
      when(ratingRepository.getAvgRating(userId, itemId)) thenReturn Future.successful(None)

      recommenderService.predictScore(userId, itemId)
        .map(value => value.isEmpty)
    }

    it("Should give an average rating of 5") {
      when(ratingRepository.getAvgRating(userId, itemId)) thenReturn Future.successful(Some(BigDecimal(5.0)))

      recommenderService.predictScore(userId, itemId)
        .map(value => value.equals(Some(BigDecimal(5.0))))
    }
  }

  describe("Recommending unrated items to user") {
    it("Should return no items if user has rated all items") {
      when(ratingRepository.getNotRatedBy(userId, take)) thenReturn Future.successful(Seq.empty)

      recommenderService.recommendItems(userId, take)
        .map(_.isEmpty)
    }

    it("Should return no items if no items rated") {
      val values = Seq(
        ("A", 5, Some(BigDecimal(5.0))),
        ("B", 4, Some(BigDecimal(4.0))),
        ("C", 0, None)
      )
      val expected = Seq(
        RecommendedItem("A", 5, BigDecimal(5.0)),
        RecommendedItem("B", 4, BigDecimal(5.0)),
        RecommendedItem("C", 0, BigDecimal(5.0))
      )

      when(ratingRepository.getNotRatedBy(userId, take)) thenReturn Future.successful(values)

      recommenderService.recommendItems(userId, take)
        .map(v => v.equals(expected))
    }
  }

}
