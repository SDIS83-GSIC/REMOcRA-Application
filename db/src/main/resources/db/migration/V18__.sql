DROP TABLE IF EXISTS l_modele_courrier_profil_droit;

CREATE TABLE remocra.l_modele_courrier_profil_droit (
    modele_courrier_id       UUID     NOT NULL REFERENCES modele_courrier(modele_courrier_id),
    profil_droit_id          UUID     NOT NULL REFERENCES profil_droit(profil_droit_id),

    PRIMARY KEY (modele_courrier_id, profil_droit_id)
);
