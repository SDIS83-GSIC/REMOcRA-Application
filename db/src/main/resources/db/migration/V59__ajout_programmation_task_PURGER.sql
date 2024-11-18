INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
    gen_random_uuid(),
    'PURGER'::remocra.type_task,
    false,
    '* * 0 * * ?',
    true,
    '{
        "purgerDocumentTemp": true,
        "purgerJobTermine": true,
        "purgerJobJours": "30"
    }'::jsonb,
    NULL
) on conflict (task_type) do update
set task_actif = excluded.task_actif,
    task_planification = excluded.task_planification,
    task_exec_manuelle = excluded.task_exec_manuelle,
    task_parametres = excluded.task_parametres,
    task_notification = excluded.task_notification
;