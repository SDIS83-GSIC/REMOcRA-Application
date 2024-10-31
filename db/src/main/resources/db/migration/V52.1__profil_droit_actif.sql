ALTER TABLE remocra.profil_droit
    ADD COLUMN profil_droit_actif BOOLEAN;

UPDATE remocra.profil_droit SET profil_droit_actif = true;

ALTER TABLE remocra.profil_droit ALTER COLUMN profil_droit_actif SET NOT NULL;
