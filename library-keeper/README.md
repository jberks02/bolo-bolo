# Library-Keeper

This is a project dedicated to running a library of things. This suite contains everything required to keep track of items and check them out. 

Once complete it'll require just a compile and deployment of a single executable.

## Data Entities

A comprehensive description and documentation of all data entities in the system

### Database Entities
Check database and table creation script for field restrictions.
```mermaid
erDiagram
    people {
        UUID person_id
        BYTEA profile_image
        int auth
        string first_name
        string last_name
        string email
        timestamp created_at
        timestamp updated_at
    }
    items {
        UUID item_id
        UUID owner_id
        BYTEA item_image
        string item_name
        string category
        string description
        string location
        timestamp created_at
        timestamp updated_at
    }
    checkouts {
        UUID checkout_id
        UUID item_id
        UUID person_id
        timestamp checkout_date
        timestamp due_date
        timestamp return_date
    }
    communications {
        uuid message_id
        uuid sender_id
        uuid recipient_id
        uuid checkout_id
        string message
        timestamp sent_at
        timestamp read_at
    }
    
    item_checkout_requests {
        uuid request_id
        uuid requester_id
        uuid item_id
        timestamp requested_at
        timestamp requested_checkout_date
        timestamp requested_due_date
    }
    
    services {
        UUID service_id
        UUID servicer_id
        string service_name
        BYTEA service_image
        string description
        string category
        timestamp created_at
        timestamp updated_at
    }
    
    service_requests {
        UUID service_request_id
        UUID requester_id
        UUID service_id
        timestamp requested_at
        timestamp service_date
        boolean fullfilled
    }
    
    people ||--|{ items : "person_id - owner_id"
    items ||--|{ checkouts : item_id
    people }|--|{ checkouts: person_id
    communications }|--|| checkouts: checkout_id
    communications }|--|| checkouts: service_request_id
    communications }|--|| people: recipient_id
    communications }|--|| people: sender_id
    item_checkout_requests }|--|| people: requester_id
    services ||--|{ people: servicer_id
    service_requests ||--|{ people: requester_id
    service_requests ||--|{ services: service_id
```

### Historical Database Entities
These are auditing tables. They're meant only to be used to gauge usage and activity and verify that what someone says happened happened. 

```mermaid
erDiagram
    people_history {
        UUID person_id
        string first_name
        string last_name
        string email
        timestamp created_at
        timestamp updated_at
        string operation_type
        timestamp changed_at
    }
    items_history {
        UUID item_id
        UUID owner_id
        string item_name
        string category
        string description
        string location
        timestamp created_at
        timestamp updated_at
        string operation_type
        timestamp changed_at
    }
    checkouts_history {
        UUID checkout_id
        UUID item_id
        UUID person_id
        timestamp checkout_date
        timestamp due_date
        timestamp return_date
        string operation_type
        timestamp changed_at
    }
    item_checkout_requests_history {
        UUID request_id
        UUID requestor_id
        UUID item_id
        timestamp requested_at
        string operation_type
        timestamp changed_at
    }
    services_history {
        UUID service_id
        UUID servicer_id
        string service_name
        string description
        string category
        timestamp created_at
        timestamp updated_at
        string operation_type
        timestamp changed_at
    }
    service_requests_history {
        UUID service_request_id
        UUID requester_id
        UUID service_id
        timestamp requested_at
        timestamp service_date
        boolean fullfilled
        string operation_type
        timestamp changed_at
    }
```

### Application Entities
These are entities that exist purely in the application and on the front end of the application. All of them act as extensions
or compilations of the database entities to make them usable to the frontend. 
```mermaid
erDiagram

    CommunicationSet-extends-checkouts {
        UUID checkout_id
        UUID item_id
        UUID person_id
        timestamp checkout_date
        timestamp due_date
        timestamp return_date
        list communications
    }
    
    OwnedItems-extends-people {
        UUID person_id
        int auth
        string first_name
        string last_name
        string email
        timestamp created_at
        timestamp updated_at
        list items
    }
    
```

## User Interaction Flows 

These are descriptions of the basic flows within that application to facilitate the checking in and out of items and requests for services


```mermaid
---
title: User Sign On Flow
---
flowchart TD
    id1(User Clicks Invite Link) --> id3(User Creates Profile) --> id4(Users password and information is stored) --> id5(User Stores token safely on device)
```

