-- =========================================================================
-- Jeu de données ScoDoc — à exécuter sur une base bd_scodoc vide
-- (après le script de création des tables)
--
-- Période couverte : du lundi 15 juin 2026 au vendredi 26 juin 2026
--   - Séances du 15 au 18 juin  -> "passées", appel déjà fait
--   - Séances du 19 juin        -> "aujourd'hui", appel à faire
--   - Séances du 22 au 26 juin  -> "à venir", pas d'appel
--
-- Structure :
--   3 promotions (BUT1, BUT2, BUT3), 6 groupes (A/B par promo)
--   5 enseignants (dont 1 Directeur des Études : tonin)
--   36 étudiants (6 par groupe)
--   9 modules (4 BUT1, 3 BUT2, 2 BUT3)
--   22 séances, 60 présences, 9 évaluations, 79 notes
-- =========================================================================

USE bd_scodoc;


-- =========================================================================
-- PROMOTIONS
-- =========================================================================
INSERT INTO Promotion (code) VALUES
('BUT1'),
('BUT2'),
('BUT3');


-- =========================================================================
-- GROUPES TD
-- =========================================================================
INSERT INTO GroupeTD (sigle, promo) VALUES
('A', 'BUT1'),
('B', 'BUT1'),
('A', 'BUT2'),
('B', 'BUT2'),
('A', 'BUT3'),
('B', 'BUT3');


-- =========================================================================
-- UTILISATEURS — ENSEIGNANTS
-- =========================================================================
INSERT INTO Utilisateur (email, motDePasse, nom, prenom) VALUES
('tonin@iut.fr',   'pass123', 'Tonin',   'Philippe'),
('naert@iut.fr',   'pass123', 'Naert',   'Lucie'),
('lambert@iut.fr', 'pass123', 'Evenas', 'Helene'),
('moreau@iut.fr',  'pass123', 'Moreau',  'Julien'),
('girard@iut.fr',  'pass123', 'Girard',  'Claire');


-- =========================================================================
-- UTILISATEURS — ÉTUDIANTS
-- =========================================================================
INSERT INTO Utilisateur (email, motDePasse, nom, prenom) VALUES
-- BUT1 - Groupe A (E001-E006)
('lucas.bernard@etu.iut.fr',  'pass123', 'Bernard',  'Lucas'),
('emma.petit@etu.iut.fr',     'pass123', 'Petit',    'Emma'),
('hugo.robert@etu.iut.fr',    'pass123', 'Robert',   'Hugo'),
('lea.richard@etu.iut.fr',    'pass123', 'Richard',  'Lea'),
('louis.durand@etu.iut.fr',   'pass123', 'Durand',   'Louis'),
('chloe.simon@etu.iut.fr',    'pass123', 'Simon',    'Chloe'),
-- BUT1 - Groupe B (E007-E012)
('nathan.laurent@etu.iut.fr',   'pass123', 'Laurent',   'Nathan'),
('manon.michel@etu.iut.fr',     'pass123', 'Michel',    'Manon'),
('tom.garcia@etu.iut.fr',       'pass123', 'Garcia',    'Tom'),
('camille.lefebvre@etu.iut.fr', 'pass123', 'Lefebvre',  'Camille'),
('enzo.david@etu.iut.fr',       'pass123', 'David',     'Enzo'),
('sarah.bertrand@etu.iut.fr',   'pass123', 'Bertrand',  'Sarah'),
-- BUT2 - Groupe A (E013-E018)
('mathis.roux@etu.iut.fr',     'pass123', 'Roux',     'Mathis'),
('jade.vincent@etu.iut.fr',    'pass123', 'Vincent',  'Jade'),
('ethan.fournier@etu.iut.fr',  'pass123', 'Fournier', 'Ethan'),
('lina.morel@etu.iut.fr',      'pass123', 'Morel',    'Lina'),
('noah.girard@etu.iut.fr',     'pass123', 'Girard',   'Noah'),
('zoe.andre@etu.iut.fr',       'pass123', 'Andre',    'Zoe'),
-- BUT2 - Groupe B (E019-E024)
('raphael.mercier@etu.iut.fr', 'pass123', 'Mercier',  'Raphael'),
('lola.dupont@etu.iut.fr',     'pass123', 'Dupont',   'Lola'),
('arthur.lambert@etu.iut.fr',  'pass123', 'Lambert',  'Arthur'),
('mila.dubois@etu.iut.fr',     'pass123', 'Dubois',   'Mila'),
('gabriel.martinez@etu.iut.fr','pass123', 'Martinez', 'Gabriel'),
('anna.legrand@etu.iut.fr',    'pass123', 'Legrand',  'Anna'),
-- BUT3 - Groupe A (E025-E030)
('maxime.roussel@etu.iut.fr',  'pass123', 'Roussel',  'Maxime'),
('julia.blanc@etu.iut.fr',     'pass123', 'Blanc',    'Julia'),
('theo.guerin@etu.iut.fr',     'pass123', 'Guerin',   'Theo'),
('ines.faure@etu.iut.fr',      'pass123', 'Faure',    'Ines'),
('sacha.rousseau@etu.iut.fr',  'pass123', 'Rousseau', 'Sacha'),
('eva.henry@etu.iut.fr',       'pass123', 'Henry',    'Eva'),
-- BUT3 - Groupe B (E031-E036)
('adam.picard@etu.iut.fr',     'pass123', 'Picard',   'Adam'),
('romane.gauthier@etu.iut.fr', 'pass123', 'Gauthier', 'Romane'),
('leo.perrin@etu.iut.fr',      'pass123', 'Perrin',   'Leo'),
('alice.bonnet@etu.iut.fr',    'pass123', 'Bonnet',   'Alice'),
('jules.francois@etu.iut.fr',  'pass123', 'Francois', 'Jules'),
('margaux.lemoine@etu.iut.fr', 'pass123', 'Lemoine',  'Margaux');


