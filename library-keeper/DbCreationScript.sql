BEGIN;

-- 1. Drop Tables (in reverse dependency order)
DROP TABLE IF EXISTS checkouts;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS people;

-- 2. Create People Table
CREATE TABLE people (
    person_id      SERIAL        PRIMARY KEY,
    first_name     VARCHAR(50)   NOT NULL,
    last_name      VARCHAR(50)   NOT NULL,
    email          VARCHAR(100),
    phone          VARCHAR(20),
    address        TEXT,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- 3. Create Items Table
CREATE TABLE items (
    item_id        SERIAL        PRIMARY KEY,
    item_name      VARCHAR(100)  NOT NULL,
    description    TEXT,
    location       TEXT,
    created_at     TIMESTAMP     NOT NULL DEFAULT NOW()
);

-- 4. Create Checkouts Table
CREATE TABLE checkouts (
    checkout_id    SERIAL        PRIMARY KEY,
    item_id        INT           NOT NULL REFERENCES items(item_id),
    person_id      INT           NOT NULL REFERENCES people(person_id),
    checkout_date  TIMESTAMP     NOT NULL DEFAULT NOW(),
    due_date       DATE,
    return_date    TIMESTAMP
);

-- Optional: Create Indexes to Speed Up Lookups
CREATE INDEX idx_checkouts_item_id ON checkouts (item_id);
CREATE INDEX idx_checkouts_person_id ON checkouts (person_id);

COMMIT;