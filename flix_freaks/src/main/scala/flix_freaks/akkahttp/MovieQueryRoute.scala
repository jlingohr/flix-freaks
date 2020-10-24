package main.scala.flix_freaks.akkahttp

package akkahttp

import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.directives.MethodDirectives.get
import akka.http.scaladsl.server.directives.RouteDirectives.complete
import common.domain.genres.GenreId
import common.json.CommonSprayCodecs
import main.scala.flix_freaks.service.MovieService

import scala.common.domain.movies.MovieId
import scala.concurrent.Future

class MovieQueryRoute(movieService: MovieService[Future]) extends CommonSprayCodecs {

  def routes: Route = {
    val route: Route =
      concat(
        get {
          pathPrefix("movie" / """\d+""".r) { id =>
            // there might be no item for a given id
            val movieDetail = movieService.getMovieDetails(MovieId(id))
            onSuccess(movieDetail) { item =>
              complete(item)
            }
          }
        },
        get {
          pathPrefix("genre" / IntNumber) { id =>
            val movies = movieService.getMoviesByGenre(GenreId(id))

            onSuccess(movies) { items =>
              complete(items)
            }
          }
        }
      )

    route
  }

}

