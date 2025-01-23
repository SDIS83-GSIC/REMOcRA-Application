DROP TABLE IF EXISTS remocra.l_commune_cis;

CREATE TABLE  IF NOT EXISTS remocra.l_commune_cis (
    commune_id    UUID            NOT NULL REFERENCES remocra.commune(commune_id),
    cis_id        UUID            NOT NULL REFERENCES remocra.organisme(organisme_id),

    PRIMARY KEY(commune_id, cis_id)
);
