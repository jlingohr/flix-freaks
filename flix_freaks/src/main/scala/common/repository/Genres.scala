package main.scala.common.repository

import domain.Genre
import slick.lifted.Tag
import slick.jdbc.PostgresProfile.api._



class GenreTable(tag: Tag) extends Table[Genre](tag, "genre") {
  def id: Rep[Int] = column[Int]("id", O.PrimaryKey, O.AutoInc)
  def name: Rep[String] = column[String]("name")

  def * = (id.?, name) <> ((Genre.apply _).tupled, Genre.unapply)
}

object Genres {
  val genres = TableQuery[GenreTable]
}
