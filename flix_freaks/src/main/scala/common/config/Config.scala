package config

import com.typesafe.config.ConfigFactory

trait Config {
  private val config = ConfigFactory.load()
  private val databaseConfig = config.getConfig("database")

  //  val databaseURL = databaseConfig.getString("properties.url")
  //  val databaseUser = databaseConfig.getString("properties.user")
  //  val databasePassword = databaseConfig.getString("properties.password")
}
