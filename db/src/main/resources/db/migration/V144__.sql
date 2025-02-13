-- Suppression de la contrainte unique
ALTER TABLE remocra.task
DROP CONSTRAINT task_task_type_key;


-- Ajout d'une task "PERSONNALISE"
ALTER TYPE remocra.type_task ADD VALUE 'PERSONNALISE';
