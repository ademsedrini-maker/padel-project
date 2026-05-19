-- =============================================
-- SITES
-- =============================================
INSERT INTO site (id, nom, adresse, heure_ouverture, heure_fermeture, annee_horaire) VALUES
                                                                                         (1, 'Padel Center Bruxelles', 'Rue de la Loi 1, 1000 Bruxelles', '08:00:00', '22:00:00', 2027),
                                                                                         (2, 'Padel Club Namur',      'Place d Armes 5, 5000 Namur',      '08:00:00', '22:00:00', 2027);

-- =============================================
-- TERRAINS
-- =============================================
INSERT INTO terrain (numero, site_id) VALUES
                                          (1, 1),
                                          (2, 1),
                                          (3, 1),
                                          (1, 2),
                                          (2, 2);

-- =============================================
-- MEMBRES
-- =============================================
INSERT INTO membre (matricule, nom, prenom, email, type_membre, site_id, solde,
                    telephone, adresse, date_naissance) VALUES
                                                            ('G0001', 'Dupont', 'Jean',  'jean.dupont@mail.com',  'GLOBAL', NULL,  0,
                                                             '0470/12.34.56', 'Rue de la Paix 10, 1000 Bruxelles', '1990-05-10'),

                                                            ('S0001', 'Martin', 'Sophie','sophie.martin@mail.com','SITE',   1,     0,
                                                             '0471/11.22.33', 'Avenue des Sports 5, 1000 Bruxelles', '1992-03-15'),

                                                            ('L0001', 'Libre',  'Paul',  'paul.libre@mail.com',   'LIBRE',  NULL,  0,
                                                             NULL, NULL, NULL);

-- =============================================
-- CRÉNEAUX
-- (dates mises en 2027 pour être dans le futur)
-- =============================================
INSERT INTO creneau (date_heure_debut, date_heure_fin, disponible, terrain_id) VALUES
-- Site 1 - Terrain 1
('2027-05-14 09:00:00', '2027-05-14 10:30:00', true, 1),
('2027-05-14 10:30:00', '2027-05-14 12:00:00', true, 1),
('2027-05-14 14:00:00', '2027-05-14 15:30:00', true, 1),
('2027-05-15 09:00:00', '2027-05-15 10:30:00', true, 1),
('2027-05-15 14:00:00', '2027-05-15 15:30:00', true, 1),

-- Site 1 - Terrain 2
('2027-05-14 09:00:00', '2027-05-14 10:30:00', true, 2),
('2027-05-14 14:00:00', '2027-05-14 15:30:00', true, 2),
('2027-05-15 10:30:00', '2027-05-15 12:00:00', true, 2),

-- Site 1 - Terrain 3
('2027-05-16 09:00:00', '2027-05-16 10:30:00', true, 3),
('2027-05-16 14:00:00', '2027-05-16 15:30:00', true, 3),

-- Site 2 - Terrain 1
('2027-05-14 09:00:00', '2027-05-14 10:30:00', true, 4),
('2027-05-14 14:00:00', '2027-05-14 15:30:00', true, 4),
('2027-05-15 09:00:00', '2027-05-15 10:30:00', true, 4),

-- Site 2 - Terrain 2
('2027-05-15 14:00:00', '2027-05-15 15:30:00', true, 5),
('2027-05-16 10:00:00', '2027-05-16 11:30:00', true, 5);