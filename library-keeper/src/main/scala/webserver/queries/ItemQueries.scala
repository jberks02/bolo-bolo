package webserver.queries

import doobie.util.query.Query0
import models.Item
import doobie.postgres.implicits.*
import doobie.implicits.*
import doobie.util.meta.Meta
import doobie.*
import models.AccountRequests.{CompleteToken, CreationToken, UpdatePersonDetails, UserPromotion}
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
}
