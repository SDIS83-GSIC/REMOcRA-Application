ALTER TABLE remocra.l_couche_groupe_fonctionnalites
    ADD COLUMN limite_zc BOOLEAN;

UPDATE remocra.l_couche_groupe_fonctionnalites
    SET limite_zc = TRUE;

ALTER TABLE remocra.l_couche_groupe_fonctionnalites
    ALTER COLUMN limite_zc SET NOT NULL;
