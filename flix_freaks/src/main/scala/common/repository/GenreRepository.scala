package repository

import domain.Genre

import scala.concurrent.Future

trait GenreRepository {

  def findAll: Future[Seq[Genre]]

  def create(genre: Genre): Future[Int]

  def findById(genreId: Int): Future[Genre]

  def delete(genreId: Int): Future[Int]

}
