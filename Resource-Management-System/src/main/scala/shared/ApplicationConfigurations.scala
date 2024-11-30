package shared

import models._
import com.typesafe.config.{Config, ConfigFactory}

object ApplicationConfigurations {
  private val config: Config = ConfigFactory.parseResources("reference.json").resolve()
  val dbConfig: DbSecrets = DbSecrets(
    url = config.getString("db.host"),
    username = config.getString("db.username"),
    password = config.getString("db.password"),
    driver = config.getString("db.driver")
  )
}
