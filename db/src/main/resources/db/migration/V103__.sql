-- On retire la valeur d'enum des endroits où elle est assignée
---- remocra.profil_droit
WITH nouveaux_droits AS (
    SELECT
        profil_droit_id,
        ARRAY_AGG(droit ORDER BY droit) AS agg_droits
    FROM (
        SELECT
            profil_droit_id,
            UNNEST(profil_droit_droits) AS droit
        FROM remocra.profil_droit
        WHERE profil_droit_droits @> ARRAY['PERMIS_TRAITEMENT_E']::remocra."_DROIT"
    ) AS unnested_droits
    WHERE droit <> 'PERMIS_TRAITEMENT_E'
    GROUP BY profil_droit_id
)
UPDATE remocra.profil_droit remo
SET profil_droit_droits = new.agg_droits
FROM nouveaux_droits new
WHERE remo.profil_droit_id = new.profil_droit_id;
