package main.scala.recommender.repository

import scala.concurrent.Future

trait SeededRecRepository[Recommendation, SaveResult] {

  def save(rec: Recommendation): Future[SaveResult]

}
