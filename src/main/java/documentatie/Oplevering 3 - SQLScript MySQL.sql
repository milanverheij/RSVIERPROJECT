-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema rsvierProjectDeel3
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `rsvierProjectDeel3` ;

-- -----------------------------------------------------
-- Schema rsvierProjectDeel3
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `rsvierProjectDeel3` DEFAULT CHARACTER SET utf8 ;
USE `rsvierProjectDeel3` ;

-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`klant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`klant` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`klant` (
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

CREATE UNIQUE INDEX `klant_id_UNIQUE` ON `rsvierProjectDeel3`.`klant` (`klantId` ASC);

CREATE UNIQUE INDEX `klant_uniekheid` ON `rsvierProjectDeel3`.`klant` (`voornaam` ASC, `achternaam` ASC, `email` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`bestelling`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`bestelling` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`bestelling` (
  `bestellingId` INT NOT NULL AUTO_INCREMENT COMMENT 'Unique want geen dubbele bestelnummers.\n\nAutoincrement zodat geen nummers worden overgeslagen.\n\nPrimary omdat bestel-ID het belangrijkste in in bestelling.',
  `klantId` INT NOT NULL COMMENT 'Not-null. Er moet altijd een klant gekoppeld zijn.\n\nForeign key met klant.',
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bestellingActief` TINYINT(1) NOT NULL DEFAULT 1,
  PRIMARY KEY (`bestellingId`),
  CONSTRAINT `klantId`
    FOREIGN KEY (`klantId`)
    REFERENCES `rsvierProjectDeel3`.`klant` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE UNIQUE INDEX `bestelling_id_UNIQUE` ON `rsvierProjectDeel3`.`bestelling` (`bestellingId` ASC);

CREATE INDEX `klant_id_idx` ON `rsvierProjectDeel3`.`bestelling` (`klantId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`adres`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`adres` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`adres` (
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

CREATE UNIQUE INDEX `uniekAdres` ON `rsvierProjectDeel3`.`adres` (`postcode` ASC, `huisnummer` ASC, `toevoeging` ASC);

CREATE UNIQUE INDEX `adres_id_UNIQUE` ON `rsvierProjectDeel3`.`adres` (`adresId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`klantHeeftAdres`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`klantHeeftAdres` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`klantHeeftAdres` (
  `klantIdKlant` INT NOT NULL,
  `adresIdAdres` INT NOT NULL,
  PRIMARY KEY (`klantIdKlant`, `adresIdAdres`),
  CONSTRAINT `klant_id_pa`
    FOREIGN KEY (`klantIdKlant`)
    REFERENCES `rsvierProjectDeel3`.`klant` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `adres_id_pa`
    FOREIGN KEY (`adresIdAdres`)
    REFERENCES `rsvierProjectDeel3`.`adres` (`adresId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `adres_id_idx` ON `rsvierProjectDeel3`.`klantHeeftAdres` (`adresIdAdres` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`artikel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`artikel` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`artikel` (
  `artikelId` INT NOT NULL AUTO_INCREMENT,
  `omschrijving` VARCHAR(45) NOT NULL,
  `prijsId` INT NOT NULL DEFAULT 0,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `verwachteLevertijd` VARCHAR(10) NULL,
  `inAssortiment` TINYINT(1) NOT NULL,
  PRIMARY KEY (`artikelId`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`prijs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`prijs` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`prijs` (
  `prijsId` INT NOT NULL AUTO_INCREMENT,
  `prijs` DECIMAL(10,2) NULL,
  `artikelId` INT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prijsId`),
  CONSTRAINT `artikel_id_pr`
    FOREIGN KEY (`artikelId`)
    REFERENCES `rsvierProjectDeel3`.`artikel` (`artikelId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `artikel_id_pr_idx` ON `rsvierProjectDeel3`.`prijs` (`artikelId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel3`.`bestellingHeeftArtikel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel3`.`bestellingHeeftArtikel` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel3`.`bestellingHeeftArtikel` (
  `bestellingIdBest` INT NOT NULL,
  `artikelIdArt` INT NOT NULL,
  `prijsIdPrijs` INT NOT NULL,
  `aantal` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`bestellingIdBest`, `artikelIdArt`),
  CONSTRAINT `bestelling_id_ba`
    FOREIGN KEY (`bestellingIdBest`)
    REFERENCES `rsvierProjectDeel3`.`bestelling` (`bestellingId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `artikel_id_ba`
    FOREIGN KEY (`artikelIdArt`)
    REFERENCES `rsvierProjectDeel3`.`artikel` (`artikelId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `prijs_id_best_ba`
    FOREIGN KEY (`prijsIdPrijs`)
    REFERENCES `rsvierProjectDeel3`.`prijs` (`prijsId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `artikel_id_idx` ON `rsvierProjectDeel3`.`bestellingHeeftArtikel` (`artikelIdArt` ASC);

CREATE INDEX `bestelling_id_idx` ON `rsvierProjectDeel3`.`bestellingHeeftArtikel` (`bestellingIdBest` ASC);

CREATE INDEX `prijs_id_ba_idx` ON `rsvierProjectDeel3`.`bestellingHeeftArtikel` (`prijsIdPrijs` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
