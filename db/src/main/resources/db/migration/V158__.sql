-- Ajout de l'attribut protected dans la table groupe de couche pour gérer les couches particulières
ALTER TABLE remocra.groupe_couche ADD groupe_couche_protected BOOLEAN;

-- Ajout de l'attribut protected dans la table couche
ALTER TABLE remocra.couche ADD couche_protected BOOLEAN;

UPDATE remocra.groupe_couche SET groupe_couche_protected=false;
UPDATE remocra.couche SET couche_protected=false;

ALTER TABLE remocra.groupe_couche ALTER COLUMN groupe_couche_protected SET NOT NULL;
ALTER TABLE remocra.couche ALTER COLUMN couche_protected SET NOT NULL;

--Ajout d'un groupe protected pour les RCCI et d'une couche protected pour les RCCI
WITH new_groupe AS(
    INSERT INTO remocra.groupe_couche (groupe_couche_id, groupe_couche_code, groupe_couche_ordre, groupe_couche_libelle, groupe_couche_protected)
    VALUES (gen_random_uuid(),
            'RCCI',
            COALESCE(
                (SELECT MAX(groupe_couche_ordre) + 1
                FROM remocra.groupe_couche),
                1
            ),
            'RCCI',
            true)
    ON CONFLICT (groupe_couche_code) DO UPDATE
    SET groupe_couche_protected = EXCLUDED.groupe_couche_protected
    RETURNING groupe_couche_id
),
new_couche AS (
    INSERT INTO remocra.couche (couche_id, couche_code, couche_groupe_couche_id, couche_ordre, couche_libelle, couche_source, couche_url, couche_public, couche_active, couche_proxy, couche_protected)
    SELECT
        gen_random_uuid(),
        'RCCI',
        new_groupe.groupe_couche_id,
        COALESCE(
            (SELECT MAX(couche_ordre) + 1
            FROM remocra.couche),
            1
        ),
        'RCCI',
        'GEOJSON',
        '/api/rcci/layer',
        false,
        true,
        false,
        true
    FROM new_groupe
    ON CONFLICT (couche_code) DO UPDATE
    SET couche_groupe_couche_id = EXCLUDED.couche_groupe_couche_id,
        couche_source = EXCLUDED.couche_source,
        couche_url = EXCLUDED.couche_url,
        couche_public = EXCLUDED.couche_public,
        couche_proxy = EXCLUDED.couche_proxy,
        couche_protected = EXCLUDED.couche_protected
    RETURNING couche_id
)
INSERT INTO remocra.l_couche_module (couche_id, module_type)
SELECT new_couche.couche_id, 'RCI'
FROM new_couche
ON CONFLICT (couche_id, module_type) DO NOTHING;
