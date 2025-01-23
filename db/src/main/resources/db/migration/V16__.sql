DROP TABLE IF EXISTS remocra.indisponibilite_temporaire;
DROP TABLE IF EXISTS remocra.l_indisponibilite_temporaire_pei;

CREATE TABLE remocra.indisponibilite_temporaire
(
    indisponibilite_temporaire_id                         UUID PRIMARY KEY,
    indisponibilite_temporaire_date_debut                 TIMESTAMPTZ                               NOT NULL,
    indisponibilite_temporaire_date_fin                   TIMESTAMPTZ,
    indisponibilite_temporaire_motif                      TEXT                                      NOT NULL,
    indisponibilite_temporaire_observation                TEXT,
    indisponibilite_temporaire_bascule_auto_indisponible  BOOLEAN                                   NOT NULL,
    indisponibilite_temporaire_bascule_auto_disponible    BOOLEAN                                   NOT NULL,
    indisponibilite_temporaire_mail_avant_indisponibilite BOOLEAN                                   NOT NULL,
    indisponibilite_temporaire_mail_apres_indisponibilite BOOLEAN                                   NOT NULL
);

ALTER TYPE historique.type_objet ADD VALUE 'INDISPONIBILITE_TEMPORAIRE';

CREATE TABLE remocra.l_indisponibilite_temporaire_pei
(
    indisponibilite_temporaire_id UUID REFERENCES remocra.indisponibilite_temporaire,
    pei_id                        UUID REFERENCES remocra.pei,
    PRIMARY KEY (pei_id, indisponibilite_temporaire_id)
);
