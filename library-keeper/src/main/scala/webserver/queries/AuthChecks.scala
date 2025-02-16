package webserver.queries

import doobie.util.query.Query0
import models.{CreationToken, Person}
import doobie.postgres.implicits.*
import doobie.implicits.*
import doobie.util.meta.Meta
import doobie.*
import shared.DoobieCaseObjectMappings.*
import java.util.UUID

object AuthChecks {
  def getUserEncPassword(personId: UUID): Query0[String] = {
    sql"""
      SELECT enc_password
      FROM people
      WHERE person_id = $personId
    """.query[String]
  }
  def getUserData(personId: UUID): Query0[Person] = {
    sql"""
          SELECT
            person_id,
            auth,
            first_name,
            last_name,
            email,
            createdAt,
            updatedAt
          FROM people
          WHERE person_id = $personId
       """.query[Person]
  }
  def getToken(token: String): Query0[CreationToken] = {
    sql"""
         SELECT
          created_at,
          expiration,
          valid
         FROM new_user_tokens
         WHERE token = $token
       """.query[CreationToken]
  }
  def getCountOfPersons(): Query0[Int] = {
    sql"""
         SELECT COUNT(*) as count
         FROM people
       """.query[Int]
  }
  def insertNewPerson(person: Person, encryptedPassword: String): Update0 = {
    sql"""
         INSERT INTO people (
           person_id,
           enc_password,
           auth,
           first_name,
           last_name,
           email,
           enc_password,
           created_at,
           updated_at
         ) VALUES (
           ${person.personId},
           $encryptedPassword,
           ${person.auth},
           ${person.firstName},
           ${person.lastName},
           ${person.email},
           $encryptedPassword,
           ${person.createdAt},
           ${person.updatedAt}
         )
       """.update
  }
}
