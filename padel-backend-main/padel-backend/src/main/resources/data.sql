INSERT INTO site (nom, adresse, heure_ouverture, heure_fermeture, annee_horaire) VALUES
                                                                                     ('Site Bruxelles', 'Rue de la Loi 1, Bruxelles', '08:00:00', '22:00:00', 2025),
                                                                                     ('Site Liège', 'Place Saint-Lambert 5, Liège', '09:00:00', '21:00:00', 2025);

INSERT INTO terrain (numero, site_id) VALUES
                                          (1, 1), (2, 1), (3, 1),
                                          (1, 2), (2, 2);

INSERT INTO membre (matricule, nom, prenom, email, type_membre, site_id, solde) VALUES
                                                                                    ('G0001', 'Dupont', 'Jean', 'jean.dupont@mail.com', 'GLOBAL', NULL, 0),
                                                                                    ('S00001', 'Martin', 'Sophie', 'sophie.martin@mail.com', 'SITE', 1, 0),
                                                                                    ('L00001', 'Libre', 'Paul', 'paul.libre@mail.com', 'LIBRE', NULL, 0);