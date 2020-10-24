package main.scala.common.repository

import scala.concurrent.Future

trait SeededRecRepository[F[_], Recommendation, SaveResult] {

  def save(rec: Recommendation): F[SaveResult]

}
