-- Corrige la planification des tâches qui ont une planification du type "* * %", en remplaçant les deux premiers "*" par "0 0"
-- La planification actuelle exécute la tâche toutes les secondes, de toutes les minutes pendant l'heure indiquée

-- regexp_replace avec ^ va remplacer uniquement au début de la chaîne
UPDATE remocra.task
SET task_planification = new_data.new_planification
    FROM (
        SELECT 
            task_id,
            regexp_replace(task_planification, '^\* \* ', '0 0 ') AS new_planification
        FROM remocra.task
        WHERE task_planification ILIKE '* * %'
    ) new_data
WHERE task.task_id = new_data.task_id;
