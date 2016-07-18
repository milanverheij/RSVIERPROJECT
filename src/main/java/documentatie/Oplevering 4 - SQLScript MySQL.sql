-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
-- -----------------------------------------------------
-- Schema rsvierProjectDeel4
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `rsvierProjectDeel4` ;

-- -----------------------------------------------------
-- Schema rsvierProjectDeel4
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `rsvierProjectDeel4` DEFAULT CHARACTER SET utf8 ;
USE `rsvierProjectDeel4` ;

-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`klant`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`klant` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`klant` (
  `klantId` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Primary key. Belangrijkste. \nAuto-increment zodat er geen nummers overgeslagen worden.\n\nNot null want is verplicht. \n\nUnique zodat er geen dubbele klantId’s voorkomen.',
  `voornaam` VARCHAR(50) NOT NULL COMMENT 'Not null. Bij iedere klant-id minimaal een voornaam.',
  `achternaam` VARCHAR(51) NOT NULL COMMENT 'Not null, bij iedere klant minstens een achternaam.',
  `tussenvoegsel` VARCHAR(10) NULL DEFAULT NULL,
  `email` VARCHAR(80) NULL DEFAULT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datumGewijzigd` TIMESTAMP NULL DEFAULT NULL,
  `klantActief` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`klantId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `klant_id_UNIQUE` ON `rsvierProjectDeel4`.`klant` (`klantId` ASC);

CREATE UNIQUE INDEX `klant_uniekheid` ON `rsvierProjectDeel4`.`klant` (`voornaam` ASC, `achternaam` ASC, `email` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`account`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`account` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`account` (
  `accountId` INT(11) NOT NULL AUTO_INCREMENT,
  `klantId` INT(11) NOT NULL,
  `accountNaam` VARCHAR(25) NULL DEFAULT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datumGewijzigd` TIMESTAMP NULL DEFAULT NULL,
  `accountActief` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`accountId`),
  CONSTRAINT `account_ibfk_1`
    FOREIGN KEY (`klantId`)
    REFERENCES `rsvierProjectDeel4`.`klant` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `klantId` ON `rsvierProjectDeel4`.`account` (`klantId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`adres`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`adres` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`adres` (
  `adresId` INT(11) NOT NULL AUTO_INCREMENT,
  `straatnaam` VARCHAR(26) NOT NULL,
  `postcode` VARCHAR(6) NOT NULL,
  `toevoeging` VARCHAR(6) NULL DEFAULT NULL,
  `huisnummer` VARCHAR(4) NOT NULL,
  `woonplaats` VARCHAR(45) NULL DEFAULT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `datumGewijzigd` TIMESTAMP NULL DEFAULT NULL,
  `adresActief` TINYINT(1) NULL DEFAULT '1',
  PRIMARY KEY (`adresId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `adres_id_UNIQUE` ON `rsvierProjectDeel4`.`adres` (`adresId` ASC);

CREATE UNIQUE INDEX `uniekAdres` ON `rsvierProjectDeel4`.`adres` (`postcode` ASC, `huisnummer` ASC, `toevoeging` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`adresType`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`adresType` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`adresType` (
  `adresTypeId` INT(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`adresTypeId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`artikel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`artikel` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`artikel` (
  `artikelId` INT(11) NOT NULL AUTO_INCREMENT,
  `omschrijving` VARCHAR(45) NOT NULL,
  `prijsId` INT(11) NOT NULL DEFAULT '0',
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `verwachteLevertijd` VARCHAR(10) NULL DEFAULT NULL,
  `inAssortiment` TINYINT(1) NOT NULL,
  PRIMARY KEY (`artikelId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`bestelling`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`bestelling` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`bestelling` (
  `bestellingId` INT(11) NOT NULL AUTO_INCREMENT COMMENT 'Unique want geen dubbele bestelnummers.\n\nAutoincrement zodat geen nummers worden overgeslagen.\n\nPrimary omdat bestel-ID het belangrijkste in in bestelling.',
  `klantId` INT(11) NOT NULL COMMENT 'Not-null. Er moet altijd een klant gekoppeld zijn.\n\nForeign key met klant.',
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `bestellingActief` TINYINT(1) NOT NULL DEFAULT '1',
  PRIMARY KEY (`bestellingId`),
  CONSTRAINT `klantId`
    FOREIGN KEY (`klantId`)
    REFERENCES `rsvierProjectDeel4`.`klant` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE UNIQUE INDEX `bestelling_id_UNIQUE` ON `rsvierProjectDeel4`.`bestelling` (`bestellingId` ASC);

CREATE INDEX `klant_id_idx` ON `rsvierProjectDeel4`.`bestelling` (`klantId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`prijs`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`prijs` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`prijs` (
  `prijsId` INT(11) NOT NULL AUTO_INCREMENT,
  `prijs` DECIMAL(10,2) NULL DEFAULT NULL,
  `artikelId` INT(11) NULL DEFAULT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`prijsId`),
  CONSTRAINT `artikel_id_pr`
    FOREIGN KEY (`artikelId`)
    REFERENCES `rsvierProjectDeel4`.`artikel` (`artikelId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `artikel_id_pr_idx` ON `rsvierProjectDeel4`.`prijs` (`artikelId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`bestellingHeeftArtikel`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`bestellingHeeftArtikel` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`bestellingHeeftArtikel` (
  `bestellingIdBest` INT(11) NOT NULL,
  `artikelIdArt` INT(11) NOT NULL,
  `prijsIdPrijs` INT(11) NOT NULL,
  `aantal` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`bestellingIdBest`, `artikelIdArt`),
  CONSTRAINT `artikel_id_ba`
    FOREIGN KEY (`artikelIdArt`)
    REFERENCES `rsvierProjectDeel4`.`artikel` (`artikelId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `bestelling_id_ba`
    FOREIGN KEY (`bestellingIdBest`)
    REFERENCES `rsvierProjectDeel4`.`bestelling` (`bestellingId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `prijs_id_best_ba`
    FOREIGN KEY (`prijsIdPrijs`)
    REFERENCES `rsvierProjectDeel4`.`prijs` (`prijsId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `artikel_id_idx` ON `rsvierProjectDeel4`.`bestellingHeeftArtikel` (`artikelIdArt` ASC);

CREATE INDEX `bestelling_id_idx` ON `rsvierProjectDeel4`.`bestellingHeeftArtikel` (`bestellingIdBest` ASC);

CREATE INDEX `prijs_id_ba_idx` ON `rsvierProjectDeel4`.`bestellingHeeftArtikel` (`prijsIdPrijs` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`betaalwijze`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`betaalwijze` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`betaalwijze` (
  `betaalwijzeId` INT(11) NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`betaalwijzeId`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`factuur`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`factuur` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`factuur` (
  `factuurId` INT(11) NOT NULL AUTO_INCREMENT,
  `bestellingId` INT(11) NOT NULL,
  `factuurNaam` VARCHAR(25) NULL DEFAULT NULL,
  `datumAanmaak` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`factuurId`),
  CONSTRAINT `factuur_ibfk_1`
    FOREIGN KEY (`bestellingId`)
    REFERENCES `rsvierProjectDeel4`.`bestelling` (`bestellingId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `bestellingId` ON `rsvierProjectDeel4`.`factuur` (`bestellingId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`betaling`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`betaling` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`betaling` (
  `betalingId` INT(11) NOT NULL AUTO_INCREMENT,
  `factuurId` INT(11) NOT NULL,
  `betaalwijzeId` INT(11) NOT NULL,
  `klantId` INT(11) NOT NULL,
  PRIMARY KEY (`betalingId`),
  CONSTRAINT `betaling_ibfk_1`
    FOREIGN KEY (`factuurId`)
    REFERENCES `rsvierProjectDeel4`.`factuur` (`factuurId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `betaling_ibfk_2`
    FOREIGN KEY (`betaalwijzeId`)
    REFERENCES `rsvierProjectDeel4`.`betaalwijze` (`betaalwijzeId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `betaling_ibfk_3`
    FOREIGN KEY (`klantId`)
    REFERENCES `rsvierProjectDeel4`.`klant` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `factuurId` ON `rsvierProjectDeel4`.`betaling` (`factuurId` ASC);

CREATE INDEX `betaalwijzeId` ON `rsvierProjectDeel4`.`betaling` (`betaalwijzeId` ASC);

CREATE INDEX `klantId` ON `rsvierProjectDeel4`.`betaling` (`klantId` ASC);


-- -----------------------------------------------------
-- Table `rsvierProjectDeel4`.`klantHeeftAdres`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `rsvierProjectDeel4`.`klantHeeftAdres` ;

CREATE TABLE IF NOT EXISTS `rsvierProjectDeel4`.`klantHeeftAdres` (
  `klantIdKlant` INT(11) NOT NULL,
  `adresIdAdres` INT(11) NOT NULL,
  `adresTypeId` INT(11) NOT NULL,
  PRIMARY KEY (`klantIdKlant`, `adresIdAdres`),
  CONSTRAINT `adres_id_pa`
    FOREIGN KEY (`adresIdAdres`)
    REFERENCES `rsvierProjectDeel4`.`adres` (`adresId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `klant_id_pa`
    FOREIGN KEY (`klantIdKlant`)
    REFERENCES `rsvierProjectDeel4`.`klant` (`klantId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `klantheeftadres_ibfk_1`
    FOREIGN KEY (`adresTypeId`)
    REFERENCES `rsvierProjectDeel4`.`adresType` (`adresTypeId`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf8;

CREATE INDEX `adresTypeId` ON `rsvierProjectDeel4`.`klantHeeftAdres` (`adresTypeId` ASC);

CREATE INDEX `adres_id_idx` ON `rsvierProjectDeel4`.`klantHeeftAdres` (`adresIdAdres` ASC);


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
