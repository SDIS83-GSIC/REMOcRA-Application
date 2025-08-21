INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
    gen_random_uuid(),
    'RELANCER_CALCUL_DISPONIBILITE'::remocra.type_task,
    true,
    null,
    true,
    '{}'::jsonb,
    null
);

INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
    gen_random_uuid(),
    'RELANCER_CALCUL_NUMEROTATION'::remocra.type_task,
    true,
    null,
    true,
    '{}'::jsonb,
    null
);
