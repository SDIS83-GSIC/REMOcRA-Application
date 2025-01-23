DROP TABLE remocra.l_profil_utilisateur_organisme_droit;

-- Liaison entre le profil utilisateur, le profil organisme et le profil droits
-- 1 profil droit = une combinaison unique d'un profil utilisateur et organisme
CREATE TABLE remocra.l_profil_utilisateur_organisme_droit (
    profil_utilisateur_id                       UUID    NOT NULL REFERENCES remocra.profil_utilisateur(profil_utilisateur_id),
    profil_organisme_id                         UUID    NOT NULL REFERENCES remocra.profil_organisme(profil_organisme_id),
    profil_droit_id                             UUID    NOT NULL REFERENCES remocra.profil_droit(profil_droit_id),

    PRIMARY KEY (profil_utilisateur_id, profil_organisme_id)
);
