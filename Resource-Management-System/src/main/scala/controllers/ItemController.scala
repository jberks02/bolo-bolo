package controllers

import models.Item

import java.util.UUID
import queries.ItemQueries.*
import dao.DbConnector.*
import doobie.util.update.Update0
import queries.ItemQueries.queryForItemsByUUID

import scala.concurrent.{ExecutionContext, Future}

def getItemById(id: UUID)(implicit ec: ExecutionContext): Future[Option[Item]] =
  val queryAndParams = queryForItemsByUUID(id)
  executeQuery[Item](queryAndParams).map(_.headOption)
def searchByDescription(desc: String)(implicit ec: ExecutionContext): Future[List[Item]] =
  val queryAndParams = queryForItemsByDescription(desc)
  executeQuery[Item](queryAndParams)
def searchByCommonName(name: String)(implicit ec: ExecutionContext): Future[List[Item]] =
  val query = queryForItemsByNames(name)
  executeQuery[Item](query)
def createItem(it: Item)(implicit ec: ExecutionContext): Future[Item] =
  val insertQuery = insertNewItem(it)
  executeInsert(insertQuery).map(_ => it)
def updateDocumentationUrls(id: UUID, docList: List[String])(implicit ec: ExecutionContext): Future[Option[Item]] =
  getItemById(id).flatMap {
    case Some(value) =>
      val updt: Update0 = updateDocumentUrlsField(id, docList)
      executeInsert(updt).map { _ =>
        Some(value.copy(documentationUrls = docList))
      }
    case None => Future.successful(None)
  }
def updateCommonNames(id: UUID, nameList: List[String])(implicit ec: ExecutionContext): Future[Option[Item]] =
  getItemById(id).flatMap {
    case Some(value) =>
      val updt: Update0 = updateCommonnamesField(id, nameList)
      executeInsert(updt).map(_ => Some(value.copy(commonNames = nameList)))
    case None => Future.successful(None)
  }