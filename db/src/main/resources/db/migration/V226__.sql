-- Met a jour les paramètres de la synchro SIG pour inclure la création de vue
with tables_sync as (select jsonb_array_elements(task_parametres ->'listeTableASynchroniser') as elem
from remocra.task
WHERE task_type='SYNCHRONISATION_SIG'::remocra."type_task")
UPDATE remocra.task
SET task_parametres = jsonb_set(
        task_parametres,
        '{listeTableASynchroniser}',
        to_jsonb(
                (
                    SELECT jsonb_agg(
                                   CASE
                                       WHEN elem->>'typeSynchronisation' = 'MISE_A_JOUR_REMOCRA_VOIE' THEN
                                           jsonb_set(elem, '{scriptCreationVue}', '"CREATE OR REPLACE VIEW entrepotsig.v_voie_sig AS SELECT voie.voie_id AS v_voie_sig_id, voie.voie_libelle AS v_voie_sig_libelle, voie.voie_geometrie AS v_voie_sig_geometrie, voie.voie_commune_id AS v_voie_sig_commune_id FROM remocra.voie;"')
                                       WHEN elem->>'typeSynchronisation' = 'MISE_A_JOUR_REMOCRA_COMMUNE' THEN
                                           jsonb_set(elem, '{scriptCreationVue}', '"CREATE OR REPLACE VIEW entrepotsig.v_commune_sig AS SELECT commune.commune_id AS v_commune_sig_id, commune.commune_libelle AS v_commune_sig_libelle, commune.commune_code_insee AS v_commune_sig_code_insee, commune.commune_code_postal AS v_commune_sig_code_postal, commune.commune_geometrie AS v_commune_sig_geometrie, commune.commune_pprif AS v_commune_sig_pprif, commune.commune_code AS v_commune_sig_code FROM remocra.commune;"')
                                       ELSE
                                           elem
                                       END
                           )
                    FROM jsonb_array_elements(task_parametres->'listeTableASynchroniser') AS t(elem)
                )
        )
)
from tables_sync
WHERE task_type = 'SYNCHRONISATION_SIG'::remocra."type_task";
