package repository

import common.domain.genres.{Genre, GenreId}


trait GenreRepository[F[_]] {

  def findAll: F[Seq[Genre]]

  def create(genre: Genre): F[GenreId]

  def findById(genreId: GenreId): F[Genre]

  def delete(genreId: GenreId): F[Int]

}
