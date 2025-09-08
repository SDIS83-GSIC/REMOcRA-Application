ALTER TYPE remocra.type_task ADD VALUE 'INTEGRER_INCOMING_REMOCRA';

CREATE TYPE remocra."STATUT_SYNCHRONISATION" AS ENUM (
    'EN_COURS',
    'TERMINEE'
    );

ALTER TABLE incoming.tournee ADD COLUMN tournee_statut_synchronisaiton remocra."STATUT_SYNCHRONISATION";
