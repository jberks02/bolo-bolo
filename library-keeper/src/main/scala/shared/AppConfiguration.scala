package shared

import com.typesafe.config.{Config, ConfigFactory}
import models.DbSecrets
import scala.jdk.CollectionConverters._

object AppConfiguration {
  private val configs: Config = ConfigFactory.parseResources("configurations.conf")
  private val secrets: Config = ConfigFactory.parseResources("reference.conf").withFallback(configs).resolve
  val cyrptoToken: String = secrets.getString("authentication.jwtCryptoToken")
  val whiteListedRoutes: List[String] = secrets.getStringList("authentication.whiteListedRoutes").asScala.toList
  val authTimeout: Int = secrets.getInt("authentication.authenticationTimeoutMinutes")
  val authHeader: String = secrets.getString("authorizationHeader")
  val dbConfig: DbSecrets = DbSecrets(
    url = secrets.getString("db.host"),
    username = secrets.getString("db.username"),
    password = secrets.getString("db.password"),
    driver = secrets.getString("db.driver")
  )
}
