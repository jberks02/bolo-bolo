package queries

import java.util.UUID
import models.Item
import doobie._
import doobie.implicits._
import doobie.util.meta.Meta
import doobie.postgres.implicits._
import shared._

object ItemQueries {
  def queryForItemsByUUID(uuid: UUID): Query0[Item] =
    sql"""
    SELECT
      "itemId",
      "description",
      "commonNames",
      "documentationUrls",
      "lastUpdated",
      "created"
    FROM "Items"
    WHERE "itemId" = $uuid
  """.query[Item]

  def queryForItemsByDescription(description: String): Query0[Item] =
    sql"""
    SELECT
      "itemId"
      ,"description"
      ,"commonNames"
      ,"documentationUrls"
      ,"lastUpdated"
      ,"created"
    FROM "Items"
    WHERE LOWER("description") LIKE LOWER(${"%" + description + "%"})
       """.query[Item]

  def queryForItemsByNames(name: String): Query0[Item] =
    sql"""
    SELECT
      "itemId",
      "description",
      "commonNames",
      "documentationUrls",
      "lastUpdated",
      "created"
    FROM "Items"
    WHERE "commonNames" @> ${"\"" + name + "\""}::jsonb
     """.query[Item]

  def insertNewItem(it: Item): Update0 =
    sql"""
         INSERT INTO "Items"
         ("itemId","description","commonNames","documentationUrls")
         VALUES (
           ${it.itemId},
           ${it.description},
           ${it.commonNames},
           ${it.documentationUrls}
         )
       """.update
  def updateDocumentUrlsField(id: UUID, docs: List[String]): Update0 =
    sql"""
         UPDATE "Items" i
         SET "documentationUrls" = $docs
         WHERE "itemId" = $id
       """.update
  def updateCommonnamesField(id: UUID, names: List[String]): Update0 =
    sql"""
         Update "Items"
         SET "commonNames" = $names
         WHERE "itemId" = $id
       """.update
}