package common.model

import be.venneborg.refined.{RefinedMapping, RefinedSupport}

trait SlickRefinedProfile extends slick.jdbc.PostgresProfile
  with RefinedMapping
  with RefinedSupport {

  override val api = new API with RefinedImplicits

}

object SlickRefinedProfile extends SlickRefinedProfile