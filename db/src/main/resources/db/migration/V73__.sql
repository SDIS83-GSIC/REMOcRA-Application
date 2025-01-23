ALTER TYPE remocra.TYPE_MODULE ADD VALUE 'PEI_PRESCRIT';

ALTER TYPE historique.type_objet ADD VALUE 'PEI_PRESCRIT';

CREATE TABLE remocra.pei_prescrit (
    pei_prescrit_id             UUID            NOT NULL PRIMARY KEY,
    pei_prescrit_date           TIMESTAMPTZ,
    pei_prescrit_debit          INTEGER,
    pei_prescrit_nb_poteaux     INTEGER,
    pei_prescrit_organisme_id   UUID            REFERENCES remocra.organisme(organisme_id),
    pei_prescrit_commentaire    TEXT,
    pei_prescrit_agent          TEXT,
    pei_prescrit_num_dossier    TEXT,
    pei_prescrit_geometrie      Geometry        NOT NULL
);

COMMENT
    ON COLUMN remocra.pei_prescrit.pei_prescrit_organisme_id
    IS 'Organisme de l''utilisateur dépositaire de la prescription des PEIs';