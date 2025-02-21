package webserver.controllers

import models.*
import dataConnectors.PostgersqlConnector.*
import models.AccountRequests.AuthToken
import models.ItemRequests.*
import org.apache.pekko.http.scaladsl.model.Multipart.FormData
import org.apache.pekko.stream.Materializer
import shared.CommonFunctions.parseFormData
import webserver.queries.ItemQueries.*

import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}

object ItemControls {
  def searchItemsByName(searchTerm: String)(implicit ec: ExecutionContext): Future[List[Item]] = executeQuery(searchItems(searchTerm))
  def getUserItems(personId: UUID)(implicit ec: ExecutionContext): Future[List[Item]] = executeQuery(readUserItems(personId))
  def getItemsByCategory(category: String)(implicit ec: ExecutionContext): Future[List[Item]] = executeQuery(readItemsByCategory(category))
  def getCategoryList()(implicit ec: ExecutionContext): Future[List[String]] = executeQuery(readDistinctCategories())
  def updateItemDetails(authToken: AuthToken, update: UpdateItemDetails)(implicit ec: ExecutionContext): Future[Item] = {
    executeQuery(selectItemById(update.itemId)).flatMap(items => {
      if items.head.ownerId.equals(authToken.user.personId) then
        executeInsert(updateItem(update)).map{_ =>
          items.head.copy(
            itemName = update.itemName,
            location = Some(update.location),
            category = update.category,
            description = Some(update.description)
          )
        }
      else throw NotMatchingParameters("The item does not belong to the user")
    })
  }
  def updateImage(authToken: AuthToken, itemId: UUID, image: FormData)(implicit ec: ExecutionContext, mat: Materializer): Future[Unit] = {
    val itemRead = executeQuery(selectItemById(itemId))
    val imageParse = parseFormData(image)
    itemRead.zip(imageParse).flatMap((items, bytedImage) => {
      if items.head.ownerId.equals(authToken.user.personId) then 
        executeInsert(updateItemImage(bytedImage, itemId)).map(_ => ())
      else throw NotMatchingParameters("The item requested for update does not belong to the user.")
    })
  }
  def getImage(itemId: UUID)(implicit ec: ExecutionContext): Future[Option[Array[Byte]]] = {
    executeQuery(getItemImageData(itemId)).map(_.head)
  }
}
