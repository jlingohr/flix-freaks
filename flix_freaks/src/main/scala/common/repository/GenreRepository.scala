package repository

import domain.Genre

trait GenreRepository[F[_]] {

  def findAll: F[Seq[Genre]]

  def create(genre: Genre): F[Int]

  def findById(genreId: Int): F[Genre]

  def delete(genreId: Int): F[Int]

}
