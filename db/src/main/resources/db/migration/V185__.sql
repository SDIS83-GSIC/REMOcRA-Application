CREATE TYPE remocra.TYPE_PARAMETRE_EVENEMENT_COMPLEMENT AS ENUM (
    'CHECKBOX_INPUT',
    'DATE_INPUT',
    'NUMBER_INPUT',
    'SELECT_INPUT',
    'TEXT_INPUT'
);

CREATE TABLE remocra.crise_evenement_complement (
    crise_evenement_complement_id UUID PRIMARY KEY,
    crise_evenement_complement_evenement_sous_categorie_id UUID REFERENCES remocra.evenement_sous_categorie(evenement_sous_categorie_id),
    crise_evenement_complement_libelle TEXT,
    crise_evenement_complement_source_sql TEXT,
    crise_evenement_complement_source_sql_id TEXT,
    crise_evenement_complement_source_sql_libelle TEXT,
    crise_evenement_complement_valeur_defaut TEXT,
    crise_evenement_complement_est_requis BOOL NOT NULL DEFAULT false,
    crise_evenement_complement_type remocra.TYPE_PARAMETRE_EVENEMENT_COMPLEMENT NOT NULL
);

CREATE TABLE remocra.l_evenement_crise_evenement_complement (
    evenement_id UUID REFERENCES remocra.evenement(evenement_id),
    crise_evenement_complement_id UUID REFERENCES remocra.crise_evenement_complement(crise_evenement_complement_id),
    valeur TEXT,
    PRIMARY KEY (evenement_id, crise_evenement_complement_id)
);
