alter table remocra.tournee add column tournee_date_derniere_realisation timestamp with time zone;

CREATE TYPE remocra.TYPE_COURRIER as ENUM(
    'RAPPORT_POST_ROP'
);

alter table modele_courrier add column modele_courrier_type remocra.TYPE_COURRIER null;

-- Ajout des droits
ALTER TYPE remocra."DROIT" ADD VALUE 'RAZ_MES_ROP_E';
ALTER TYPE remocra."DROIT" ADD VALUE 'ADMIN_ROP_A';