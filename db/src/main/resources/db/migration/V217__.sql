-- Retire le droit ALERTES_EXPORT_C à tous groupes de fonctionnalités qui l'ont
UPDATE remocra.groupe_fonctionnalites gf
SET groupe_fonctionnalites_droits = array_replace(groupe_fonctionnalites_droits, 'ALERTES_EXPORT_C', null)
WHERE 'ALERTES_EXPORT_C' = ANY(groupe_fonctionnalites_droits);