-- =========================================================================
-- ENSEIGNANTS
-- =========================================================================
INSERT INTO Enseignant (identifiant, estDe, userEns) VALUES
('tonin',   TRUE,  'tonin@iut.fr'),
('naert',   FALSE, 'naert@iut.fr'),
('lambert', FALSE, 'lambert@iut.fr'),
('moreau',  FALSE, 'moreau@iut.fr'),
('girard',  FALSE, 'girard@iut.fr');


-- =========================================================================
-- ÉTUDIANTS
-- =========================================================================
INSERT INTO Etudiant (numero, userEtud, groupeTD, promo) VALUES
-- BUT1 - Groupe A
('E001', 'lucas.bernard@etu.iut.fr',  'A', 'BUT1'),
('E002', 'emma.petit@etu.iut.fr',     'A', 'BUT1'),
('E003', 'hugo.robert@etu.iut.fr',    'A', 'BUT1'),
('E004', 'lea.richard@etu.iut.fr',    'A', 'BUT1'),
('E005', 'louis.durand@etu.iut.fr',   'A', 'BUT1'),
('E006', 'chloe.simon@etu.iut.fr',    'A', 'BUT1'),
-- BUT1 - Groupe B
('E007', 'nathan.laurent@etu.iut.fr',   'B', 'BUT1'),
('E008', 'manon.michel@etu.iut.fr',     'B', 'BUT1'),
('E009', 'tom.garcia@etu.iut.fr',       'B', 'BUT1'),
('E010', 'camille.lefebvre@etu.iut.fr', 'B', 'BUT1'),
('E011', 'enzo.david@etu.iut.fr',       'B', 'BUT1'),
('E012', 'sarah.bertrand@etu.iut.fr',   'B', 'BUT1'),
-- BUT2 - Groupe A
('E013', 'mathis.roux@etu.iut.fr',     'A', 'BUT2'),
('E014', 'jade.vincent@etu.iut.fr',    'A', 'BUT2'),
('E015', 'ethan.fournier@etu.iut.fr',  'A', 'BUT2'),
('E016', 'lina.morel@etu.iut.fr',      'A', 'BUT2'),
('E017', 'noah.girard@etu.iut.fr',     'A', 'BUT2'),
('E018', 'zoe.andre@etu.iut.fr',       'A', 'BUT2'),
-- BUT2 - Groupe B
('E019', 'raphael.mercier@etu.iut.fr', 'B', 'BUT2'),
('E020', 'lola.dupont@etu.iut.fr',     'B', 'BUT2'),
('E021', 'arthur.lambert@etu.iut.fr',  'B', 'BUT2'),
('E022', 'mila.dubois@etu.iut.fr',     'B', 'BUT2'),
('E023', 'gabriel.martinez@etu.iut.fr','B', 'BUT2'),
('E024', 'anna.legrand@etu.iut.fr',    'B', 'BUT2'),
-- BUT3 - Groupe A
('E025', 'maxime.roussel@etu.iut.fr',  'A', 'BUT3'),
('E026', 'julia.blanc@etu.iut.fr',     'A', 'BUT3'),
('E027', 'theo.guerin@etu.iut.fr',     'A', 'BUT3'),
('E028', 'ines.faure@etu.iut.fr',      'A', 'BUT3'),
('E029', 'sacha.rousseau@etu.iut.fr',  'A', 'BUT3'),
('E030', 'eva.henry@etu.iut.fr',       'A', 'BUT3'),
-- BUT3 - Groupe B
('E031', 'adam.picard@etu.iut.fr',     'B', 'BUT3'),
('E032', 'romane.gauthier@etu.iut.fr', 'B', 'BUT3'),
('E033', 'leo.perrin@etu.iut.fr',      'B', 'BUT3'),
('E034', 'alice.bonnet@etu.iut.fr',    'B', 'BUT3'),
('E035', 'jules.francois@etu.iut.fr',  'B', 'BUT3'),
('E036', 'margaux.lemoine@etu.iut.fr', 'B', 'BUT3');


