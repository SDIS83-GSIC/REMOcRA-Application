CREATE TABLE remocra.debit_simultane (
    debit_simultane_id              UUID      NOT NULL PRIMARY KEY,
    debit_simultane_site_id         UUID      REFERENCES remocra.site(site_id),
    debit_simultane_geometrie       Geometry  NOT NULL,
    debit_simultane_numero_dossier  TEXT      NOT NULL
);

ALTER TABLE remocra.debit_simultane
    ADD CONSTRAINT geometrie_debit_simultane CHECK (geometrytype(debit_simultane_geometrie) = 'POINT'::text);

CREATE TABLE remocra.debit_simultane_mesure (
    debit_simultane_mesure_id               UUID     NOT NULL PRIMARY KEY,
    debit_simultane_id                      UUID     NOT NULL REFERENCES remocra.debit_simultane(debit_simultane_id),
    debit_simultane_mesure_debit_requis     integer,
    debit_simultane_mesure_debit_mesure     integer,
    debit_simultane_mesure_debit_retenu     integer,
    debit_simultane_mesure_date_mesure      TIMESTAMPTZ NOT NULL,
    debit_simultane_mesure_commentaire      text,
    debit_simultane_mesure_identique_reseau_ville boolean
);


CREATE TABLE remocra.l_debit_simultane_mesure_pei (
    debit_simultane_mesure_id           UUID     NOT NULL REFERENCES remocra.debit_simultane_mesure(debit_simultane_mesure_id),
    pei_id        UUID     NOT NULL REFERENCES remocra.pei(pei_id),

    PRIMARY KEY (debit_simultane_mesure_id, pei_id)
);

CREATE TABLE remocra.l_debit_simultane_document (
    document_id   UUID     NOT NULL REFERENCES remocra.document(document_id),
    debit_simultane_mesure_id        UUID     NOT NULL REFERENCES remocra.debit_simultane_mesure(debit_simultane_mesure_id),

    PRIMARY KEY (document_id, debit_simultane_mesure_id)
);

ALTER TYPE historique.type_objet ADD VALUE 'DEBIT_SIMULTANE';