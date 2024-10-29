INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
    gen_random_uuid(),
    'IT_NOTIF_AVANT_DEBUT'::remocra.type_task,
    false,
    '0 * * * * ?',
    true,
    '{
        "deltaMinuteNotificationDebut": "5"
    }'::jsonb,
    '{
        "corps": "Madame, Monsieur,\nDes indisponibilités temporaires dont la date de début programmée est imminente ont été paramétrées dans REMOCRA.\nLes PEIs concernés par ces indisponibilités temporaires sont les suivants :\n#LISTE_PEI_DEBUT_INDISPO#\n\nCordialement.\n#FOOTER#Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
        "objet": "REMOCRA - DEBUT D''INDISPONIBILITE TEMPORAIRE",
        "typeDestinataire":
            {
                "saisieLibre": [],
                "contactOrganisme": [],
                "contactGestionnaire": "false",
                "utilisateurOrganisme": []
            }
    }'::jsonb
) on conflict (task_type) do update
set task_actif = excluded.task_actif,
    task_planification = excluded.task_planification,
    task_exec_manuelle = excluded.task_exec_manuelle,
    task_parametres = excluded.task_parametres,
    task_notification = excluded.task_notification
;
