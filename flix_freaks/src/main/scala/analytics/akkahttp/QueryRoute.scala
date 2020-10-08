package akkahttp

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import analytics.service.AnalyticService
import domain.UserId
import json.SprayJsonCodes

class QueryRoute(service: AnalyticService)(implicit system: ActorSystem[_]) extends SprayJsonCodes {

  def routes: Route = {
    val route = concat (
      get {
        pathPrefix("user" / """\d+""".r) {id =>
          val userId = UserId(id)
          val userAnalytics = service.getUserAnalytics(userId)

          onSuccess(userAnalytics) { item =>
            complete(item)
          }
        }
      }
    )

    route
  }

}
