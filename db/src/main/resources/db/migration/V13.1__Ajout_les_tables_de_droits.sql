DROP TABLE IF EXISTS remocra.l_type_droit_profil_droit;
DROP TABLE IF EXISTS remocra.profil_droit;
DROP TABLE IF EXISTS remocra.type_droit;

-- Création de la table profil_droit
CREATE TABLE remocra.profil_droit
(
    profil_droit_id                             UUID            NOT NULL PRIMARY KEY,
    profil_droit_code                           TEXT    UNIQUE  NOT NULL,
    profil_droit_libelle                        TEXT            NOT NULL
);

-- Création de la table type_droit
CREATE TABLE remocra.type_droit
(
    type_droit_id                               UUID            NOT NULL PRIMARY KEY,
    type_droit_code                             TEXT    UNIQUE  NOT NULL,
    type_droit_libelle                          TEXT            NOT NULL
);


-- Création de la table de liaison type_droit <-> profil_droit
CREATE TABLE remocra.l_type_droit_profil_droit
(
    profil_droit_id                             UUID NOT NULL REFERENCES remocra.profil_droit (profil_droit_id),
    type_droit_id                               UUID NOT NULL REFERENCES remocra.type_droit (type_droit_id),

     PRIMARY KEY (profil_droit_id, type_droit_id)
);
