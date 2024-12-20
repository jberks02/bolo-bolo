package queries

import org.scalatest.funsuite.AnyFunSuite
import queries.ItemQueries.*
import java.util.UUID
import models.Item
import java.time.LocalDateTime

class ItemQueriesSpec extends AnyFunSuite {

  test("queryForItemsByUUID returns properly prepared query") {
    val uu = UUID.randomUUID
    val query = queryForItemsByUUID(uu)
    assert(query.sql.contains(
      """
        |    SELECT
        |      "itemId",
        |      "description",
        |      "commonNames",
        |      "documentationUrls",
        |      "lastUpdated",
        |      "created"
        |    FROM "Items"
        |    WHERE "itemId" = ?
        |""".stripMargin))
  }

  test("queryForItemsByDescription returns properly prepared query") {
    val description = "testDescription"
    val query = queryForItemsByDescription(description)
    assert(query.sql.contains(
      """
        |    SELECT
        |      "itemId"
        |      ,"description"
        |      ,"commonNames"
        |      ,"documentationUrls"
        |      ,"lastUpdated"
        |      ,"created"
        |    FROM "Items"
        |    WHERE LOWER("description") LIKE LOWER(?)
        |""".stripMargin))
  }

  test("queryForItemsByNames returns properly prepared query") {
    val name = "testName"
    val query = queryForItemsByNames(name)
    assert(query.sql.contains(
      """
        |    SELECT
        |      "itemId",
        |      "description",
        |      "commonNames",
        |      "documentationUrls",
        |      "lastUpdated",
        |      "created"
        |    FROM "Items"
        |    WHERE "commonNames" @> ?::jsonb
        |""".stripMargin))
  }

  test("insertNewItem returns properly prepared update") {
    val testItem = Item(
      itemId = UUID.randomUUID(),
      description = "desc",
      commonNames = List("name1", "name2"),
      documentationUrls = List("http://doc1", "http://doc2"),
      lastUpdate = LocalDateTime.now(),
      created = LocalDateTime.now()
    )
    val update = insertNewItem(testItem)
    assert(update.sql.contains(
      """
        |         INSERT INTO "Items"
        |         ("itemId","description","commonNames","documentationUrls")
        |         VALUES (
        |           ?,
        |           ?,
        |           ?,
        |           ?
        |         )
        |""".stripMargin))
  }

  test("updateDocumentUrlsField returns properly prepared update") {
    val uu = UUID.randomUUID
    val docs = List("http://example.com/doc1")
    val update = updateDocumentUrlsField(uu, docs)
    assert(update.sql.contains(
      """
        |         UPDATE "Items" i
        |         SET "documentationUrls" = ?
        |         WHERE "itemId" = ?
        |""".stripMargin))
  }

  test("updateCommonnamesField returns properly prepared update") {
    val uu = UUID.randomUUID
    val names = List("common1", "common2")
    val update = updateCommonNamesField(uu, names)
    assert(update.sql.contains(
      """
        |         Update "Items"
        |         SET "commonNames" = ?
        |         WHERE "itemId" = ?
        |""".stripMargin))
  }

}