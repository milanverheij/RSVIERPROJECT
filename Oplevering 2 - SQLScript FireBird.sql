CREATE DATABASE 'RSVIERPROJECTDEEL2.fdb' DEFAULT CHARACTER SET utf8;

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`KLANT'`
-- -----------------------------------------------------
RECREATE TABLE KLANT (
  klant_id INT NOT NULL PRIMARY KEY,
  voornaam VARCHAR(50) NOT NULL,
  achternaam VARCHAR(51) NOT NULL,
  tussenvoegsel VARCHAR(10), 
  email VARCHAR(80),
  datumAanmaak TIMESTAMP DEFAULT 'NOW' NOT NULL,
  datumGewijzigd TIMESTAMP,
  klantActief CHAR(1) DEFAULT 1);

CREATE UNIQUE INDEX klant_id_UNIQUE ON KLANT (klant_id);
CREATE UNIQUE INDEX klant_uniekheid ON KLANT (voornaam, achternaam, email);

CREATE GENERATOR gen_klant_id;
SET GENERATOR gen_klant_id TO 0;

SET TERM ^ ; 
CREATE TRIGGER trigger_klant_id FOR klant
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW."KLANT_ID" IS NULL) 
THEN NEW."KLANT_ID" = GEN_ID(gen_klant_id, 1); 
END^
SET TERM ; ^

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`ADRES`
-- -----------------------------------------------------
RECREATE TABLE ADRES (
  adres_id INT NOT NULL PRIMARY KEY,
  straatnaam VARCHAR(26) NOT NULL,
  postcode VARCHAR(6) NOT NULL,
  toevoeging VARCHAR(45),
  huisnummer VARCHAR(4) NOT NULL,
  woonplaats VARCHAR(45),
  datumAanmaak TIMESTAMP DEFAULT 'NOW' NOT NULL,
  datumGewijzigd TIMESTAMP,
  adresActief CHAR(1) DEFAULT 1);

CREATE UNIQUE INDEX uniekAdres ON ADRES (postcode, huisnummer, toevoeging);
CREATE UNIQUE INDEX adres_id_UNIQUE ON ADRES (adres_id);

CREATE GENERATOR gen_adres_id;
SET GENERATOR gen_adres_id TO 0;

