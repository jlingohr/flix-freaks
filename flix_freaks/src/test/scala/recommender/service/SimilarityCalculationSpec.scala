package recommender.service

import common.domain.auth.UserId
import main.scala.recommender.service.interpreter.SimilarityCalculation
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

import scala.common.domain.movies.MovieId

class SimilarityCalculationSpec extends AnyFunSpec {

  val userA = UserId("userA")
  val userB = UserId("userB")

  describe("Computing Jaccard score") {
    val userARatings: Map[MovieId, BigDecimal] = Map(
      MovieId("movieA") -> BigDecimal(4),
      MovieId("movieB") -> BigDecimal(5),
      MovieId("movieC") -> BigDecimal(4),
      MovieId("movieE") -> BigDecimal(3),
      MovieId("movieF") -> BigDecimal(3)
    )

    val userBRatings: Map[MovieId, BigDecimal] = Map(
      MovieId("movieA") -> BigDecimal(3),
      MovieId("movieB") -> BigDecimal(3),
      MovieId("movieC") -> BigDecimal(3),
      MovieId("movieD") -> BigDecimal(2),
      MovieId("movieE") -> BigDecimal(4),
      MovieId("movieF") -> BigDecimal(5)
    )

    it("Should return 0 if thisUser is not in dataset") {
      val users: Map[UserId, Map[MovieId, BigDecimal]] = Map(
        userB -> userBRatings
      )
      val result = SimilarityCalculation.pearson(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return 0 if thatUser is not in dataset") {
      val users: Map[UserId, Map[MovieId, BigDecimal]] = Map(
        userA -> userARatings
      )
      val result = SimilarityCalculation.pearson(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return -0.76 otherwise") {
      val users: Map[UserId, Map[MovieId, BigDecimal]] = Map(
        userA -> userARatings,
        userB -> userBRatings
      )
      val result = SimilarityCalculation.pearson(users, userA, userB)
      result.setScale(2, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(-0.76)
    }
  }

  describe("Computing Pearson score") {
    val userARatings: Map[MovieId, BigDecimal] = Map(
      MovieId("movieA") -> BigDecimal(4),
      MovieId("movieB") -> BigDecimal(5),
    )

    val userBRatings: Map[MovieId, BigDecimal] = Map(
      MovieId("movieA") -> BigDecimal(3),
      MovieId("movieB") -> BigDecimal(3),
      MovieId("movieC") -> BigDecimal(3),
    )

    it("Should return 0 if thisUser not in dataset") {
      val users: Map[UserId, Map[MovieId, BigDecimal]] = Map(
        userB -> userBRatings
      )
      val result = SimilarityCalculation.jaccard(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return 0 if thatUser not in dataset") {
      val users: Map[UserId, Map[MovieId, BigDecimal]] = Map(
        userA -> userARatings
      )
      val result = SimilarityCalculation.jaccard(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return 0.67 otherwise") {
      val users: Map[UserId, Map[MovieId, BigDecimal]] = Map(
        userA -> userARatings,
        userB -> userBRatings
      )
      val result = SimilarityCalculation.jaccard(users, userA, userB)
      result.setScale(2, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(0.67)
    }
  }

}
