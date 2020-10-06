package app

import akka.actor.typed.ActorSystem
import akka.actor.typed.scaladsl.Behaviors
import akka.http.scaladsl.Http
import akka.Done
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes
import domain.{Genre, Movie, MovieDetail}
import services.MovieService
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, JsonFormat, RootJsonFormat}
// for JSON serialization/deserialization following dependency is required:
// "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.7"
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._

import scala.io.StdIn

import scala.concurrent.Future

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat3(Movie)
  implicit val genreFormat: RootJsonFormat[Genre] = jsonFormat2(Genre)
  implicit val movieDetailFormat: RootJsonFormat[MovieDetail] = jsonFormat2(MovieDetail)
}

object FlixFreaks extends App with JsonSupport {
  implicit val system = ActorSystem(Behaviors.empty,"flix-freaks")
  implicit val executionContext = system.executionContext

  val movieService = new MovieService()

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

  val bindingFuture = Http().newServerAt("localhost", 8080).bind(route)

  println(s"Server online at http://localhost:8080/\n Press RETURN to stop...")
  StdIn.readLine()
  bindingFuture
    .flatMap(_.unbind())
    .onComplete(_ => system.terminate())
}
