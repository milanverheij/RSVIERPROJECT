-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema RSVIERPROJECTDEEL2
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `RSVIERPROJECTDEEL2` ;

-- -----------------------------------------------------
-- Schema RSVIERPROJECTDEEL2
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `RSVIERPROJECTDEEL2` DEFAULT CHARACTER SET utf8 ;
USE `RSVIERPROJECTDEEL2` ;

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`KLANT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`KLANT` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`KLANT` (
  `klantId` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key. Belangrijkste. \nAuto-increment zodat er geen nummers overgeslagen worden.\n\nNot null want is verplicht. \n\nUnique zodat er geen dubbele klantId’s voorkomen.',
  `voornaam` VARCHAR(50) NOT NULL COMMENT 'Not null. Bij iedere klant-id minimaal een voornaam.',
  `achternaam` VARCHAR(51) NOT NULL COMMENT 'Not null, bij iedere klant minstens een achternaam.',
  `tussenvoegsel` VARCHAR(10) NULL,
  `email` VARCHAR(80) NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datumGewijzigd` TIMESTAMP NULL,
  `klantActief` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`klantId`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `klant_id_UNIQUE` ON `RSVIERPROJECTDEEL2`.`KLANT` (`klantId` ASC);

CREATE UNIQUE INDEX `klant_uniekheid` ON `RSVIERPROJECTDEEL2`.`KLANT` (`voornaam` ASC, `achternaam` ASC, `email` ASC);


-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`BESTELLING`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`BESTELLING` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`BESTELLING` (
  `bestelling_id` INT NOT NULL AUTO_INCREMENT COMMENT 'Unique want geen dubbele bestelnummers.\n\nAutoincrement zodat geen nummers worden overgeslagen.\n\nPrimary omdat bestel-ID het belangrijkste in in bestelling.',
  `klantId` INT NOT NULL COMMENT 'Not-null. Er moet altijd een klant gekoppeld zijn.\n\nForeign key met klant.',
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bestellingActief` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`bestelling_id`),
  CONSTRAINT `klantId`
    FOREIGN KEY (`klantId`)
    REFERENCES `RSVIERPROJECTDEEL2`.`KLANT` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `bestelling_id_UNIQUE` ON `RSVIERPROJECTDEEL2`.`BESTELLING` (`bestelling_id` ASC);

CREATE INDEX `klant_id_idx` ON `RSVIERPROJECTDEEL2`.`BESTELLING` (`klantId` ASC);


-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`ADRES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`ADRES` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`ADRES` (
  `adresId` INT NOT NULL AUTO_INCREMENT,
  `straatnaam` VARCHAR(26) NOT NULL,
  `postcode` VARCHAR(6) NOT NULL,
  `toevoeging` VARCHAR(6) NULL,
  `huisnummer` VARCHAR(4) NOT NULL,
  `woonplaats` VARCHAR(45) NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datumGewijzigd` TIMESTAMP NULL,
  `adresActief` TINYINT(1) NULL DEFAULT 1,
  PRIMARY KEY (`adresId`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `uniekAdres` ON `RSVIERPROJECTDEEL2`.`ADRES` (`postcode` ASC, `huisnummer` ASC, `toevoeging` ASC);

CREATE UNIQUE INDEX `adres_id_UNIQUE` ON `RSVIERPROJECTDEEL2`.`ADRES` (`adresId` ASC);


-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`KLANT_HEEFT_ADRES`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`KLANT_HEEFT_ADRES` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`KLANT_HEEFT_ADRES` (
  `klant_id_klant` INT NOT NULL,
  `adres_id_adres` INT NOT NULL,
  PRIMARY KEY (`klant_id_klant`, `adres_id_adres`),
  CONSTRAINT `klant_id_pa`
    FOREIGN KEY (`klant_id_klant`)
    REFERENCES `RSVIERPROJECTDEEL2`.`KLANT` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `adres_id_pa`
    FOREIGN KEY (`adres_id_adres`)
    REFERENCES `RSVIERPROJECTDEEL2`.`ADRES` (`adresId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `adres_id_idx` ON `RSVIERPROJECTDEEL2`.`KLANT_HEEFT_ADRES` (`adres_id_adres` ASC);


-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`PRIJS`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`PRIJS` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`PRIJS` (
  `prijs_id` INT NOT NULL,
  `prijs` DECIMAL(10,2) NULL,
  `artikel_id` INT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prijs_id`),
  CONSTRAINT `artikel_id_pr`
    FOREIGN KEY (`artikel_id`)
    REFERENCES `RSVIERPROJECTDEEL2`.`ARTIKEL` (`artikel_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `artikel_id_pr_idx` ON `RSVIERPROJECTDEEL2`.`PRIJS` (`artikel_id` ASC);


-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`ARTIKEL`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`ARTIKEL` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`ARTIKEL` (
  `artikel_id` INT NOT NULL AUTO_INCREMENT,
  `omschrijving` VARCHAR(45) NOT NULL,
  `prijs_id` INT NULL DEFAULT 0,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `verwachteLevertijd` VARCHAR(10) NULL,
  `inAssortisement` TINYINT(1) NOT NULL,
  PRIMARY KEY (`artikel_id`),
  CONSTRAINT `prijs_id_prijs`
    FOREIGN KEY (`prijs_id`)
    REFERENCES `RSVIERPROJECTDEEL2`.`PRIJS` (`prijs_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `prijs_id_prijs_idx` ON `RSVIERPROJECTDEEL2`.`ARTIKEL` (`prijs_id` ASC);


-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL` ;

CREATE TABLE IF NOT EXISTS `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL` (
  `bestelling_id_best` INT NOT NULL,
  `artikel_id_art` INT NOT NULL,
  `prijs_id_prijs` INT NOT NULL,
  `aantal` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`bestelling_id_best`, `artikel_id_art`),
  CONSTRAINT `bestelling_id_ba`
    FOREIGN KEY (`bestelling_id_best`)
    REFERENCES `RSVIERPROJECTDEEL2`.`BESTELLING` (`bestelling_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `artikel_id_ba`
    FOREIGN KEY (`artikel_id_art`)
    REFERENCES `RSVIERPROJECTDEEL2`.`ARTIKEL` (`artikel_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `prijs_id_best_ba`
    FOREIGN KEY (`prijs_id_prijs`)
    REFERENCES `RSVIERPROJECTDEEL2`.`PRIJS` (`prijs_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `artikel_id_idx` ON `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL` (`artikel_id_art` ASC);

CREATE INDEX `bestelling_id_idx` ON `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL` (`bestelling_id_best` ASC);

CREATE INDEX `prijs_id_ba_idx` ON `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL` (`prijs_id_prijs` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
