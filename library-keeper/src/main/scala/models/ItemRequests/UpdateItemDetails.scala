package models.ItemRequests

import java.util.UUID

case class UpdateItemDetails(
                              itemId: UUID,
                              itemName: String,
                              category: String,
                              description: String,
                              location: String
                            )
