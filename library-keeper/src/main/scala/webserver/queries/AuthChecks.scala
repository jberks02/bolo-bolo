package webserver.queries

import doobie.util.query.Query0
import models.Person
import doobie.postgres.implicits.*
import doobie.implicits.*
import doobie.util.meta.Meta
import doobie.*
import models.AccountRequests.{CompleteToken, CreationToken, UpdatePersonDetails, UserPromotion}
import shared.DoobieCaseObjectMappings.*

import java.util.UUID

object AuthChecks {
  def getUserEncPassword(email: String): Query0[String] = {
    sql"""
      SELECT enc_password
      FROM people
      WHERE email = $email
    """.query[String]
  }
  def getUserData(email: String): Query0[Person] = {
    sql"""
          SELECT
            person_id,
            auth,
            first_name,
            middle_name
            last_name,
            email,
            createdAt,
            updatedAt
          FROM people
          WHERE email = $email
       """.query[Person]
  }
  def getUserDataById(personId: UUID): Query0[Person] = {
    sql"""
          SELECT
            person_id,
            auth,
            first_name,
            middle_name
            last_name,
            email,
            createdAt,
            updatedAt
          FROM people
          WHERE person_id = $personId
    """.query[Person]
  }
  def getUserImageData(id: UUID): Query0[Option[Array[Byte]]] = {
    sql"""
         SELECT profile_image
         FROM people
         WHERE person_id = $id
       """.query[Option[Array[Byte]]]
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
  def invalidateToken(token: String): Update0 = {
    sql"""
         UPDATE new_user_tokens
         SET valid = false
         WHERE token = $token""".update
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
  def insertNewToken(token: CompleteToken): Update0 = {
    sql"""
         INSERT INTO new_user_tokens (
           token,
           created_at,
           expiration,
           valid
         ) VALUES (
           ${token.token},
           ${token.createdAt},
           ${token.expiration},
           ${token.valid}
           """.update
  }
  def updatePersonImage(image: Array[Byte], personId: UUID): Update0 = {
    sql"""
         UPDATE people
         SET profile_image = $image,
         updated_at = now()
         WHERE person_id = ${personId.toString}
    """.update
  }
  def updatePersonAuth(promo: UserPromotion): Update0 = {
    sql"""
         UPDATE people
         SET auth = ${promo.authLevel},
         updated_at = now()
         WHERE person_id = ${promo.personId}
       """.update
  }
  def updatePersonPassword(password: String, personId: UUID): Update0 = {
    sql"""
         UPDATE people
         SET enc_password = $password,
         updated_at = now()
         WHERE person_id = $personId
    """.update
  }
  def updateDetails(personalDetails: UpdatePersonDetails): Update0 = {
    sql"""
         UPDATE people
         SET first_name = ${personalDetails.firstName},
         middle_name = ${personalDetails.middleName},
         last_name = ${personalDetails.lastName},
         email = ${personalDetails.email},
         updated_at = now()
         WHERE person_id = ${personalDetails.personId}
    """.update
  }
}
