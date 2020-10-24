package json

import common.json.CommonSprayCodecs
import domain._
import spray.json.RootJsonFormat

trait SprayJsonCodes extends CommonSprayCodecs {

  implicit val genreStatisticsFormat: RootJsonFormat[GenreStatistics] = jsonFormat2(GenreStatistics)
  implicit val movieDTOFormat: RootJsonFormat[MovieDTO] = jsonFormat3(MovieDTO)

  implicit val userAnalyticsFormat: RootJsonFormat[UserAnalytics] = jsonFormat5(UserAnalytics)
  implicit val contentAnalyticsFormat: RootJsonFormat[ContentAnalytics] = jsonFormat7(ContentAnalytics)
}
