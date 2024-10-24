ALTER TABLE remocra.niveau ADD COLUMN niveau_protected boolean;
ALTER TABLE remocra.type_pena_aspiration RENAME COLUMN type_pena_aspiration_type_actif TO type_pena_aspiration_actif;


UPDATE remocra.niveau set niveau_protected = true where niveau_code in ('VP', 'INFRA', 'SUPER');