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
    id1(User Types In Domain) --> id2(User Scans One Time Use QR Code) --> id3(User Creates Profile) --> id4(Users password and information is stored, JWT tracks sessions)
```

```mermaid
---
title: Route Continued Authentication - Technical
---
flowchart TD 
a(User Request Comes In) --> b(Filter Stage Hit) --> c(Route Checked to see if it's in exception list) --> d(Header Token Decrypted and Validated) --> e(Call is passed to actual endpoint)
c --> f(Unique route handled) --> g(Route Completed)
```


