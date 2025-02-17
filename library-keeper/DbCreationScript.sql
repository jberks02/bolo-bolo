-- 1. Drop Tables (in reverse dependency order)
DROP TRIGGER IF EXISTS people_table_audit_trigger ON people CASCADE;
DROP TRIGGER IF EXISTS items_table_audit_trigger ON items CASCADE;
DROP TRIGGER IF EXISTS checkout_table_audit_trigger ON checkouts CASCADE;
DROP TRIGGER IF EXISTS item_checkout_requests_audit_trigger ON item_checkout_requests CASCADE;
DROP TRIGGER IF EXISTS services_table_audit_trigger ON services CASCADE;
DROP TRIGGER IF EXISTS service_requests_audit_trigger ON service_requests CASCADE;
DROP TABLE IF EXISTS item_checkout_requests;
DROP TABLE IF EXISTS communications;
DROP TABLE IF EXISTS service_requests;
DROP TABLE IF EXISTS services;
DROP TABLE IF EXISTS checkouts;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS people;
DROP TABLE IF EXISTS people_history;
DROP TABLE IF EXISTS items_history;
DROP TABLE IF EXISTS checkouts_history;
DROP TABLE IF EXISTS communications;
DROP TABLE IF EXISTS item_checkout_requests_history;
DROP TABLE IF EXISTS services_history;
DROP TABLE IF EXISTS service_requests_history;

-- 2. Create relevant tables and their indexes

CREATE TABLE people (
    person_id		UUID			PRIMARY KEY,
    enc_password	VARCHAR(1000)	NOT NULL,
    profile_image   BYTEA,
    auth 			INT NOT NULL,
    first_name		VARCHAR(500)	NOT NULL,
    middle_name 	VARCHAR(500),
    last_name		VARCHAR(500)	NOT NULL,
    email			VARCHAR(500),
    created_at		TIMESTAMP	NOT NULL DEFAULT NOW(),
    updated_at		TIMESTAMP
);
CREATE INDEX idx_person_id ON people (person_id);
CREATE INDEX idx_person_names ON people (first_name, last_name);

CREATE TABLE items (
    item_id		UUID			PRIMARY KEY,
    item_image	BYTEA 			NOT NULL,
    owner_id	UUID			NOT NULL,
    item_name	VARCHAR(300)	NOT NULL,
    category	VARCHAR(300)	NOT NULL,
    description	VARCHAR(500),
    location	VARCHAR(500),
    updated_at	TIMESTAMP,
    created_at	TIMESTAMP		NOT NULL DEFAULT NOW(),
    CONSTRAINT fk_owner_must_be_person
    	FOREIGN KEY (owner_id)
    	REFERENCES people (person_id)
    	ON DELETE NO ACTION
    	ON UPDATE NO ACTION
);
CREATE INDEX idx_items_item_id ON items (item_id);
CREATE INDEX idx_items_owner_id ON items (owner_id);

CREATE TABLE services (
	service_id		UUID PRIMARY KEY,
	servicer_id 	UUID NOT NULL,
	service_image	BYTEA,
	service_name	VARCHAR(400) NOT NULL,
	description 	VARCHAR(500) NOT NULL,
	category 		VARCHAR(100) NOT NULL,
	created_at 		TIMESTAMP,
	updated_at 		TIMESTAMP,
	CONSTRAINT fk_servicer_is_registered
		FOREIGN KEY (servicer_id)
		REFERENCES people (person_id)
		ON DELETE CASCADE
		ON UPDATE NO ACTION
);
CREATE INDEX idx_service_service_id ON services (service_id);
CREATE INDEX idx_service_servicer_id ON services (servicer_id);
CREATE INDEX idx_service_category ON services (category);

CREATE TABLE service_requests (
	service_request_id	UUID PRIMARY KEY,
	requester_id		UUID NOT NULL,
	service_id			UUID NOT NULL,
	requested_at		TIMESTAMP NOT NULL,
	service_date		TIMESTAMP NOT NULL,
	fullfilled			BOOLEAN NOT NULL,
	CONSTRAINT fk_service_requester
		FOREIGN KEY (requester_id)
		REFERENCES people (person_id)
		ON DELETE CASCADE,
	CONSTRAINT fk_service_id
		FOREIGN KEY (service_id)
		REFERENCES services (service_id)
		ON DELETE CASCADE
);
CREATE INDEX idx_service_requests_id ON service_requests (service_request_id);
CREATE INDEX idx_service_requests_requester ON service_requests (requester_id);
CREATE INDEX idx_service_requests_service ON service_requests (service_id);


