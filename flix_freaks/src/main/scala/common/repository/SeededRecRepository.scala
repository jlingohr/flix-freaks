package main.scala.common.repository

import scala.concurrent.Future

trait SeededRecRepository[Recommendation, SaveResult] {

  def save(rec: Recommendation): Future[SaveResult]

}
