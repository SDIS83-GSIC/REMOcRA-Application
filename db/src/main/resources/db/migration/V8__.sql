DROP TABLE IF EXISTS remocra.l_tournee_pei;
DROP TABLE IF EXISTS remocra.tournee;

CREATE TABLE remocra.tournee (
    tournee_id                          UUID            PRIMARY KEY,
    tournee_actif                       BOOLEAN         NOT NULL,
    tournee_libelle                     TEXT            NOT NULL,
    tournee_organisme_id                UUID            NOT NULL REFERENCES remocra.organisme,
    tournee_etat                        INTEGER,
    tournee_reservation_utilisateur_id  UUID            REFERENCES remocra.utilisateur,
    tournee_date_synchronisation        TIMESTAMPTZ,

    UNIQUE (tournee_organisme_id, tournee_libelle)
);

COMMENT
    ON COLUMN remocra.tournee.tournee_organisme_id
    IS 'Identifiant de l''organisme à qui est affecté cette tournée';
COMMENT
    ON COLUMN remocra.tournee.tournee_reservation_utilisateur_id
    IS 'Identifiant de l''utilisateur ayant réservé la tournée depuis l''application mobile';
COMMENT
    ON COLUMN remocra.tournee.tournee_date_synchronisation
    IS 'Date de synchronisation depuis l''application mobile';


CREATE TABLE l_tournee_pei (
    tournee_id      UUID        NOT NULL REFERENCES remocra.tournee (tournee_id),
    pei_id          UUID        NOT NULL REFERENCES remocra.pei (pei_id),
    l_tournee_pei_ordre           INTEGER     NOT NULL,

    PRIMARY KEY (tournee_id, pei_id),
    UNIQUE (tournee_id, l_tournee_pei_ordre)
);

COMMENT
    ON COLUMN remocra.l_tournee_pei.l_tournee_pei_ordre
    IS 'Indique l''ordre d''apparition du pei dans la tournee';


ALTER TYPE historique.type_objet ADD VALUE 'TOURNEE';
ALTER TYPE historique.type_objet ADD VALUE 'TOURNEE_PEI';
