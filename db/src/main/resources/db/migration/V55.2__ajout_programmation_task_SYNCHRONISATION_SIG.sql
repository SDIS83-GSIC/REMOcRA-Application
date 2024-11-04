INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
    gen_random_uuid(),
    'SYNCHRONISATION_SIG'::remocra.type_task,
    false,
    '* * 1 * * ?',
    true,
    '{
       "listeTableASynchroniser": [
         {
           "tableSource": "commune",
           "schemaSource": "public",
           "tableDestination": "communes_sig",
           "listeChampsAUpdate": [
             "libelle",
             "code_postal",
             "geometrie"
           ],
           "typeSynchronisation": "MISE_A_JOUR_REMOCRA_COMMUNE",
           "scriptPostRecuperation": null
         },
         {
           "tableSource": "voie",
           "schemaSource": "public",
           "tableDestination": "voie_sig",
           "listeChampsAUpdate": null,
           "typeSynchronisation": "MISE_A_JOUR_REMOCRA_VOIE",
           "scriptPostRecuperation": null
         }
       ]
     }'::jsonb,
    NULL
) on conflict (task_type) do update
set task_actif = excluded.task_actif,
    task_planification = excluded.task_planification,
    task_exec_manuelle = excluded.task_exec_manuelle,
    task_parametres = excluded.task_parametres,
    task_notification = excluded.task_notification
;
