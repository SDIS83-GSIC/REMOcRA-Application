INSERT INTO remocra.task (task_id, task_type, task_actif, task_planification, task_exec_manuelle, task_parametres, task_notification)
VALUES(
  gen_random_uuid(),
  'MOBILE_EXPORT_LOG'::remocra.type_task,
  true,
  null,
  true,
  '{}'::jsonb,
  null
);
