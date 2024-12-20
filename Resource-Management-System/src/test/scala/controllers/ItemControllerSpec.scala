package controllers

import org.scalatest.flatspec.AsyncFlatSpec
import org.scalatest.matchers.should.Matchers
import org.scalamock.scalatest.AsyncMockFactory

import java.time.LocalDateTime
import java.util.UUID
import scala.concurrent.{ExecutionContext, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import doobie.Query0
import doobie.Update0
import models.{DbConnection, Item}
import doobie.*
import doobie.implicits.*
import doobie.util.meta.Meta
import controllers.ItemController

import scala.reflect.ClassTag

class ItemControllerSpec extends AsyncFlatSpec with Matchers with AsyncMockFactory {

  trait MyTestContext {
    val mockDb: DbConnection = mock[DbConnection]

    val sampleItem = Item(
      itemId = UUID.randomUUID(),
      description = "A test item",
      commonNames = List("TestCommonName"),
      documentationUrls = List("http://docs.example.com"),
      lastUpdate = LocalDateTime.now(),
      created = LocalDateTime.now().minusDays(1)
    )
  }

  "getItemById" should "return an item when found" in {
    val testCtx = new MyTestContext {}
    import testCtx._
    // Mock the executeQuery call to return a list with our sampleItem
    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(List(sampleItem)))

    val resultF = ItemController.getItemById(sampleItem.itemId)(global, mockDb)
    resultF.map { result =>
      result shouldBe Some(sampleItem)
    }
  }

  it should "return None when no item is found" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(Nil))

    val resultF = ItemController.getItemById(UUID.randomUUID())(global, mockDb)
    resultF.map { result =>
      result shouldBe None
    }
  }

  "searchByDescription" should "return a list of matching items" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    val matchingItems = List(sampleItem, sampleItem.copy(itemId = UUID.randomUUID()))

    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(matchingItems))

    val resultF = ItemController.searchByDescription("test")(global, mockDb)
    resultF.map { result =>
      result should contain theSameElementsAs matchingItems
    }
  }

  "searchByCommonName" should "return a list of items matching a given common name" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    val matchingItems = List(sampleItem, sampleItem.copy(itemId = UUID.randomUUID()))

    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(matchingItems))

    val resultF = ItemController.searchByCommonName("TestCommonName")(global, mockDb)
    resultF.map { result =>
      result should contain theSameElementsAs matchingItems
    }
  }

  "createItem" should "insert the item and return it" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    // Mock the executeInsert to return 1, indicating a row was inserted
    (mockDb.executeInsert _)
      .expects(*)
      .returning(Future.successful(1))

    val resultF = ItemController.createItem(sampleItem)(global, mockDb)
    resultF.map { result =>
      result shouldBe sampleItem
    }
  }

  "updateDocumentationUrls" should "update doc URLs if item exists" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    val newDocs = List("http://newdoc1.com", "http://newdoc2.com")

    // First call: getItemById
    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(List(sampleItem)))

    // Second call: update doc URLs
    (mockDb.executeInsert _)
      .expects(*)
      .returning(Future.successful(1))

    val resultF = ItemController.updateDocumentationUrls(sampleItem.itemId, newDocs)(global, mockDb)
    resultF.map { maybeUpdated =>
      maybeUpdated.map(_.documentationUrls) shouldBe Some(newDocs)
    }
  }

  it should "return None if the item does not exist (updateDocumentationUrls)" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(Nil))

    val newDocs = List("http://doesnotmatter.com")

    val resultF = ItemController.updateDocumentationUrls(UUID.randomUUID(), newDocs)(global, mockDb)
    resultF.map { result =>
      result shouldBe None
    }
  }

  "updateCommonNames" should "update common names if item exists" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    val newNames = List("Name1", "Name2")

    // First call: getItemById
    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(List(sampleItem)))

    // Second call: executeInsert to update commonNames
    (mockDb.executeInsert _)
      .expects(*)
      .returning(Future.successful(1))

    val resultF = ItemController.updateCommonNames(sampleItem.itemId, newNames)(global, mockDb)
    resultF.map { maybeUpdated =>
      maybeUpdated.map(_.commonNames) shouldBe Some(newNames)
    }
  }

  it should "return None if the item does not exist (updateCommonNames)" in {
    val testCtx = new MyTestContext {}
    import testCtx._

    (mockDb.executeQuery(_: Query0[Item])(_: ClassTag[Item]))
      .expects(*, *)
      .returning(Future.successful(Nil))

    val newNames = List("DoesNotMatter")

    val resultF = ItemController.updateCommonNames(UUID.randomUUID(), newNames)(global, mockDb)
    resultF.map { result =>
      result shouldBe None
    }
  }
}