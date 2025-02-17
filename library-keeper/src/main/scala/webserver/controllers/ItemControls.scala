package webserver.controllers

import models._
import dataConnectors.PostgersqlConnector._
import webserver.queries.ItemQueries._
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object ItemControls {
  def searchItemsByName(searchTerm: String)(implicit ec: ExecutionContext): Future[List[Item]] = executeQuery(searchItems(searchTerm))
  def getUserItems(personId: UUID)(implicit ec: ExecutionContext): Future[List[Item]] = executeQuery(readUserItems(personId))
  def getItemsByCategory(category: String)(implicit ec: ExecutionContext): Future[List[Item]] = executeQuery(readItemsByCategory(category))
  def getCategoryList()(implicit ec: ExecutionContext): Future[List[String]] = executeQuery(readDistinctCategories())
  
}
