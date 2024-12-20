package models

import doobie.{Update0, Query0}
import doobie.util.*
import scala.concurrent.Future
import scala.reflect.ClassTag 

trait DbConnection {
  def executeQuery[T: ClassTag](query: Query0[T]): Future[List[T]];
  def executeInsert(in: Update0): Future[Int]
}