-- =========================================================================
-- MODULES
-- =========================================================================
INSERT INTO Module (code, intitule, typeModule, promo) VALUES
-- BUT1
('R1.01', 'Programmation',           'RESSOURCE', 'BUT1'),
('R1.02', 'Developpement Web',       'RESSOURCE', 'BUT1'),
('R1.03', 'Bases de donnees',        'RESSOURCE', 'BUT1'),
('S1.01', 'Initiation au developpement', 'SAE',   'BUT1'),
-- BUT2
('R2.01', 'Developpement Objet',     'RESSOURCE', 'BUT2'),
('R2.02', 'Developpement IHM',       'RESSOURCE', 'BUT2'),
('SAE2.01', 'Application Web',       'SAE',       'BUT2'),
-- BUT3
('R3.04', 'Qualite dev',             'RESSOURCE', 'BUT3'),
('SAE3.01', 'Projet de fin d''etudes','SAE',      'BUT3');


-- =========================================================================
-- A COMME MODULE (enseignant <-> module)
-- =========================================================================
INSERT INTO ACommeModule (ens, module) VALUES
('tonin',   'R1.01'),
('tonin',   'R2.02'),
('tonin',   'SAE2.01'),
('naert',   'R1.02'),
('naert',   'R1.03'),
('naert',   'R3.04'),
('lambert', 'R2.01'),
('lambert', 'R2.02'),
('lambert', 'SAE2.01'),
('moreau',  'S1.01'),
('moreau',  'R1.01'),
('girard',  'R3.04'),
('girard',  'SAE3.01'),
('girard',  'S1.01');


-- =========================================================================
-- SUIT MODULE (groupe <-> module)
-- =========================================================================
INSERT INTO SuitModule (groupeTD, promo, module) VALUES
-- BUT1
('A', 'BUT1', 'R1.01'),
('A', 'BUT1', 'R1.02'),
('A', 'BUT1', 'R1.03'),
('A', 'BUT1', 'S1.01'),
('B', 'BUT1', 'R1.01'),
('B', 'BUT1', 'R1.02'),
('B', 'BUT1', 'R1.03'),
('B', 'BUT1', 'S1.01'),
-- BUT2
('A', 'BUT2', 'R2.01'),
('A', 'BUT2', 'R2.02'),
('A', 'BUT2', 'SAE2.01'),
('B', 'BUT2', 'R2.01'),
('B', 'BUT2', 'R2.02'),
('B', 'BUT2', 'SAE2.01'),
-- BUT3
('A', 'BUT3', 'R3.04'),
('A', 'BUT3', 'SAE3.01'),
('B', 'BUT3', 'R3.04'),
('B', 'BUT3', 'SAE3.01');


-- =========================================================================
-- RESPONSABLES
-- =========================================================================
INSERT INTO Responsable (ens, module) VALUES
('tonin',   'R1.01'),
('naert',   'R1.02'),
('naert',   'R1.03'),
('moreau',  'S1.01'),
('lambert', 'R2.01'),
('tonin',   'R2.02'),
('lambert', 'SAE2.01'),
('naert',   'R3.04'),
('girard',  'SAE3.01');


