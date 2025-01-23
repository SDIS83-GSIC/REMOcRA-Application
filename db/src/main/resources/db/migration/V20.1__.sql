DROP TABLE IF EXISTS remocra.couche;
DROP TABLE IF EXISTS remocra.groupe_couche;

CREATE TABLE remocra.groupe_couche
(
    groupe_couche_id      UUID PRIMARY KEY,
    groupe_couche_code    TEXT UNIQUE    NOT NULL,
    groupe_couche_ordre   INTEGER UNIQUE NOT NULL,
    groupe_couche_libelle TEXT           NOT NULL
);

CREATE TABLE remocra.couche
(
    couche_id               UUID PRIMARY KEY,
    couche_code             TEXT UNIQUE    NOT NULL,
    couche_groupe_couche_id UUID           NOT NULL REFERENCES remocra.groupe_couche (groupe_couche_id),
    couche_ordre            INTEGER UNIQUE NOT NULL,
    couche_libelle          TEXT           NOT NULL,
    couche_source           TEXT           NOT NULL,
    couche_projection       TEXT           NOT NULL,
    couche_url              TEXT           NOT NULL,
    couche_nom              TEXT           NOT NULL,
    couche_format           TEXT           NOT NULL,
    couche_public           BOOLEAN        NOT NULL,
    couche_active           BOOLEAN        NOT NULL,
    couche_icone            BYTEA,
    couche_legende          BYTEA
);
COMMENT ON COLUMN remocra.couche.couche_source IS 'Type de la couche (WMS, WMTS, etc.)';
COMMENT ON COLUMN remocra.couche.couche_projection IS 'Référentiel de la couche';
COMMENT ON COLUMN remocra.couche.couche_url IS 'URL de la source absolu si externe sinon relatif';
COMMENT ON COLUMN remocra.couche.couche_nom IS 'Nom de la couche issue de la source';
COMMENT ON COLUMN remocra.couche.couche_format IS 'Format de la couche';
COMMENT ON COLUMN remocra.couche.couche_public IS 'Indique si la couche est accessible publiquement';
COMMENT ON COLUMN remocra.couche.couche_active IS 'Affichage par défaut oui/non';
COMMENT ON COLUMN remocra.couche.couche_icone IS 'Icône de la couche';
COMMENT ON COLUMN remocra.couche.couche_legende IS 'Image de la légende de la couche';

CREATE TABLE remocra.l_couche_droit
(
    couche_id       UUID NOT NULL REFERENCES remocra.couche (couche_id),
    profil_droit_id UUID NOT NULL REFERENCES remocra.profil_droit (profil_droit_id),
    PRIMARY KEY (couche_id, profil_droit_id)
);
