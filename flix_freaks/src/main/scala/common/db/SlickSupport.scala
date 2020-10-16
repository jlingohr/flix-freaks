package main.scala.common.db

import slick.jdbc.JdbcProfile

import scala.concurrent.Future

trait AbstractModel {
  val databaseProfile: JdbcProfile
}

trait DatabaseSupport[F[_], +Model <: AbstractModel] {
  def db: JdbcProfile#Backend#Database

  val model: Model

  import model.databaseProfile.api._

  def run[T](dbio: DBIO[T]): F[T]
  def runTx[T](dbio: DBIO[T]): F[T]
}

trait DatabaseSupportFutureImpl[+F <: AbstractModel] extends DatabaseSupport[Future, F] {
  import model.databaseProfile.api._

  override def run[T](dbio: DBIO[T]): Future[T] = db.run(dbio)
  override def runTx[T](dbio: DBIO[T]): Future[T] = run(dbio.transactionally)
}

package object SlickSupport {
  import _root_.slick.dbio.DBIO
  import cats.~>

  def dbioTransformation[F[_]](databaseSupport: DatabaseSupport[F, _],
                              transactional: Boolean = true): DBIO ~> F =
    new (DBIO ~> F) {
      override def apply[T](dbio: DBIO[T]): F[T] =
        if (transactional) databaseSupport.runTx(dbio) else databaseSupport.run(dbio)
    }

}