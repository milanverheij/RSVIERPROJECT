-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
DROP SCHEMA IF EXISTS `mydb` ;

-- -----------------------------------------------------
-- Schema mydb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `mydb` DEFAULT CHARACTER SET utf8 ;
USE `mydb` ;

-- -----------------------------------------------------
-- Table `mydb`.`KLANT`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`KLANT` ;

CREATE TABLE IF NOT EXISTS `mydb`.`KLANT` (
  `klant_id` INT NOT NULL AUTO_INCREMENT COMMENT 'Primary key. Belangrijkste. \nAuto-increment zodat er geen nummers overgeslagen worden.\n\nNot null want is verplicht. \n\nUnique zodat er geen dubbele klant_id’s voorkomen.',
  `voornaam` VARCHAR(50) NOT NULL COMMENT 'Not null. Bij iedere klant-id minimaal een voornaam.',
  `achternaam` VARCHAR(51) NOT NULL COMMENT 'Not null, bij iedere klant minstens een achternaam.',
  `tussenvoegsel` VARCHAR(10) NULL,
  `email` VARCHAR(80) NULL,
  `straatnaam` VARCHAR(26) NULL,
  `postcode` VARCHAR(6) NULL COMMENT '\n',
  `toevoeging` VARCHAR(6) NULL,
  `huisnummer` INT NULL,
  `woonplaats` VARCHAR(26) NULL,
  PRIMARY KEY (`klant_id`),
  UNIQUE INDEX `klant_id_UNIQUE` (`klant_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `mydb`.`BESTELLING`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `mydb`.`BESTELLING` ;

CREATE TABLE IF NOT EXISTS `mydb`.`BESTELLING` (
  `bestelling_id` INT NOT NULL AUTO_INCREMENT COMMENT 'Unique want geen dubbele bestelnummers.\n\nAutoincrement zodat geen nummers worden overgeslagen.\n\nPrimary omdat bestel-ID het belangrijkste in in bestelling.',
  `klant_id` INT NOT NULL COMMENT 'Not-null. Er moet altijd een klant gekoppeld zijn.\n\nForeign key met klant.',
  `artikel1_id` VARCHAR(10) NOT NULL COMMENT 'Bij artikel 1, not null. Zodat er op zijn minst 1 artikel besteld moet zijn. Voorkomt lege order?',
  `artikel2_id` VARCHAR(10) NULL,
  `artikel3_id` VARCHAR(10) NULL,
  `artikel1_naam` VARCHAR(45) NOT NULL COMMENT 'Bij artikel 1, not null. Zodat er op zijn minst 1 artikel besteld moet zijn. Voorkomt lege order?',
  `artikel2_naam` VARCHAR(45) NULL,
  `artikel3_naam` VARCHAR(45) NULL,
  `artikel1_prijs` DECIMAL(10,2) NOT NULL COMMENT 'Bij artikel 1, not null. Zodat er op zijn minst 1 artikel besteld moet zijn. Voorkomt lege order?',
  `artikel2_prijs` DECIMAL(10,2) NULL,
  `artikel3_prijs` DECIMAL(10,2) NULL,
  PRIMARY KEY (`bestelling_id`),
  UNIQUE INDEX `bestelling_id_UNIQUE` (`bestelling_id` ASC),
  INDEX `klant_id_idx` (`klant_id` ASC),
  CONSTRAINT `klant_id`
    FOREIGN KEY (`klant_id`)
    REFERENCES `mydb`.`KLANT` (`klant_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;
