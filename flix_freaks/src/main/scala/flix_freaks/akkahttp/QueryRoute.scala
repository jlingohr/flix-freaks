package main.scala.flix_freaks.akkahttp

package akkahttp

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import app.FlixFreaks.movieService
import main.scala.flix_freaks.json.SprayJsonCodes
import main.scala.flix_freaks.service.interpreter.MovieService

class QueryRoute(service: MovieService)(implicit system: ActorSystem[_]) extends SprayJsonCodes {

  def routes: Route = {
    val route: Route =
      concat(
        get {
          pathPrefix("movie" / """\d+""".r) { id =>
            // there might be no item for a given id
            val movieDetail = movieService.getMovieDetails(id)

            onSuccess(movieDetail) { item =>
              complete(item)
            }
          }
        },
        get {
          pathPrefix("genre" / IntNumber) { id =>
            val movies = movieService.getMoviesByGenre(id)

            onSuccess(movies) { items =>
              complete(items)
            }
          }
        }
      )

    route
  }

}