SET TERM ^ ;
CREATE TRIGGER trigger_adres_id FOR adres
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW."ADRES_ID" IS NULL) 
THEN NEW."ADRES_ID" = GEN_ID(gen_adres_id, 1);
END^
SET TERM ; ^

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`KLANT_HEEFT_ADRES`
-- -----------------------------------------------------
RECREATE TABLE KLANT_HEEFT_ADRES (
  klant_id_klant INT NOT NULL,
  adres_id_adres INT NOT NULL,
  CONSTRAINT klant_id_pa
    FOREIGN KEY (klant_id_klant)
    REFERENCES KLANT (klant_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT adres_id_pa
    FOREIGN KEY (adres_id_adres)
    REFERENCES ADRES (adres_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE INDEX adres_id_idx ON KLANT_HEEFT_ADRES (adres_id_adres);
CREATE UNIQUE INDEX uniekeCombi ON KLANT_HEEFT_ADRES (klant_id_klant, adres_id_adres);

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`BESTELLING`
-- -----------------------------------------------------

RECREATE TABLE BESTELLING (
  bestelling_id INT NOT NULL PRIMARY KEY,
  klant_id INT NOT NULL,
  datumAanmaak TIMESTAMP DEFAULT 'NOW' NOT NULL,
  bestellingActief CHAR(1) DEFAULT 1,
  CONSTRAINT klant_id
    FOREIGN KEY (klant_id)
    REFERENCES KLANT (klant_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE UNIQUE INDEX bestelling_id_UNIQUE ON BESTELLING (bestelling_id);
CREATE INDEX klant_id_idx ON BESTELLING (klant_id);

CREATE GENERATOR gen_bestelling_id;
SET GENERATOR gen_bestelling_id TO 0;

SET TERM ^ ;
CREATE TRIGGER trigger_bestelling_id FOR bestelling
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW."BESTELLING_ID" IS NULL)
THEN NEW."BESTELLING_ID" = GEN_ID(gen_bestelling_id, 1);
END^
SET TERM ; ^

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`PRIJS`
-- -----------------------------------------------------

RECREATE TABLE PRIJS (
  prijs_id INT NOT NULL PRIMARY KEY,
  prijs DECIMAL(10,2) DEFAULT NULL,
  artikel_id INT,
  datumAanmaak TIMESTAMP DEFAULT 'NOW' NOT NULL);

CREATE INDEX artikel_id_pr_idx ON PRIJS (artikel_id);

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`ARTIKEL`
-- -----------------------------------------------------

RECREATE TABLE ARTIKEL (
  artikel_id INT NOT NULL PRIMARY KEY,
  omschrijving VARCHAR(45) NOT NULL,
  prijsId INT DEFAULT 0,
  datumAanmaak TIMESTAMP DEFAULT 'NOW' NOT NULL,
  verwachteLevertijd VARCHAR(10) DEFAULT NULL,
  inAssortisement CHAR(1) DEFAULT 1,
  CONSTRAINT prijs_id_prijs
    FOREIGN KEY (prijsId)
    REFERENCES PRIJS (prijs_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE INDEX prijs_id_prijs_idx ON ARTIKEL (prijsId);

CREATE GENERATOR gen_artikel_id;
SET GENERATOR gen_artikel_id TO 0;

SET TERM ^ ;
CREATE TRIGGER trigger_artikel_id FOR artikel
ACTIVE BEFORE INSERT POSITION 0
AS
BEGIN
IF (NEW."ARTIKEL_ID" IS NULL)
THEN NEW."ARTIKEL_ID" = GEN_ID(gen_artikel_id, 1);
END^
SET TERM ; ^


ALTER TABLE PRIJS ADD CONSTRAINT artikel_id_pr
    FOREIGN KEY (artikel_id)
    REFERENCES ARTIKEL (artikel_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION;

-- -----------------------------------------------------
-- Table `RSVIERPROJECTDEEL2`.`BESTELLING_HEEFT_ARTIKEL`
-- -----------------------------------------------------

RECREATE TABLE BESTELLING_HEEFT_ARTIKEL (
  bestelling_id_best INT NOT NULL PRIMARY KEY,
  artikel_id_art INT NOT NULL,
  prijs_id_prijs INT NOT NULL,
  aantal VARCHAR(10) NOT NULL,
  CONSTRAINT bestelling_id_ba
    FOREIGN KEY (bestelling_id_best)
    REFERENCES BESTELLING (bestelling_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT artikel_id_ba
    FOREIGN KEY (artikel_id_art)
    REFERENCES ARTIKEL (artikel_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT prijs_id_best_ba
    FOREIGN KEY (prijs_id_prijs)
    REFERENCES PRIJS (prijs_id)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION);

CREATE INDEX artikel_id_idx ON BESTELLING_HEEFT_ARTIKEL (artikel_id_art);
CREATE INDEX bestelling_id_idx ON BESTELLING_HEEFT_ARTIKEL (bestelling_id_best);
CREATE INDEX prijs_id_ba_idx ON BESTELLING_HEEFT_ARTIKEL (prijs_id_prijs);

GRANT ALL ON TABLE KLANT TO USER RSVIERPROJECT;
GRANT ALL ON TABLE ADRES TO USER RSVIERPROJECT;
GRANT ALL ON TABLE BESTELLING TO USER RSVIERPROJECT;
GRANT ALL ON TABLE ARTIKEL TO USER RSVIERPROJECT;
GRANT ALL ON TABLE PRIJS TO USER RSVIERPROJECT;
GRANT ALL ON TABLE KLANT_HEEFT_ADRES TO USER RSVIERPROJECT;
GRANT ALL ON TABLE BESTELLING_HEEFT_ARTIKEL TO USER RSVIERPROJECT;
