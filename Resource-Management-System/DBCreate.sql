-- Disable foreign key checks
SET FOREIGN_KEY_CHECKS = 0;

-- Drop existing triggers
-- Triggers for Items table
DROP TRIGGER IF EXISTS RMS.Items_AFTER_INSERT;
DROP TRIGGER IF EXISTS RMS.`Items_AFTER_UPDATE`;
DROP TRIGGER IF EXISTS RMS.`Items_AFTER_DELETE`;

-- Triggers for Resource table
DROP TRIGGER IF EXISTS RMS.`Resource_AFTER_INSERT`;
DROP TRIGGER IF EXISTS RMS.`Resource_AFTER_UPDATE`;
DROP TRIGGER IF EXISTS RMS.`Resource_AFTER_DELETE`;

-- Triggers for Resource_Request table
DROP TRIGGER IF EXISTS RMS.`Resource_Request_AFTER_INSERT`;
DROP TRIGGER IF EXISTS RMS.`Resource_Request_AFTER_UPDATE`;
DROP TRIGGER IF EXISTS RMS.`Resource_Request_AFTER_DELETE`;

-- Drop existing tables
DROP TABLE IF EXISTS RMS.`Resource_Request_History`;
DROP TABLE IF EXISTS RMS.`Resource_Request`;
DROP TABLE IF EXISTS RMS.`Resource_History`;
DROP TABLE IF EXISTS RMS.`Resource`;
DROP TABLE IF EXISTS RMS.`Items_History`;
DROP TABLE IF EXISTS RMS.`Items`;

-- Re-enable foreign key checks
SET FOREIGN_KEY_CHECKS = 1;

-- Proceed with table creations
-- Create Items table
CREATE TABLE `Items` (
    `itemId` VARCHAR(50) NOT NULL,
    `description` VARCHAR(500) CHARACTER SET utf8mb4,
    `commonNames` JSON,
    `documentationUrls` JSON,
    `created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdated` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`itemId`)
) ENGINE=InnoDB;

-- Create Items_History table
CREATE TABLE `Items_History` (
    `historyId` BIGINT NOT NULL,
    `itemId` VARCHAR(50) NOT NULL,
    `description` VARCHAR(500) CHARACTER SET utf8mb4,
    `commonNames` JSON,
    `documentationUrls` JSON,
    `created` DATETIME,
    `lastUpdated` DATETIME,
    `operation` ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    `operationTimestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`historyId`)
) ENGINE=InnoDB;

-- Triggers for Items table
DELIMITER $$

CREATE TRIGGER `Items_AFTER_INSERT` AFTER INSERT ON `Items`
FOR EACH ROW
BEGIN
    INSERT INTO `Items_History` (
        `itemId`, `description`, `commonNames`, `documentationUrls`, `created`, `lastUpdated`, `operation`
    ) VALUES (
        NEW.`itemId`, NEW.`description`, NEW.`commonNames`, NEW.`documentationUrls`, NEW.`created`, NEW.`lastUpdated`, 'INSERT'
    );
END $$

CREATE TRIGGER `Items_AFTER_UPDATE` AFTER UPDATE ON `Items`
FOR EACH ROW
BEGIN
    INSERT INTO `Items_History` (
        `itemId`, `description`, `commonNames`, `documentationUrls`, `created`, `lastUpdated`, `operation`
    ) VALUES (
        NEW.`itemId`, NEW.`description`, NEW.`commonNames`, NEW.`documentationUrls`, NEW.`created`, NEW.`lastUpdated`, 'UPDATE'
    );
END $$

CREATE TRIGGER `Items_AFTER_DELETE` AFTER DELETE ON `Items`
FOR EACH ROW
BEGIN
    INSERT INTO `Items_History` (
        `itemId`, `description`, `commonNames`, `documentationUrls`, `created`, `lastUpdated`, `operation`
    ) VALUES (
        OLD.`itemId`, OLD.`description`, OLD.`commonNames`, OLD.`documentationUrls`, OLD.`created`, OLD.`lastUpdated`, 'DELETE'
    );
END $$

DELIMITER ;

-- Create Resource table
CREATE TABLE `Resource` (
    `id` VARCHAR(36) NOT NULL,
    `priority` INT,
    `priorityText` VARCHAR(100),
    `itemId` VARCHAR(50) NOT NULL,
    `concern` VARCHAR(255) CHARACTER SET utf8mb4,
    `lotWeight` FLOAT,
    `weightUnits` VARCHAR(10),
    `lotCount` INT,
    `originBolo` VARCHAR(50),
    `residingBolo` VARCHAR(50),
    `status` INT,
    `statusDescription` VARCHAR(100),
    `volume` FLOAT,
    `volumeUnits` VARCHAR(10),
    `location` VARCHAR(100),
    `created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdated` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_itemId` (`itemId`),
    CONSTRAINT `fk_itemId` FOREIGN KEY (`itemId`) REFERENCES `Items`(`itemId`)
) ENGINE=InnoDB;

