package main.scala.flix_freaks.akkahttp

package akkahttp

import akka.actor.typed.ActorSystem
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import main.scala.flix_freaks.json.SprayJsonCodes
import main.scala.flix_freaks.service.MovieService

import scala.concurrent.Future

class MovieQueryRoute(movieService: MovieService[Future]) extends SprayJsonCodes {

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

