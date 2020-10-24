package common.domain

import eu.timepit.refined.types.string.NonEmptyString
import io.estatico.newtype.macros.newtype
import slick.jdbc.JdbcType

object genres {

  @newtype case class GenreId(value: Int)
  @newtype case class GenreName(value: String)

  final case class Genre(id: GenreId, name: GenreName)


}
