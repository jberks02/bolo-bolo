package dao

import org.apache.pekko.http.scaladsl.model.DateTime

//import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException, SQLTimeoutException}
import shared.ApplicationConfigurations.dbConfig
import shared.resultSetToObject
import scala.concurrent.{ExecutionContext, Future}
import scala.reflect.ClassTag
import cats.effect.IO
import doobie._
import doobie.hikari.HikariTransactor
import doobie.implicits
object DbConnector {

  // Load MariaDB JDBC driver
  try {
    println(dbConfig)
    Class.forName(dbConfig.driver)
  } catch {
    case e: ClassNotFoundException =>
      e.printStackTrace()
      throw new RuntimeException("Failed to load MariaDB JDBC driver")
  }
//  private val connection: Connection = DriverManager
//    .getConnection(dbConfig.url, dbConfig.username, dbConfig.password)

  private def executePureQuery(query: String): ResultSet = {
    try {
      connection.createStatement.executeQuery(query)
    } catch {
      case sqlException: SQLException => 
        println(s"The Sql Query Failed: \n${sqlException.getLocalizedMessage}")
        println(s"The sql state is: \n${sqlException.getSQLState}")
        throw new Exception("Database Error Occurred. SQLException")
      case sqlTimeout: SQLTimeoutException => 
        println(s"The SQL query failed due to timeout. ${sqlTimeout.getLocalizedMessage}")
        throw new Exception("Database Query timed out. SQLTimeoutException")
    }
  }
  def executeQuery[T: ClassTag](query: String, params: Seq[Any] = Seq())(implicit ec: ExecutionContext): Future[List[T]] = Future {
    if params.isEmpty then resultSetToObject[T](executePureQuery(query))
    else
      val preparedStatement = connection.prepareStatement(query)
      params.zipWithIndex.foreach { case (param, index) =>
        param match {
          case value: String => preparedStatement.setString(index + 1, value)
          case value: Int => preparedStatement.setInt(index + 1, value)
          case value: Double => preparedStatement.setDouble(index + 1, value)
          case value: Long => preparedStatement.setLong(index + 1, value)
          case value: Boolean => preparedStatement.setBoolean(index + 1, value)
          case _ => throw new IllegalArgumentException("Unsupported parameter type")
        }
      }
      resultSetToList[T](preparedStatement.executeQuery())
  }
}