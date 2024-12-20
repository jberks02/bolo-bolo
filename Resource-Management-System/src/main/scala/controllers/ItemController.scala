package controllers

import models.{DbConnection, Item}
import java.util.UUID
import queries.ItemQueries.*
import doobie.util.update.Update0
import queries.ItemQueries._
import scala.concurrent.{ExecutionContext, Future}

object ItemController {
  
  def getItemById(id: UUID)(implicit ec: ExecutionContext, database: DbConnection): Future[Option[Item]] =
    val queryAndParams = queryForItemsByUUID(id)
    database.executeQuery[Item](queryAndParams).map(_.headOption)

  def searchByDescription(desc: String)(implicit ec: ExecutionContext, database: DbConnection): Future[List[Item]] =
    val queryAndParams = queryForItemsByDescription(desc)
    database.executeQuery[Item](queryAndParams)

  def searchByCommonName(name: String)(implicit ec: ExecutionContext, database: DbConnection): Future[List[Item]] =
    val query = queryForItemsByNames(name)
    database.executeQuery[Item](query)

  def createItem(it: Item)(implicit ec: ExecutionContext, database: DbConnection): Future[Item] =
    val insertQuery = insertNewItem(it)
    database.executeInsert(insertQuery).map(_ => it)

  def updateDocumentationUrls(id: UUID, docList: List[String])(implicit ec: ExecutionContext, database: DbConnection): Future[Option[Item]] =
    getItemById(id).flatMap {
      case Some(value) =>
        val updt: Update0 = updateDocumentUrlsField(id, docList)
        database.executeInsert(updt).map { _ =>
          Some(value.copy(documentationUrls = docList))
        }
      case None => Future.successful(None)
    }

  def updateCommonNames(id: UUID, nameList: List[String])(implicit ec: ExecutionContext, database: DbConnection): Future[Option[Item]] =
    getItemById(id).flatMap {
      case Some(value) =>
        val updt: Update0 = updateCommonNamesField(id, nameList)
        database.executeInsert(updt).map(_ => Some(value.copy(commonNames = nameList)))
      case None => Future.successful(None)
    }
}