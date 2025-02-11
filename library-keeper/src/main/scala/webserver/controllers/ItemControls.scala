package webserver.controllers

import models.{ItemCheckoutRequest, Person, Item}

import scala.concurrent.{ExecutionContext, Future}

object ItemControls {
  def getItemRequests()(implicit ec: ExecutionContext, user: Person): Future[List[ItemCheckoutRequest]] = ???
  def getUserItems()(implicit ec: ExecutionContext, user: Person): Future[List[Item]] = ???
  def getAvailableItems(paginationId: String, size: Int, page: Int)(implicit ec: ExecutionContext): Future[List[Item]] = ???
  def searchItems(searchTerm: String)(implicit ec: ExecutionContext): Future[List[Item]] = ??? 
  def createCheckoutRequest(itemId: String)(implicit ec: ExecutionContext, user: Person): Future[Unit] = ??? 
  
}
