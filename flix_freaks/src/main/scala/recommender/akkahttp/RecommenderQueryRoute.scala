package main.scala.recommender.akkahttp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import domain.UserId
import main.scala.common.domain.SeededRecommendation
import main.scala.recommender.application.RestService
import main.scala.recommender.domain.{AssociationRule, ChartRecommendation, RecommendedItem, SimilarUsersCalculation, SimilarityMethod}
import main.scala.recommender.json.SprayJsonCodes

import scala.concurrent.Future

class RecommenderQueryRoute(handler: RestService[Future, SimilarityMethod, RecommendedItem, ChartRecommendation, SimilarUsersCalculation, SeededRecommendation, AssociationRule])
  extends SprayJsonCodes{

  def routes: Route = {
    val route = concat(
      get {
        pathPrefix("chart" ) {
          val chart = handler.chart()
          onSuccess(chart) { chart =>
            complete(chart)
          }
        }
      },
      get {
        pathPrefix("association_rule" /  """\d+""".r) {contentId =>
          val recs = handler.getAssociationRulesFor(contentId)
          onSuccess(recs) { recs =>
            complete(recs)
          }
        }
      },
      get {
        pathPrefix("ar" /  """\d+""".r) { id =>
          val userId = UserId(id)
          val recs = handler.recsUsingAssociationRules(userId)
          onSuccess(recs) {recs =>
            complete(recs)
          }
        }
      },
      path("sim/user") {
        get {
          parameters("userId", "simMethod") { (id: String, methodType: String) =>
            val userId = UserId(id)
            val method = SimilarityMethod.apply(methodType)
            val simUsers = handler.similarUsers(userId, method)
            onSuccess(simUsers) { users =>
              complete(users)
            }
          }
        }
      },
      path("pop/user/") {
        get {
          parameters("userId") { id: String =>
            val userId = UserId(id)
            val recs = handler.recsPopular(userId)
            onSuccess(recs) { recs =>
              complete(recs)
            }
          }
        }
      }
    )
    route
  }

}
