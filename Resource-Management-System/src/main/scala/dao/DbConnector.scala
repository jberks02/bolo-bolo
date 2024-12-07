package dao

import shared.ApplicationConfigurations.dbConfig
import scala.concurrent.Future
import scala.reflect.ClassTag
import doobie._
import doobie.implicits._
import cats._
import cats.effect._
import cats.effect.unsafe.implicits.global


object DbConnector {
  
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