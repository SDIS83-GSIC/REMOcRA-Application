-- Mise à jour du champ pibi.pibi_numero_scp vers pibi.pibi_identifiant_gestionnaire
ALTER TABLE remocra.pibi RENAME COLUMN pibi_numero_scp TO pibi_identifiant_gestionnaire;
-- Ajout d'un commentaire, pour éviter la confusion avec pei.pei_gestionnaire_id
COMMENT ON COLUMN remocra.pibi.pibi_identifiant_gestionnaire IS 'Numéro utilisé par le gestionnaire pour identifier un PIBI dans son système d''information.';

-- Mise à jour des potentiels effets de bords
---- Rapports personnalisés et paramètres
UPDATE remocra.rapport_personnalise
SET rapport_personnalise_source_sql = regexp_replace(rapport_personnalise_source_sql, 'pibi_numero_scp', 'pibi_identifiant_gestionnaire', 'gi')
WHERE rapport_personnalise_source_sql ILIKE '%pibi_numero_scp%';
------ L'option 'gi' dans le regex permet : g -> remplace toutes les occurrences, i -> case-insensitive
UPDATE remocra.rapport_personnalise_parametre
SET rapport_personnalise_parametre_source_sql = regexp_replace(rapport_personnalise_parametre_source_sql, 'pibi_numero_scp', 'pibi_identifiant_gestionnaire', 'gi')
WHERE rapport_personnalise_parametre_source_sql ILIKE '%pibi_numero_scp%';

---- Modèles de courriers et paramètres
UPDATE remocra.modele_courrier
SET modele_courrier_source_sql = regexp_replace(modele_courrier_source_sql, 'pibi_numero_scp', 'pibi_identifiant_gestionnaire', 'gi')
WHERE modele_courrier_source_sql ILIKE '%pibi_numero_scp%';
UPDATE remocra.modele_courrier_parametre
SET modele_courrier_parametre_source_sql = regexp_replace(modele_courrier_parametre_source_sql, 'pibi_numero_scp', 'pibi_identifiant_gestionnaire', 'gi')
WHERE modele_courrier_parametre_source_sql ILIKE '%pibi_numero_scp%';
