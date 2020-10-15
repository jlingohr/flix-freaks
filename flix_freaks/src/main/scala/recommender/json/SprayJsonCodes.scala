package main.scala.recommender.json

import java.time.Instant
import java.time.format.DateTimeFormatter

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.UserId
import main.scala.common.domain.SeededRecommendation
import main.scala.recommender.domain.{AssociationRule, ChartRecommendation, Jaccard, Pearson, RecommendedItem, SimilarUsersCalculation, SimilarityMethod}
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}

trait SprayJsonCodes extends SprayJsonSupport with DefaultJsonProtocol {

  implicit val instantFormat: JsonFormat[Instant] =
    new JsonFormat[Instant] {
      private val formatter = DateTimeFormatter.ISO_INSTANT
      override def write(x: Instant): JsValue = JsString(formatter.format(x))

      override def read(value: JsValue): Instant = value match {
        case JsString(x) => Instant.parse(x)
        case x => throw DeserializationException(s"Wrong time format of $x")
      }
    }

  implicit val similarityMethodFormat: JsonFormat[SimilarityMethod] =
    new JsonFormat[SimilarityMethod] {
      def write(obj: SimilarityMethod): JsValue = obj match {
        case Jaccard => JsString("jaccard")
        case Pearson => JsString("pearson")
      }

      override def read(json: JsValue): SimilarityMethod = ???
    }

  implicit val userIdFormat: RootJsonFormat[UserId] = jsonFormat1(UserId)

  implicit val chartRecommendationFormat: RootJsonFormat[ChartRecommendation] = jsonFormat2(ChartRecommendation)
  implicit val seededRecommendationFormat: RootJsonFormat[SeededRecommendation] = jsonFormat6(SeededRecommendation)
  implicit val associationRuleFormat: RootJsonFormat[AssociationRule] = jsonFormat2(AssociationRule)
  implicit val similarUsersFormat: RootJsonFormat[SimilarUsersCalculation] = jsonFormat5(SimilarUsersCalculation)
  implicit val recommendedItemFormat: RootJsonFormat[RecommendedItem] = jsonFormat3(RecommendedItem)

}
