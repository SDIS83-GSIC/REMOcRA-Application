-- Profil_droit : Ajout de données dans la table
INSERT INTO remocra.profil_droit
(profil_droit_id, profil_droit_code, profil_droit_libelle)
VALUES (gen_random_uuid(), 'REMOCRA', 'Administrateur REMOcRA');

-- Type_droit : Ajout de données dans la table
INSERT INTO remocra.type_droit
(type_droit_id, type_droit_code, type_droit_libelle)
VALUES
(gen_random_uuid(), 'PEI_R', 'Consulter les PEI'),
(gen_random_uuid(), 'PEI_D', 'Supprimer des PEI');

-- Droit : Ajout de données dans la table
INSERT INTO remocra.l_type_droit_profil_droit (profil_droit_id, type_droit_id)
SELECT p.profil_droit_id, t.type_droit_id
FROM remocra.profil_droit p, remocra.type_droit t
WHERE p.profil_droit_code = 'REMOCRA' AND t.type_droit_code = 'PEI_R';

INSERT INTO remocra.l_type_droit_profil_droit (profil_droit_id, type_droit_id)
SELECT p.profil_droit_id, t.type_droit_id
FROM remocra.profil_droit p, remocra.type_droit t
WHERE p.profil_droit_code = 'REMOCRA' AND t.type_droit_code = 'PEI_D';