-- Create Resource_History table
CREATE TABLE `Resource_History` (
    `historyId` BIGINT NOT NULL,
    `id` CHAR(36) NOT NULL,
    `priority` INT,
    `priorityText` VARCHAR(100),
    `itemId` VARCHAR(50),
    `concern` VARCHAR(255) CHARACTER SET utf8mb4,
    `lotWeight` FLOAT,
    `weightUnits` VARCHAR(10),
    `lotCount` INT,
    `originBolo` VARCHAR(50),
    `residingBolo` VARCHAR(50),
    `status` INT,
    `statusDescription` VARCHAR(100),
    `volume` FLOAT,
    `volumeUnits` VARCHAR(10),
    `location` VARCHAR(100),
    `created` DATETIME,
    `lastUpdated` DATETIME,
    `operation` ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    `operationTimestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`historyId`)
) ENGINE=InnoDB;

-- Triggers for Resource table
DELIMITER $$

CREATE TRIGGER `Resource_AFTER_INSERT` AFTER INSERT ON `Resource`
FOR EACH ROW
BEGIN
    INSERT INTO `Resource_History` (
        `id`, `priority`, `priorityText`, `itemId`, `concern`, `lotWeight`, `weightUnits`, `lotCount`,
        `originBolo`, `residingBolo`, `status`, `statusDescription`, `volume`, `volumeUnits`, `location`,
        `created`, `lastUpdated`, `operation`
    ) VALUES (
        NEW.`id`, NEW.`priority`, NEW.`priorityText`, NEW.`itemId`, NEW.`concern`, NEW.`lotWeight`, NEW.`weightUnits`, NEW.`lotCount`,
        NEW.`originBolo`, NEW.`residingBolo`, NEW.`status`, NEW.`statusDescription`, NEW.`volume`, NEW.`volumeUnits`, NEW.`location`,
        NEW.`created`, NEW.`lastUpdated`, 'INSERT'
    );
END $$

CREATE TRIGGER `Resource_AFTER_UPDATE` AFTER UPDATE ON `Resource`
FOR EACH ROW
BEGIN
    INSERT INTO `Resource_History` (
        `id`, `priority`, `priorityText`, `itemId`, `concern`, `lotWeight`, `weightUnits`, `lotCount`,
        `originBolo`, `residingBolo`, `status`, `statusDescription`, `volume`, `volumeUnits`, `location`,
        `created`, `lastUpdated`, `operation`
    ) VALUES (
        NEW.`id`, NEW.`priority`, NEW.`priorityText`, NEW.`itemId`, NEW.`concern`, NEW.`lotWeight`, NEW.`weightUnits`, NEW.`lotCount`,
        NEW.`originBolo`, NEW.`residingBolo`, NEW.`status`, NEW.`statusDescription`, NEW.`volume`, NEW.`volumeUnits`, NEW.`location`,
        NEW.`created`, NEW.`lastUpdated`, 'UPDATE'
    );
END $$

CREATE TRIGGER `Resource_AFTER_DELETE` AFTER DELETE ON `Resource`
FOR EACH ROW
BEGIN
    INSERT INTO `Resource_History` (
        `id`, `priority`, `priorityText`, `itemId`, `concern`, `lotWeight`, `weightUnits`, `lotCount`,
        `originBolo`, `residingBolo`, `status`, `statusDescription`, `volume`, `volumeUnits`, `location`,
        `created`, `lastUpdated`, `operation`
    ) VALUES (
        OLD.`id`, OLD.`priority`, OLD.`priorityText`, OLD.`itemId`, OLD.`concern`, OLD.`lotWeight`, OLD.`weightUnits`, OLD.`lotCount`,
        OLD.`originBolo`, OLD.`residingBolo`, OLD.`status`, OLD.`statusDescription`, OLD.`volume`, OLD.`volumeUnits`, OLD.`location`,
        OLD.`created`, OLD.`lastUpdated`, 'DELETE'
    );
END $$

DELIMITER ;

-- Create Resource_Request table
CREATE TABLE `Resource_Request` (
    `id` VARCHAR(36) NOT NULL,
    `itemId` VARCHAR(50) NOT NULL,
    `requestingBolo` VARCHAR(50),
    `respondingBolo` VARCHAR(50),
    `confirmed` BOOLEAN,
    `volume` FLOAT,
    `volumeUnits` VARCHAR(10),
    `lotWeight` FLOAT,
    `weightUnits` VARCHAR(10),
    `lotCount` INT,
    `created` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `lastUpdated` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (`id`),
    INDEX `idx_itemId` (`itemId`),
    CONSTRAINT `fk_rr_itemId` FOREIGN KEY (`itemId`) REFERENCES `Items`(`itemId`)
) ENGINE=InnoDB;

-- Create Resource_Request_History table
CREATE TABLE `Resource_Request_History` (
    `historyId` BIGINT NOT NULL,
    `id` CHAR(36) NOT NULL,
    `itemId` VARCHAR(50) NOT NULL,
    `requestingBolo` VARCHAR(50),
    `respondingBolo` VARCHAR(50),
    `confirmed` BOOLEAN,
    `volume` FLOAT,
    `volumeUnits` VARCHAR(10),
    `lotWeight` FLOAT,
    `weightUnits` VARCHAR(10),
    `lotCount` INT,
    `created` DATETIME,
    `lastUpdated` DATETIME,
    `operation` ENUM('INSERT', 'UPDATE', 'DELETE') NOT NULL,
    `operationTimestamp` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (`historyId`)
) ENGINE=InnoDB;

-- Triggers for Resource_Request table
DELIMITER $$

CREATE TRIGGER `Resource_Request_AFTER_INSERT` AFTER INSERT ON `Resource_Request`
FOR EACH ROW
BEGIN
    INSERT INTO `Resource_Request_History` (
        `id`, `itemId`, `requestingBolo`, `respondingBolo`, `confirmed`, `volume`, `volumeUnits`,
        `lotWeight`, `weightUnits`, `lotCount`, `created`, `lastUpdated`, `operation`
    ) VALUES (
        NEW.`id`, NEW.`itemId`, NEW.`requestingBolo`, NEW.`respondingBolo`, NEW.`confirmed`, NEW.`volume`, NEW.`volumeUnits`,
        NEW.`lotWeight`, NEW.`weightUnits`, NEW.`lotCount`, NEW.`created`, NEW.`lastUpdated`, 'INSERT'
    );
END $$

CREATE TRIGGER `Resource_Request_AFTER_UPDATE` AFTER UPDATE ON `Resource_Request`
FOR EACH ROW
BEGIN
    INSERT INTO `Resource_Request_History` (
        `id`, `itemId`, `requestingBolo`, `respondingBolo`, `confirmed`, `volume`, `volumeUnits`,
        `lotWeight`, `weightUnits`, `lotCount`, `created`, `lastUpdated`, `operation`
    ) VALUES (
        NEW.`id`, NEW.`itemId`, NEW.`requestingBolo`, NEW.`respondingBolo`, NEW.`confirmed`, NEW.`volume`, NEW.`volumeUnits`,
        NEW.`lotWeight`, NEW.`weightUnits`, NEW.`lotCount`, NEW.`created`, NEW.`lastUpdated`, 'UPDATE'
    );
END $$

CREATE TRIGGER `Resource_Request_AFTER_DELETE` AFTER DELETE ON `Resource_Request`
FOR EACH ROW
BEGIN
    INSERT INTO `Resource_Request_History` (
        `id`, `itemId`, `requestingBolo`, `respondingBolo`, `confirmed`, `volume`, `volumeUnits`,
        `lotWeight`, `weightUnits`, `lotCount`, `created`, `lastUpdated`, `operation`
    ) VALUES (
        OLD.`id`, OLD.`itemId`, OLD.`requestingBolo`, OLD.`respondingBolo`, OLD.`confirmed`, OLD.`volume`, OLD.`volumeUnits`,
        OLD.`lotWeight`, OLD.`weightUnits`, OLD.`lotCount`, OLD.`created`, OLD.`lastUpdated`, 'DELETE'
    );
END $$

DELIMITER ;