-- =========================================================================
-- SEANCES
-- Numerotation attendue (AUTO_INCREMENT a partir de 1, table vide) :
--   1-8   : semaine du 15 au 19 juin (avec appel fait, sauf le 19)
--   9-16  : idem pour BUT2
--   17-22 : BUT3
-- =========================================================================
INSERT INTO Seance (module, groupeTD, promo, dateSeance, horaireDebut, horaireFin, dateSaisie) VALUES
-- BUT1 - Groupe A
('R1.01', 'A', 'BUT1', '2026-06-15', '08:00:00', '10:00:00', '2026-06-15'), -- 1  passee, appel fait
('R1.02', 'A', 'BUT1', '2026-06-16', '10:00:00', '12:00:00', '2026-06-16'), -- 2  passee, appel fait
('R1.03', 'A', 'BUT1', '2026-06-19', '08:00:00', '10:00:00', NULL),         -- 3  aujourd'hui, appel a faire
('S1.01', 'A', 'BUT1', '2026-06-23', '13:30:00', '16:30:00', NULL),         -- 4  a venir
-- BUT1 - Groupe B
('R1.01', 'B', 'BUT1', '2026-06-15', '10:00:00', '12:00:00', '2026-06-15'), -- 5  passee, appel fait
('R1.02', 'B', 'BUT1', '2026-06-17', '08:00:00', '10:00:00', '2026-06-17'), -- 6  passee, appel fait
('R1.03', 'B', 'BUT1', '2026-06-19', '10:00:00', '12:00:00', NULL),         -- 7  aujourd'hui, appel a faire
('S1.01', 'B', 'BUT1', '2026-06-24', '13:30:00', '16:30:00', NULL),         -- 8  a venir
-- BUT2 - Groupe A
('R2.01',   'A', 'BUT2', '2026-06-15', '13:30:00', '15:30:00', '2026-06-15'), -- 9  passee, appel fait
('R2.02',   'A', 'BUT2', '2026-06-18', '08:00:00', '10:00:00', '2026-06-18'), -- 10 passee, appel fait
('SAE2.01', 'A', 'BUT2', '2026-06-19', '13:30:00', '17:30:00', NULL),         -- 11 aujourd'hui, appel a faire
('R2.01',   'A', 'BUT2', '2026-06-25', '08:00:00', '10:00:00', NULL),         -- 12 a venir
-- BUT2 - Groupe B
('R2.01',   'B', 'BUT2', '2026-06-16', '13:30:00', '15:30:00', '2026-06-16'), -- 13 passee, appel fait
('R2.02',   'B', 'BUT2', '2026-06-18', '10:00:00', '12:00:00', '2026-06-18'), -- 14 passee, appel fait
('SAE2.01', 'B', 'BUT2', '2026-06-19', '08:00:00', '12:00:00', NULL),         -- 15 aujourd'hui, appel a faire
('R2.02',   'B', 'BUT2', '2026-06-26', '10:00:00', '12:00:00', NULL),         -- 16 a venir
-- BUT3 - Groupe A
('R3.04',   'A', 'BUT3', '2026-06-17', '10:00:00', '12:00:00', '2026-06-17'), -- 17 passee, appel fait
('SAE3.01', 'A', 'BUT3', '2026-06-19', '08:00:00', '12:00:00', NULL),         -- 18 aujourd'hui, appel a faire
('R3.04',   'A', 'BUT3', '2026-06-23', '14:00:00', '16:00:00', NULL),         -- 19 a venir
-- BUT3 - Groupe B
('R3.04',   'B', 'BUT3', '2026-06-17', '14:00:00', '16:00:00', '2026-06-17'), -- 20 passee, appel fait
('SAE3.01', 'B', 'BUT3', '2026-06-19', '13:30:00', '17:30:00', NULL),         -- 21 aujourd'hui, appel a faire
('R3.04',   'B', 'BUT3', '2026-06-24', '08:00:00', '10:00:00', NULL);         -- 22 a venir


