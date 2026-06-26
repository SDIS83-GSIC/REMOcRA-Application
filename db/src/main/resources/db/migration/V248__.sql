INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES (gen_random_uuid(), 'DFCI_LISTE_COUCHE', null, 'STRING'::remocra."TYPE_PARAMETRE");
ALTER TYPE remocra."DROIT" ADD VALUE 'DFCI_AIRE_U';
ALTER TYPE remocra."DROIT" ADD VALUE 'DFCI_PISTE_U';
ALTER TYPE remocra."DROIT" ADD VALUE 'DFCI_DEB_U';
ALTER TYPE remocra."DROIT" ADD VALUE 'DFCI_PANNEAU_U';
ALTER TYPE remocra."DROIT" ADD VALUE 'DFCI_GESTION_CONFLITS_R';
ALTER TYPE remocra."DROIT" ADD VALUE 'DFCI_GESTION_CONFLITS_A';
