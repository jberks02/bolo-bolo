package queries

import java.util.UUID

def queryForItemsByUUID(uuid: UUID): (String, Seq[String]) =
  val query ="""
      |
      |SELECT
      |itemId
      |,description
      |,commonNames
      |,documentationUrls
      |,created
      |,lastUpdated
      |FROM Items
      |WHERE itemId = ?
      |""".stripMargin
  val params = Seq(uuid.toString)
  (query, params)

def queryForItemsByDescription(description: String): (String, Seq[String]) =
  val query ="""
      |SELECT
      |itemId
      |,description
      |,commonNames
      |,documentationUrls
      |,created
      |,lastUpdated
      |FROM Items
      |WHERE description LIKE '%?%'
      |
      |""".stripMargin
  (query, Seq(description))
