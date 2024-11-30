package controllers

import models.Item
import java.util.UUID
import queries.{queryForItemsByUUID, queryForItemsByDescription}
import dao.DbConnector.executeQuery
import scala.concurrent.{ExecutionContext, Future}

def getItemById(id: UUID)(implicit ec: ExecutionContext): Future[Option[Item]] =
  val queryAndParams = queryForItemsByUUID(id)
  executeQuery[Item](queryAndParams._1, queryAndParams._2).map(_.headOption)
def searchByDescription(desc: String)(implicit ec: ExecutionContext): Future[List[Item]] =
  val queryAndParams = queryForItemsByDescription(desc)
  executeQuery[Item](queryAndParams._1, queryAndParams._2)
//def searchByCommonName(name: String)(implicit ec: ExecutionContext): Future[Option[List[Item]]] = ???
//def createItem(it: Item)(implicit ec: ExecutionContext): Future[Item] = ???
//def updateDocumentationUrls(id: UUID, docList: List[String])(implicit ec: ExecutionContext): Future[Option[Item]] = ???
//def updateCommonNames(id: UUID, nameList: List[String])(implicit ec: ExecutionContext): Future[Option[Item]] = ???
