INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
    gen_random_uuid(),
    'IT_NOTIF_RESTE_INDISPO'::remocra.type_task,
    false,
    '0 * * * * ?',
    true,
    '{}',
    '{
        "corps": "Madame, Monsieur,\nDes indisponibilités temporaires, dont la date de fin programmée est passée, ont été clôturées automatiquement dans REMOCRA.\nAttention, la liste des PEI ci-dessous reste malgré tout indisponible pour une autre raison :\n#LISTE_PEI_RESTE_INDISPO#\n\nCordialement.\n#FOOTER#Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
        "objet": "REMOCRA - FIN D''INDISPONIBILITE TEMPORAIRE",
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
