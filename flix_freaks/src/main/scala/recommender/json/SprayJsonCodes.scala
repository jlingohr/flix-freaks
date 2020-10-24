package main.scala.recommender.json

import common.domain.recommendations.SeededRecommendation
import common.json.CommonSprayCodecs
import main.scala.recommender.domain._
import spray.json.{JsString, JsValue, JsonFormat, RootJsonFormat}

trait SprayJsonCodes extends CommonSprayCodecs {

  implicit val similarityMethodFormat: JsonFormat[SimilarityMethod] =
    new JsonFormat[SimilarityMethod] {
      def write(obj: SimilarityMethod): JsValue = obj match {
        case Jaccard => JsString("jaccard")
        case Pearson => JsString("pearson")
      }

      override def read(json: JsValue): SimilarityMethod = ???
    }

  implicit val chartRecommendationFormat: RootJsonFormat[ChartRecommendation] = jsonFormat2(ChartRecommendation)
  implicit val seededRecommendationFormat: RootJsonFormat[SeededRecommendation] = jsonFormat6(SeededRecommendation)
  implicit val associationRuleFormat: RootJsonFormat[AssociationRule] = jsonFormat2(AssociationRule)
  implicit val similarUsersFormat: RootJsonFormat[SimilarUsersCalculation] = jsonFormat5(SimilarUsersCalculation)
  implicit val recommendedItemFormat: RootJsonFormat[RecommendedItem] = jsonFormat3(RecommendedItem)

}
