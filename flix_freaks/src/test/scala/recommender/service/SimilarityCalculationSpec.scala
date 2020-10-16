package recommender.service

import domain.UserId
import main.scala.recommender.service.interpreter.SimilarityCalculation
import org.scalatest.funspec.AnyFunSpec
import org.scalatest.matchers.should.Matchers.convertToAnyShouldWrapper

class SimilarityCalculationSpec extends AnyFunSpec {

  val userA = UserId("userA")
  val userB = UserId("userB")

  describe("Computing Jaccard score") {
    val userARatings: Map[String, BigDecimal] = Map(
      "movieA" -> BigDecimal(4),
      "movieB" -> BigDecimal(5),
      "movieC" -> BigDecimal(4),
      "movieE" -> BigDecimal(3),
      "movieF" -> BigDecimal(3)
    )

    val userBRatings: Map[String, BigDecimal] = Map(
      "movieA" -> BigDecimal(3),
      "movieB" -> BigDecimal(3),
      "movieC" -> BigDecimal(3),
      "movieD" -> BigDecimal(2),
      "movieE" -> BigDecimal(4),
      "movieF" -> BigDecimal(5)
    )

    it("Should return 0 if thisUser is not in dataset") {
      val users: Map[String, Map[String, BigDecimal]] = Map(
        userB.value -> userBRatings
      )
      val result = SimilarityCalculation.pearson(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return 0 if thatUser is not in dataset") {
      val users: Map[String, Map[String, BigDecimal]] = Map(
        userA.value -> userARatings
      )
      val result = SimilarityCalculation.pearson(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return -0.76 otherwise") {
      val users: Map[String, Map[String, BigDecimal]] = Map(
        userA.value -> userARatings,
        userB.value -> userBRatings
      )
      val result = SimilarityCalculation.pearson(users, userA, userB)
      result.setScale(2, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(-0.76)
    }
  }

  describe("Computing Pearson score") {
    val userARatings: Map[String, BigDecimal] = Map(
      "movieA" -> BigDecimal(4),
      "movieB" -> BigDecimal(5),
    )

    val userBRatings: Map[String, BigDecimal] = Map(
      "movieA" -> BigDecimal(3),
      "movieB" -> BigDecimal(3),
      "movieC" -> BigDecimal(3),
    )

    it("Should return 0 if thisUser not in dataset") {
      val users: Map[String, Map[String, BigDecimal]] = Map(
        userB.value -> userBRatings
      )
      val result = SimilarityCalculation.jaccard(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return 0 if thatUser not in dataset") {
      val users: Map[String, Map[String, BigDecimal]] = Map(
        userA.value -> userARatings
      )
      val result = SimilarityCalculation.jaccard(users, userA, userB)
      result shouldEqual BigDecimal(0)
    }

    it("Should return 0.67 otherwise") {
      val users: Map[String, Map[String, BigDecimal]] = Map(
        userA.value -> userARatings,
        userB.value -> userBRatings
      )
      val result = SimilarityCalculation.jaccard(users, userA, userB)
      result.setScale(2, BigDecimal.RoundingMode.HALF_UP) shouldEqual BigDecimal(0.67)
    }
  }

}
