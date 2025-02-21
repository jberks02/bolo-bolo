package webserver.queries

import doobie.util.query.Query0
import models.Item
import doobie.postgres.implicits.*
import doobie.implicits.*
import doobie.util.meta.Meta
import doobie.*
import models.AccountRequests.{CompleteToken, CreationToken, UpdatePersonDetails, UserPromotion}
import models.ItemRequests.UpdateItemDetails
import shared.DoobieCaseObjectMappings.*

import java.util.UUID

object ItemQueries {
  def searchItems(searchTerm: String): Query0[Item] = {
    sql"""
         SELECT 
          item_id,
          owner_id, 
          item_name, 
          description,
          category, 
          location,
          created_at,
          updated_at
         FROM items
         WHERE name LIKE $searchTerm
       """.query[Item]
  }
  def readUserItems(personId: UUID): Query0[Item] = {
    sql"""
      SELECT 
        item_id,
        owner_id, 
        item_name, 
        description,
        category, 
        location,
        created_at,
        updated_at
      FROM items
      WHERE owner_id = $personId
    """.query[Item]
  }
  def readItemsByCategory(category: String): Query0[Item] = {
    sql"""
      SELECT 
        item_id,
        owner_id, 
        item_name, 
        description,
        category, 
        location,
        created_at,
        updated_at
        FROM items
        WHERE category = $category
    """.query[Item]
  }
  def readDistinctCategories(): Query0[String] = {
    sql"""
         SELECT DISTINCT category 
         FROM items;
    """.query[String]
  }
  def selectItemById(itemId: UUID): Query0[Item] = {
    sql"""
         SELECT
          item_id,
          owner_id,
          item_name,
          description,
          category,
          location,
          created_at,
          updated_at
        FROM items
        WHERE item_id = $itemId
    """.query[Item]
  }
  def updateItem(update: UpdateItemDetails): Update0 = {
    sql"""
        UPDATE items
          SET item_name = ${update.itemName},
          description = ${update.description},
          category = ${update.category},
          location = ${update.location},
          updated_at = now()
        WHERE item_id = ${update.itemId}
       """.update
  }

  def updateItemImage(image: Array[Byte], itemId: UUID): Update0 = {
    sql"""
           UPDATE items
           SET item_image = $image,
           updated_at = now()
           WHERE item_id = $itemId
      """.update
  }

  def getItemImageData(id: UUID): Query0[Option[Array[Byte]]] = {
    sql"""
           SELECT item_image
           FROM items
           WHERE item_id = $id
    """.query[Option[Array[Byte]]]
  }
}