-- =========================================================================
-- PRESENCES
-- Uniquement pour les seances 1, 2, 5, 6, 9, 10, 13, 14, 17, 20 (appel fait)
-- =========================================================================
INSERT INTO Presence (etudiant, seance, statut) VALUES
-- Seance 1 - R1.01 - BUT1 A
('E001', 1, 'PRESENT'),
('E002', 1, 'PRESENT'),
('E003', 1, 'ABSENT'),
('E004', 1, 'PRESENT'),
('E005', 1, 'RETARD'),
('E006', 1, 'PRESENT'),
-- Seance 2 - R1.02 - BUT1 A
('E001', 2, 'RETARD'),
('E002', 2, 'PRESENT'),
('E003', 2, 'PRESENT'),
('E004', 2, 'PRESENT'),
('E005', 2, 'PRESENT'),
('E006', 2, 'PRESENT'),
-- Seance 5 - R1.01 - BUT1 B
('E007', 5, 'PRESENT'),
('E008', 5, 'PRESENT'),
('E009', 5, 'ABSENT'),
('E010', 5, 'PRESENT'),
('E011', 5, 'PRESENT'),
('E012', 5, 'PRESENT'),
-- Seance 6 - R1.02 - BUT1 B
('E007', 6, 'ABSENT'),
('E008', 6, 'PRESENT'),
('E009', 6, 'PRESENT'),
('E010', 6, 'PRESENT'),
('E011', 6, 'RETARD'),
('E012', 6, 'PRESENT'),
-- Seance 9 - R2.01 - BUT2 A
('E013', 9, 'PRESENT'),
('E014', 9, 'PRESENT'),
('E015', 9, 'RETARD'),
('E016', 9, 'PRESENT'),
('E017', 9, 'PRESENT'),
('E018', 9, 'PRESENT'),
-- Seance 10 - R2.02 - BUT2 A
('E013', 10, 'ABSENT'),
('E014', 10, 'PRESENT'),
('E015', 10, 'PRESENT'),
('E016', 10, 'ABSENT'),
('E017', 10, 'PRESENT'),
('E018', 10, 'PRESENT'),
-- Seance 13 - R2.01 - BUT2 B
('E019', 13, 'PRESENT'),
('E020', 13, 'PRESENT'),
('E021', 13, 'RETARD'),
('E022', 13, 'PRESENT'),
('E023', 13, 'PRESENT'),
('E024', 13, 'PRESENT'),
-- Seance 14 - R2.02 - BUT2 B
('E019', 14, 'PRESENT'),
('E020', 14, 'PRESENT'),
('E021', 14, 'PRESENT'),
('E022', 14, 'PRESENT'),
('E023', 14, 'PRESENT'),
('E024', 14, 'ABSENT'),
-- Seance 17 - R3.04 - BUT3 A
('E025', 17, 'PRESENT'),
('E026', 17, 'PRESENT'),
('E027', 17, 'ABSENT'),
('E028', 17, 'PRESENT'),
('E029', 17, 'RETARD'),
('E030', 17, 'PRESENT'),
-- Seance 20 - R3.04 - BUT3 B
('E031', 20, 'PRESENT'),
('E032', 20, 'PRESENT'),
('E033', 20, 'RETARD'),
('E034', 20, 'PRESENT'),
('E035', 20, 'PRESENT'),
('E036', 20, 'PRESENT');


-- =========================================================================
-- EVALUATIONS
-- Numerotation attendue (AUTO_INCREMENT a partir de 1, table vide) : 1-9
-- SAE2.01 et SAE3.01 n'ont volontairement aucune evaluation.
-- =========================================================================
INSERT INTO Evaluation (coeff, duree, module) VALUES
(2, '02:00:00', 'R1.01'),   -- 1 DS 1 - Bases du langage
(1, '01:30:00', 'R1.01'),   -- 2 TP note - Premiers programmes
(1, '01:30:00', 'R1.02'),   -- 3 DS 1 - Structure d'une page web
(1, '02:00:00', 'R1.03'),   -- 4 TP note - Requetes SQL
(3, NULL,       'S1.01'),   -- 5 Projet - Application console
(2, '02:00:00', 'R2.01'),   -- 6 DS 1 - POO et heritage
(1, '01:00:00', 'R2.01'),   -- 7 CC 1 - Interfaces et abstraction
(1, '02:00:00', 'R2.02'),   -- 8 TP note - Patterns de conception
(2, '01:30:00', 'R3.04');   -- 9 DS 1 - Tests unitaires


