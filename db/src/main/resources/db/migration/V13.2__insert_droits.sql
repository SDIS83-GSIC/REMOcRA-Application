-- Profil_droit : Ajout de données dans la table
INSERT INTO remocra.profil_droit
(profil_droit_id, profil_droit_code, profil_droit_libelle, profil_droit_droits)
VALUES (gen_random_uuid(), 'REMOCRA', 'Administrateur REMOcRA', ARRAY ['PEI_R', 'PEI_D']::remocra."DROIT"[]);
