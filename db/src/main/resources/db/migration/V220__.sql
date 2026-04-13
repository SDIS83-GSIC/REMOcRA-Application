-- Ajout de la colonne participe_dfci dans la table remocra.nature
ALTER TABLE remocra.nature
    ADD COLUMN nature_participe_dfci BOOLEAN NOT NULL DEFAULT FALSE;

-- Suppression du DEFAULT pour ne pas affecter les futurs INSERT
ALTER TABLE remocra.nature
    ALTER COLUMN nature_participe_dfci DROP DEFAULT;

-- Ajout des colonnes pei_perenne et pei_rotation_6_ccf dans la table remocra.pei
ALTER TABLE remocra.pei
    ADD COLUMN pei_perenne BOOLEAN,
    ADD COLUMN pei_rotation_6_ccf BOOLEAN;
