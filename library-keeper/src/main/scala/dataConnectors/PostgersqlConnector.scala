package dataConnectors

import shared.AppConfiguration.dbConfig
import scala.concurrent.Future
import scala.reflect.ClassTag
import doobie.*
import doobie.implicits.*
import cats.*
import cats.effect.*
import cats.effect.unsafe.implicits.global

object PostgersqlConnector {
  private val transactor = Transactor.fromDriverManager[IO](
    driver = dbConfig.driver,
    url = dbConfig.url,
    user = dbConfig.username,
    password = dbConfig.password,
    logHandler = None
  )

  def executeQuery[T: ClassTag](query: Query0[T]): Future[List[T]] = {
    val req = query.to[List].transact(transactor)
    req.unsafeToFuture()
  }

  def executeInsert(in: Update0): Future[Int] = {
    in.run.transact(transactor).unsafeToFuture()
  }
  
}
