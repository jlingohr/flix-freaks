package common.domain

import io.estatico.newtype.macros.newtype

object auth {

  @newtype case class UserId(value: String)

}