```mermaid
---
title: Route Continued Authentication - Technical
---
flowchart TD 
a(User Request Comes In) --> b(Filter Stage Hit) --> c(Route Checked to see if it's in exception list) --> d(Header Token Decrypted and Validated) --> e(Call is passed to actual endpoint)
c --> f(Unique route handled) --> g(Route Completed)
```
```mermaid
---
title: New User Sign Up
---
flowchart TD 
a(User Receives Sign Up Token) 
b(New system is setup with no Users in the Database)
c(Requests for sign up comes in)
d(User has valid token)
f(User token is invalid or expired)
NoToken(User has not provided a token)
a --> c --> d --> e(User profile created new header token sent back to user.)
b --> c --> f --> g(User receives error telling them to contact system administrator)
c --> NoToken --> NoUsers(Person Data bae is empty, new user becomes administrator.)
NoToken --> ValidUsers(There are valid users in the database, error to get sign up token received.)
```
```mermaid
---
title: Administrator Permission Upgrade
---
flowchart TD
    Promo(Administrator wishes to promote someone to higher level)
    Req(Administrator sends request to upgrade permissions)
    Auth(Request authenticated to be from administrator)
    NoAuth(Bad request returned to non administrator)
    Authed(User database record updated with posted new level)
    NoContent(NoContent result message returned)
    Promo --> Req --> Auth --> Authed --> NoContent
    Req --> NoAuth
```
### Item Rental Flows
```mermaid
---
title: User adds rental item
---
flowchart TD
    AddItem(User presses add button and selects Rental Item)
    NewItem(User fills out form for new item they want to add to the dataset.)
    Optional(User can optionally add an image of the rental item)
    Authentication(User authentication confirmed. Item confirmed to be owned by them.)
    Added(Item added to dataset)
    Permissions(User permissions updated based on number of items in the database.)
    NoContent(NoContent success message sent to user.)
    AddItem --> NewItem --> Optional --> Authentication --> Added --> Permissions --> NoContent
```
```mermaid
---
title: User checks out item
---
flowchart TD
    FindsItem(User finds item they wish to check out)
    UserIsHighEnoughAuthLevel(Users auth level or level is appropriate)
    Rejection(User is unable to checkout item)
    SelectsOptions(User selects from available dates and pickup locations)
    ProposesPriceFromRange(User proposes price from item given range)
    ChatOpensToFinalizeDeal(User and item owner open temporary chat to discuss details)
    PriceFinalized(Price and details are finalized by both parties.)
    PaymentFinalized(User pays the owner, signaling the completion of the transaction part)
    PickupHappened(User marks item as Picked Up.)
    ReturnHappened(Owner marks item as returned once returned)
    Rating(?Both are prompted to rate one another?)
    FindsItem --> UserIsHighEnoughAuthLevel -- Yes --> SelectsOptions --> ProposesPriceFromRange -->
    ChatOpensToFinalizeDeal --> PriceFinalized --> PaymentFinalized --> PickupHappened --> 
    ReturnHappened --> Rating
    
    UserIsHighEnoughAuthLevel --No --> Rejection
```
```mermaid
---
title: Item Owner Receives Rental Alert
---
flowchart TD
    ItemRentalRequested(A User asks to rent item held by owner)
    Review(Owner reviews details and price of request)
    ChatInitiated(Owner agrees to open chat)
    Chat(Owner and User discuss details of rental pickup and drop off)
    PriceAndTermsFinalized(Price and details are finalized by both parties)
    PaymentFinalized(Payment is finalized by User requesting item when they pay)
    Pickup(User marks item as picked up)
    DropOff(Owner marks item as dropped off)
    Rating(?Both are prompted to rate each other?)
    ItemRentalRequested --> Review --> ChatInitiated --> Chat --> PriceAndTermsFinalized -->
    PaymentFinalized --> Pickup --> DropOff --> Rating 
```
### Service Requested Flows
```mermaid
---
title: User Adds Service
---
flowchart TD
%%    Need to find a better word than service
    PressesAdd(User presses add button and selects Service)
    Form(User fills out information for service)
    Optional(Optionally User adds an image to represent the service)
    ItemUpload(Item and details are uploaded to dataset)
    Image(Image is added if user uploaded one)
    ServiceIsAvailableForRequest(Service becomes available for request)
    PressesAdd --> Form --> Optional --> ItemUpload --> Image --> ServiceIsAvailableForRequest
```
```mermaid
---
title: User Requests Service
---
flowchart TD
    UserReq(User finds service they need)
    Date(User specifies available date and time)
    Price(User picks from price range)
    FinalizesFromDetails(User Finalizes choices Prompting worker for chat)
    Chat(User and worker chat to finalize price and timeframe)
    TermsFinalized(Both parties finalize details)
    Payment(Payment finalized once user pays)
    Fulfilled(User marks service as fulfilled once they are satisfied service is complete)
    Cancelled(Service can be cancelled for any reason if both parties agree)
    Rating(?Users rate one another and experience)
    UserReq --> Date --> Price --> FinalizesFromDetails --> Chat --> TermsFinalized -->
    Payment --> Fulfilled --> Rating
    Payment --> Cancelled --> Rating
```
```mermaid
---
title: User Gets Service Request
---
flowchart TD
    Notification(Worker gets notification of service request)
    Review(Reviews details of request from user)
    Reject(Worker either doesn't want to or can't fulfill service and rejects it)
    Accept(Worker accepts initial proposal to start chat)
    Chat(Worker and user begin chat to finalize details of job)
    FinalizeDetails(Both users agree to the terms of the service)
    Payment(Payment made by User Finalizing Transaction)
    Fulfillment(User marks task as fulfilled releasing payment)
    Rating(?Both users rate each other?)
    Notification --> Review --> Accept --> Chat --> FinalizeDetails --> Payment --> Fulfillment --> Rating
    Review --> Reject
    Chat --> Reject
```
### General Payment Flow
```mermaid
---
title: Payment Flow (General)
---
flowchart TD
    DetailsFinalized(Details of deal finalized - Date,time,price)
    Rejection(Cancellation or other rejection condition results in monetary repayment)
    UserFinalizesPayment(User has money removed to pay for service and goes into the app as pending toward the end user)
    FinalizedActionOccurs(Item is picked up or service is fulfilled, paying out to renter or worker)
    DetailsFinalized --> UserFinalizesPayment --> FinalizedActionOccurs
    UserFinalizesPayment --> Rejection
```