CREATE TABLE checkouts (
    checkout_id    UUID         PRIMARY KEY,
    item_id        UUID         NOT NULL,
    person_id      UUID         NOT NULL,
    checkout_date  TIMESTAMP    NOT NULL DEFAULT NOW(),
    due_date       TIMESTAMP 	NOT NULL,
    return_date    TIMESTAMP,
    CONSTRAINT fk_checkout_is_valid_item
    	FOREIGN KEY (item_id)
    	REFERENCES items (item_id)
    	ON DELETE CASCADE
    	ON UPDATE NO ACTION,
    CONSTRAINT fk_valid_person_must_checkout
    	FOREIGN KEY (person_id)
    	REFERENCES people (person_id)
    	ON DELETE CASCADE
    	ON UPDATE NO ACTION
);
CREATE INDEX idx_checkouts_item_id ON checkouts (item_id);
CREATE INDEX idx_checkouts_person_id ON checkouts (person_id);
CREATE INDEX idx_checkouts_checkout_id ON checkouts (checkout_id);

CREATE TABLE communications (
    message_id		UUID		PRIMARY KEY,
    sender_id    	UUID		NOT NULL,
    recipient_id	UUID		NOT NULL,
    action_id		UUID		NOT NULL,
    message			varchar(500)		NOT NULL,
    sent_at			TIMESTAMP	NOT NULL DEFAULT NOW(),
    read_at			TIMESTAMP,
    CONSTRAINT fk_sender
        FOREIGN KEY (sender_id)
        REFERENCES people (person_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_recipient
        FOREIGN KEY (recipient_id)
        REFERENCES people (person_id)
        ON DELETE CASCADE,
    CONSTRAINT fk_checkout_id
    	FOREIGN KEY (action_id)
    	REFERENCES checkouts (checkout_id)
    	ON DELETE CASCADE,
    CONSTRAINT fk_service_id_on_comm
    	FOREIGN KEY (action_id)
    	REFERENCES service_requests (service_request_id)
    	ON DELETE CASCADE
);
CREATE INDEX idx_communications_message_id  ON communications (message_id);
CREATE INDEX idx_communications_recepient ON communications (recipient_id);
CREATE INDEX idx_communications_sender ON communications (sender_id);
CREATE INDEX idx_communications_both ON communications (sender_id, recipient_id);

CREATE TABLE item_checkout_requests (
    request_id    		UUID DEFAULT gen_random_uuid() PRIMARY KEY,
    requester_id  		UUID NOT NULL,
    item_id       		UUID NOT NULL,
    requested_due_date	TIMESTAMP NOT NULL,
    requested_checkout_date TIMESTAMP NOT NULL,
    requested_at		TIMESTAMP NOT NULL DEFAULT NOW(),

    CONSTRAINT fk_requester
        FOREIGN KEY (requester_id)
        REFERENCES people (person_id)
        ON DELETE CASCADE,

    CONSTRAINT fk_item
        FOREIGN KEY (item_id)
        REFERENCES items (item_id)
        ON DELETE CASCADE
);
CREATE INDEX idx_item_checkout_requests_id ON item_checkout_requests (request_id);
CREATE INDEX idx_item_checkout_requester_id ON item_checkout_requests (requester_id);
CREATE INDEX idx_item_checkout_request_item ON item_checkout_requests (item_id);

-- 3. create historical tables for each existing table

CREATE TABLE people_history (
    person_id		UUID			NOT NULL,
    profile_image   BYTEA,
    auth 			INT				NOT NULL,
    first_name		VARCHAR(200)	NOT NULL,
    last_name		VARCHAR(200)	NOT NULL,
    created_at		TIMESTAMP		NOT NULL,
    updated_at		TIMESTAMP,
    operation_type	VARCHAR(6) 		NOT NULL,
    changed_at		TIMESTAMP 		NOT NULL
);
CREATE INDEX idx_person_id_history ON people_history (person_id);
CREATE INDEX idx_person_names_history ON people_history (first_name, last_name);
REVOKE INSERT, UPDATE, DELETE ON people_history FROM PUBLIC;

CREATE TABLE items_history (
    item_id			UUID			NOT NULL,
    item_image		BYTEA 			NOT NULL,
    owner_id		UUID 			NOT NULL,
    item_name		VARCHAR(300) 	NOT NULL,
    category		VARCHAR(300)	NOT NULL,
    description		VARCHAR(500),
    location		VARCHAR(500),
    updated_at		TIMESTAMP,
    created_at		TIMESTAMP 		NOT NULL,
    operation_type	VARCHAR(6) 		NOT NULL,
    changed_at		TIMESTAMP 		NOT NULL
);
CREATE INDEX idx_items_item_id_history ON items_history (item_id);
CREATE INDEX idx_items_owner_id_history ON items_history (owner_id);
REVOKE INSERT, UPDATE, DELETE ON items_history FROM PUBLIC;

CREATE TABLE checkouts_history (
    checkout_id    	UUID        NOT NULL,
    item_id        	UUID        NOT NULL,
    person_id      	UUID        NOT NULL,
    checkout_date	TIMESTAMP   NOT NULL DEFAULT NOW(),
    due_date       	TIMESTAMP	NOT NULL,
    return_date    	TIMESTAMP,
    operation_type	VARCHAR(6)	NOT NULL,
    changed_at		TIMESTAMP	NOT NULL
);
CREATE INDEX idx_checkouts_item_id_history ON checkouts_history (item_id);
CREATE INDEX idx_checkouts_person_id_history ON checkouts_history (person_id);
CREATE INDEX idx_checkouts_checkout_id_history ON checkouts_history (checkout_id);
REVOKE INSERT, UPDATE, DELETE ON checkouts_history FROM PUBLIC;

CREATE TABLE item_checkout_requests_history (
    request_id				UUID NOT NULL,
    requester_id 			UUID NOT NULL,
    item_id					UUID NOT NULL,
    requested_due_date		TIMESTAMP NOT NULL,
    requested_checkout_date TIMESTAMP NOT NULL,
    requested_at			TIMESTAMP NOT NULL DEFAULT NOW(),
    operation_type			VARCHAR(6) 		NOT NULL,
    changed_at				TIMESTAMP 		NOT NULL
);
CREATE INDEX idx_item_checkout_requests_history_request_id ON item_checkout_requests_history (request_id);
CREATE INDEX idx_item_checkout_requests_history_requester_id ON item_checkout_requests_history (requester_id);
CREATE INDEX idx_item_checkout_requests_history_item_id ON item_checkout_requests_history (item_id);
REVOKE INSERT, UPDATE, DELETE ON item_checkout_requests_history FROM PUBLIC;

CREATE TABLE services_history (
	service_id		UUID PRIMARY KEY,
	servicer_id 	UUID NOT NULL,
	service_image	BYTEA,
	service_name	VARCHAR(400),
	description 	VARCHAR(500),
	category 		VARCHAR(100),
	created_at 		TIMESTAMP,
	updated_at 		TIMESTAMP,
	operation_type	VARCHAR(6)	NOT NULL,
    changed_at		TIMESTAMP	NOT NULL
);
CREATE INDEX idx_service_history_service ON services_history (service_id);
CREATE INDEX idx_service_history_servicer ON services_history (servicer_id);
CREATE INDEX idx_service_history_service_name ON services_history (service_name);
REVOKE INSERT, UPDATE, DELETE ON services_history FROM PUBLIC;

CREATE TABLE service_requests_history (
	service_request_id	UUID PRIMARY KEY,
	requester_id		UUID NOT NULL,
	service_id			UUID NOT NULL,
	requested_at		TIMESTAMP NOT NULL,
	service_date		TIMESTAMP NOT NULL,
	fullfilled			BOOLEAN NOT NULL,
	operation_type		VARCHAR(6) 		NOT NULL,
    changed_at			TIMESTAMP 		NOT NULL
);
CREATE INDEX idx_service_requests_history_service_request_id ON service_requests_history (service_request_id);
CREATE INDEX idx_service_requests_history_service_requester_id ON service_requests_history (requester_id);
CREATE INDEX idx_service_requests_history_service_service_id ON service_requests_history (service_id);
REVOKE INSERT, UPDATE, DELETE ON service_requests_history FROM PUBLIC;

CREATE TABLE new_user_tokens (
	token		VARCHAR(1000)	PRIMARY KEY,
	created_at	TIMESTAMP NOT NULL,
	expiration	TIMESTAMP NOT NULL,
	valid		BOOLEAN NOT NULL DEFAULT(true)
)
CREATE INDEX idx_new_user_tokens_token ON new_user_tokens ("token")
I
-- 4. Create trigger functions to capture changes into our historical tables

CREATE OR REPLACE FUNCTION people_table_audit()
RETURNS TRIGGER AS $$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO people_history (
		person_id, profile_image, auth, first_name, last_name,
		created_at, updated_at, operation_type, changed_at
	)
    VALUES (
		NEW.person_id, NEW.profile_image, NEW.auth, NEW.first_name, NEW.last_name,
		NEW.created_at, NEW.updated_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
    INSERT INTO people_history (
		person_id, profile_image, auth, first_name, last_name,
		created_at, updated_at, operation_type, changed_at
	)
    VALUES (
		NEW.person_id, NEW.profile_image, NEW.auth, NEW.first_name, NEW.last_name,
		NEW.created_at, NEW.updated_at, 'UPDATE', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'DELETE') THEN
     INSERT INTO people_history (
		person_id, profile_image, auth, first_name, last_name,
		created_at, updated_at, operation_type, changed_at
	)
    VALUES (
		OLD.person_id, OLD.profile_image, OLD.auth, OLD.first_name, OLD.last_name,
		OLD.created_at, OLD.updated_at, 'DELETE', NOW()
	);
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION items_table_audit()
RETURNS TRIGGER AS $$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO items_history (
		item_id, item_image, owner_id, item_name, description, location,
		category, updated_at, created_at, operation_type, changed_at
	)
    VALUES (
		NEW.item_id, NEW.item_image, NEW.owner_id, NEW.item_name, NEW.description, NEW.location,
		NEW.category, NEW.updated_at, NEW.created_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
    INSERT INTO items_history (
		item_id, item_image, owner_id, item_name, description, location,
		category, updated_at, created_at, operation_type, changed_at
	)
    VALUES (
		NEW.item_id, NEW.item_image, NEW.owner_id, NEW.item_name, NEW.description, NEW.location,
		NEW.category, NEW.updated_at, NEW.created_at, 'UPDATE', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'DELETE') THEN
     INSERT INTO items_history (
		item_id, item_image, owner_id, item_name, description, location,
		category, updated_at, created_at, operation_type, changed_at
	)
    VALUES (
		OLD.item_id, OLD.item_image, OLD.owner_id, OLD.item_name, OLD.description, OLD.location,
		OLD.category, OLD.updated_at, OLD.created_at, 'DELETE', NOW()
	);
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION checkout_table_audit()
RETURNS TRIGGER AS $$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO checkouts_history (
		checkout_id, item_id, person_id, checkout_date, due_date,
		return_date, operation_type, changed_at
	)
    VALUES (
		NEW.checkout_id, NEW.item_id, NEW.person_id, NEW.checkout_date, NEW.due_date,
		NEW.return_date, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
    INSERT INTO checkouts_history (
		checkout_id, item_id, person_id, checkout_date, due_date,
		return_date, operation_type, changed_at
	)
    VALUES (
		NEW.checkout_id, NEW.item_id, NEW.person_id, NEW.checkout_date, NEW.due_date,
		NEW.return_date, 'UPDATE', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'DELETE') THEN
     INSERT INTO checkouts_history (
		checkout_id, item_id, person_id, checkout_date, due_date,
		return_date, operation_type, changed_at
	)
    VALUES (
		OLD.checkout_id, OLD.item_id, OLD.person_id, OLD.checkout_date, OLD.due_date,
		OLD.return_date, 'DELETE', NOW()
	);
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION item_checkout_request_table_audit()
RETURNS TRIGGER AS $$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO item_checkout_requests_history (
		request_id, requester_id, item_id, requested_due_date, requested_checkout_date,
   	 	requested_at, operation_type, changed_at
	)
    VALUES (
		NEW.request_id, NEW.requester_id, NEW.item_id,
		NEW.requested_due_date, NEW.requested_checkout_date, NEW.requested_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
    INSERT INTO item_checkout_requests_history (
		request_id, requester_id, item_id, requested_due_date, requested_checkout_date,
   	 	requested_at, operation_type, changed_at
	)
    VALUES (
		NEW.request_id, NEW.requester_id, NEW.item_id,
		NEW.requested_due_date, NEW.requested_checkout_date, NEW.requested_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'DELETE') THEN
     INSERT INTO item_checkout_requests_history (
		request_id, requester_id, item_id, requested_due_date, requested_checkout_date,
   	 	requested_at, operation_type, changed_at
	)
    VALUES (
		OLD.request_id, OLD.requester_id, OLD.item_id,
		OLD.requested_due_date, OLD.requested_checkout_date, OLD.requested_at, 'INSERT', NOW()
	);
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION services_table_audit()
RETURNS TRIGGER AS $$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO services_history (
		service_id, servicer_id, service_image, service_name, description,
		category, created_at, updated_at, operation_type, changed_at
	)
    VALUES (
		NEW.service_id, NEW.servicer_id, NEW.service_image, NEW.service_name, NEW.description,
		NEW.category, NEW.created_at, NEW.updated_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
    INSERT INTO services_history (
		service_id, servicer_id, service_image, service_name, description,
		category, created_at, updated_at, operation_type, changed_at
	)
    VALUES (
		NEW.service_id, NEW.servicer_id, NEW.service_image, NEW.service_name, NEW.description,
		NEW.category, NEW.created_at, NEW.updated_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'DELETE') THEN
     INSERT INTO services_history (
		service_id, servicer_id, service_image, service_name, description,
		category, created_at, updated_at, operation_type, changed_at
	)
    VALUES (
		OLD.service_id, OLD.servicer_id, OLD.service_image, OLD.service_name, OLD.description,
		OLD.category, OLD.created_at, OLD.updated_at, 'INSERT', NOW()
	);
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

CREATE OR REPLACE FUNCTION services_requests_table_audit()
RETURNS TRIGGER AS $$
BEGIN
  IF (TG_OP = 'INSERT') THEN
    INSERT INTO service_requests_history (
		service_request_id, requester_id, service_id, service_date, fullfilled,
		requested_at, operation_type, changed_at
	)
    VALUES (
		NEW.service_request_id, NEW.requester_id, NEW.service_id,  NEW.service_date, NEW.fullfilled,
		NEW.requested_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'UPDATE') THEN
    INSERT INTO service_requests_history (
		service_request_id, requester_id, service_id, service_date, fullfilled,
		requested_at, operation_type, changed_at
	)
    VALUES (
		NEW.service_request_id, NEW.requester_id, NEW.service_id, NEW.service_date, NEW.fullfilled,
		NEW.requested_at, 'INSERT', NOW()
	);
    RETURN NEW;
  ELSIF (TG_OP = 'DELETE') THEN
     INSERT INTO service_requests_history (
		service_request_id, requester_id, service_id, service_date, fullfilled,
		requested_at, operation_type, changed_at
	)
    VALUES (
		OLD.service_request_id, OLD.requester_id, OLD.service_id, OLD.service_date, OLD.fullfilled,
		OLD.requested_at, 'INSERT', NOW()
	);
    RETURN OLD;
  END IF;
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- 5. Tie trigger functions to insert, update, and delete actions on tables

CREATE TRIGGER people_table_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON people
FOR EACH ROW
EXECUTE FUNCTION people_table_audit();

CREATE TRIGGER items_table_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON items
FOR EACH ROW
EXECUTE FUNCTION items_table_audit();

CREATE TRIGGER checkout_table_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON checkouts
FOR EACH ROW
EXECUTE FUNCTION checkout_table_audit();

CREATE TRIGGER item_checkout_requests_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON item_checkout_requests
FOR EACH ROW
EXECUTE FUNCTION item_checkout_request_table_audit();

CREATE TRIGGER services_table_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON services
FOR EACH ROW
EXECUTE FUNCTION services_table_audit();

CREATE TRIGGER service_requests_audit_trigger
AFTER INSERT OR UPDATE OR DELETE
ON service_requests
FOR EACH ROW
EXECUTE FUNCTION services_requests_table_audit();
