INSERT INTO remocra.task(task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES (
    gen_random_uuid(),
    'NOTIFIER_CHANGEMENTS_ETAT'::remocra.type_task,
    true,
    '0 /5 * * * ?',
    true,
    '{}'::jsonb,
    '{
       "corps": "Bonjour, \n#resultsDispo##resultsIndispo##resultsNonConforme#Cordialement.\n#FOOTER#\n\nCe message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
       "objet": "Objet de la notification",
       "typeDestinataire": {
         "saisieLibre": [],
         "contactOrganisme": [
           "COMMUNE",
           "CIS"
         ],
         "contactGestionnaire": "true",
         "utilisateurOrganisme": [
           "COMMUNE",
           "CIS"
         ]
       }
     }'::jsonb
) on conflict do nothing;
