package main.scala.flix_freaks.json

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import domain.{Genre, Movie, MovieDetail}
import spray.json.{DefaultJsonProtocol, RootJsonFormat}

trait SprayJsonCodes extends SprayJsonSupport with DefaultJsonProtocol{

  implicit val movieFormat: RootJsonFormat[Movie] = jsonFormat3(Movie)
  implicit val genreFormat: RootJsonFormat[Genre] = jsonFormat2(Genre)
  implicit val movieDetailFormat: RootJsonFormat[MovieDetail] = jsonFormat2(MovieDetail)

}
