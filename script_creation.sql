DROP DATABASE IF EXISTS bd_scodoc;
CREATE DATABASE IF NOT EXISTS bd_scodoc;
USE bd_scodoc;

DROP TABLE IF EXISTS Note;
DROP TABLE IF EXISTS Evaluation;
DROP TABLE IF EXISTS SuitModule;
DROP TABLE IF EXISTS ACommeModule;
DROP TABLE IF EXISTS Note;
DROP TABLE IF EXISTS Evaluation;
DROP TABLE IF EXISTS Seance;
DROP TABLE IF EXISTS Responsable;
DROP TABLE IF EXISTS Etudiant;
DROP TABLE IF EXISTS Module;
DROP TABLE IF EXISTS GroupeTD;
DROP TABLE IF EXISTS Enseignant;
DROP TABLE IF EXISTS Utilisateur;
DROP TABLE IF EXISTS Promotion;

CREATE TABLE Utilisateur (
    email VARCHAR(50),
    motDePasse VARCHAR(50) NOT NULL,
    nom VARCHAR(50) NOT NULL,
    prenom VARCHAR(50) NOT NULL,
    CONSTRAINT pk_Utilisateur PRIMARY KEY (email),
    CONSTRAINT ck_email CHECK (email LIKE '%_@%_.%_')
);

CREATE TABLE Enseignant (
    identifiant VARCHAR(50),
    estDe BOOLEAN DEFAULT FALSE,
    userEns VARCHAR(50),
    CONSTRAINT pk_Enseignant PRIMARY KEY (identifiant),
    CONSTRAINT fk_EnseignantUtilisateur FOREIGN KEY (userEns) REFERENCES Utilisateur(email)
);

CREATE TABLE Promotion (
    code VARCHAR(5),
    CONSTRAINT pk_Promotion PRIMARY KEY (code)
);

CREATE TABLE GroupeTD (
    sigle CHAR,
    promo VARCHAR(5),
    CONSTRAINT pk_GroupeTD PRIMARY KEY (sigle, promo),
    CONSTRAINT fk_GroupeTDPromotion FOREIGN KEY (promo) REFERENCES Promotion(code)
);

CREATE TABLE Etudiant (
    numero VARCHAR(50),
    userEtud VARCHAR(50) NOT NULL,
    groupeTD CHAR NOT NULL,
    promo VARCHAR(5) NOT NULL,
    CONSTRAINT pk_Etudiant PRIMARY KEY (numero),
    CONSTRAINT fk_EtudiantUtilisateur FOREIGN KEY (userEtud) REFERENCES Utilisateur(email),
    CONSTRAINT fk_EtudiantGroupeTD FOREIGN KEY (groupeTD) REFERENCES GroupeTD(sigle),
    CONSTRAINT fk_EtudiantPromotion FOREIGN KEY (promo) REFERENCES Promotion(code)
);

CREATE TABLE Module (
    code VARCHAR(10),
    intitule VARCHAR(50) NOT NULL,
    typeModule ENUM('SAE', 'RESSOURCE'),
    promo VARCHAR(5) NOT NULL,
    CONSTRAINT pk_Module PRIMARY KEY (code),
    CONSTRAINT fk_ModulePromotion FOREIGN KEY (promo) REFERENCES Promotion(code)
);

CREATE TABLE ACommeModule (
    ens VARCHAR(50),
    module VARCHAR(10),
    CONSTRAINT pk_ACommeModule PRIMARY KEY (ens, module),
    CONSTRAINT fk_ACommeModuleEnseignant FOREIGN KEY (ens) REFERENCES Enseignant(identifiant),
    CONSTRAINT fk_ACommeModuleModule FOREIGN KEY (module) REFERENCES Module(code)
);

CREATE TABLE SuitModule (
    groupeTD CHAR,
    promo VARCHAR(5),
    module VARCHAR(10),
    CONSTRAINT pk_SuitModule PRIMARY KEY (groupeTD, promo, module),
    CONSTRAINT fk_SuitModuleGroupeTD FOREIGN KEY (groupeTD, promo) REFERENCES GroupeTD(sigle, promo),
    CONSTRAINT fk_SuitModuleModule FOREIGN KEY (module) REFERENCES Module(code)
);

CREATE TABLE Responsable(
	ens VARCHAR(50),
    module VARCHAR(10),
	CONSTRAINT pk_Responsable PRIMARY KEY (ens , module),
    CONSTRAINT fk_ResponsableEnseignant FOREIGN KEY (ens)
        REFERENCES Enseignant (identifiant),
    CONSTRAINT fk_ResponsableModule FOREIGN KEY (module) REFERENCES Module(code)
);

CREATE TABLE Seance (
    numero INT AUTO_INCREMENT,
    module VARCHAR(10),
    groupeTD CHAR,
    promo VARCHAR(5),
    dateSeance DATE,
    horaireDebut TIME,
    horaireFin TIME,
    dateSaisie DATE,
    CONSTRAINT pk_Seance PRIMARY KEY (numero),
    CONSTRAINT fk_SeanceModule FOREIGN KEY (module) REFERENCES Module(code),
    CONSTRAINT fk_SeanceGroupeTD FOREIGN KEY (groupeTD, promo) REFERENCES GroupeTD(sigle, promo)
);

CREATE TABLE Presence (
    etudiant VARCHAR(50),
    seance INT,
    statut ENUM('PRESENT', 'RETARD', 'ABSENT'),
    CONSTRAINT pk_Presence PRIMARY KEY (etudiant, seance),
    CONSTRAINT fk_PresenceEtudiant FOREIGN KEY (etudiant) REFERENCES Etudiant(numero),
    CONSTRAINT fk_PresenceSeance FOREIGN KEY (seance) REFERENCES Seance(numero)
);

CREATE TABLE Evaluation (
    numero INT AUTO_INCREMENT,
    coeff INT,
    duree TIME,
    module VARCHAR(10),
    CONSTRAINT pk_Evaluation PRIMARY KEY (numero),
    CONSTRAINT fk_EvaluationModule FOREIGN KEY (module) REFERENCES Module(code)
);

CREATE TABLE Note (
    etudiant VARCHAR(50),
    eval INT,
    valeur DECIMAL(4,2),
    statut ENUM('OK', 'ABS', 'EXC', 'ATT') DEFAULT 'OK',
    CONSTRAINT pk_Note PRIMARY KEY (etudiant, eval),
    CONSTRAINT fk_NoteEtudiant FOREIGN KEY (etudiant) REFERENCES Etudiant(numero),
    CONSTRAINT fk_NoteEvaluation FOREIGN KEY (eval) REFERENCES Evaluation(numero)
);

CREATE USER IF NOT EXISTS 'scodoc'@'localhost' IDENTIFIED BY 'scodoc';
GRANT ALL PRIVILEGES ON bd_scodoc.* TO 'scodoc'@'localhost';
FLUSH PRIVILEGES;