-- =========================================================================
-- NOTES
-- =========================================================================
INSERT INTO Note (etudiant, eval, valeur, statut) VALUES
-- Eval 1 - R1.01 DS 1 - Bases du langage - complet (12/12)
('E001', 1, 14.5, 'OK'),
('E002', 1, 9.0,  'OK'),
('E003', 1, 16.0, 'OK'),
('E004', 1, 6.5,  'OK'),
('E005', 1, 12.0, 'OK'),
('E006', 1, 18.0, 'OK'),
('E007', 1, 11.5, 'OK'),
('E008', 1, NULL, 'ABS'),
('E009', 1, 8.0,  'OK'),
('E010', 1, 15.5, 'OK'),
('E011', 1, 13.0, 'OK'),
('E012', 1, 10.0, 'OK'),

-- Eval 2 - R1.01 TP note - Premiers programmes - partiel (6/12, groupe A uniquement)
('E001', 2, 17.0, 'OK'),
('E002', 2, 13.5, 'OK'),
('E003', 2, 19.0, 'OK'),
('E004', 2, 11.0, 'OK'),
('E005', 2, NULL, 'EXC'),
('E006', 2, 15.0, 'OK'),

-- Eval 3 - R1.02 DS 1 - Structure d'une page web - partiel (9/12)
('E001', 3, 12.0, 'OK'),
('E002', 3, 8.5,  'OK'),
('E003', 3, 14.0, 'OK'),
('E004', 3, 10.5, 'OK'),
('E005', 3, 16.5, 'OK'),
('E006', 3, 9.0,  'OK'),
('E007', 3, 13.0, 'OK'),
('E008', 3, 7.5,  'OK'),
('E009', 3, 11.0, 'OK'),

-- Eval 4 - R1.03 TP note - Requetes SQL - complet (12/12)
('E001', 4, 15.0, 'OK'),
('E002', 4, 12.5, 'OK'),
('E003', 4, 17.5, 'OK'),
('E004', 4, 9.5,  'OK'),
('E005', 4, 14.0, 'OK'),
('E006', 4, 16.0, 'OK'),
('E007', 4, 10.0, 'OK'),
('E008', 4, 13.5, 'OK'),
('E009', 4, 8.5,  'OK'),
('E010', 4, 18.5, 'OK'),
('E011', 4, 12.0, 'OK'),
('E012', 4, NULL, 'ABS'),

-- Eval 5 - S1.01 Projet - pas encore notee (0/12) : aucune ligne

-- Eval 6 - R2.01 DS 1 - POO et heritage - partiel (10/12)
('E013', 6, 13.0, 'OK'),
('E014', 6, 9.5,  'OK'),
('E015', 6, 16.5, 'OK'),
('E016', 6, 11.0, 'OK'),
('E017', 6, 14.5, 'OK'),
('E018', 6, 8.0,  'OK'),
('E019', 6, NULL, 'ABS'),
('E020', 6, 12.5, 'OK'),
('E021', 6, 17.0, 'OK'),
('E023', 6, 10.0, 'OK'),

-- Eval 7 - R2.01 CC 1 - Interfaces et abstraction - complet (12/12)
('E013', 7, 16.0, 'OK'),
('E014', 7, 12.0, 'OK'),
('E015', 7, 18.0, 'OK'),
('E016', 7, 9.0,  'OK'),
('E017', 7, 14.0, 'OK'),
('E018', 7, 11.5, 'OK'),
('E019', 7, 13.0, 'OK'),
('E020', 7, NULL, 'EXC'),
('E021', 7, 15.5, 'OK'),
('E022', 7, 10.0, 'OK'),
('E023', 7, 17.0, 'OK'),
('E024', 7, 12.5, 'OK'),

-- Eval 8 - R2.02 TP note - Patterns de conception - complet (12/12)
('E013', 8, 14.0, 'OK'),
('E014', 8, 11.0, 'OK'),
('E015', 8, 15.5, 'OK'),
('E016', 8, 9.5,  'OK'),
('E017', 8, 13.0, 'OK'),
('E018', 8, 16.0, 'OK'),
('E019', 8, 12.5, 'OK'),
('E020', 8, 10.0, 'OK'),
('E021', 8, 14.5, 'OK'),
('E022', 8, 8.5,  'OK'),
('E023', 8, NULL, 'ABS'),
('E024', 8, 17.5, 'OK'),

-- Eval 9 - R3.04 DS 1 - Tests unitaires - partiel (6/12, groupe A uniquement)
('E025', 9, 12.0, 'OK'),
('E026', 9, 15.5, 'OK'),
('E027', 9, NULL, 'ABS'),
('E028', 9, 10.5, 'OK'),
('E029', 9, 13.0, 'OK'),
('E030', 9, 16.0, 'OK');