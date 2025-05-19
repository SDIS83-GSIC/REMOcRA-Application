-- Correction paramètre type COMMUNE
UPDATE remocra.rapport_personnalise_parametre
SET
	rapport_personnalise_parametre_source_sql='SELECT * FROM (
		SELECT
			'''' AS id,
	        ''Tous'' AS libelle,
	        CAST(NULL AS TEXT) AS tricol
		UNION
	    SELECT
	        commune.commune_id::text as id,
	        commune.commune_libelle::text as libelle
	        ,commune.commune_libelle AS tricol
	    FROM remocra.commune
	        JOIN remocra.zone_integration zi ON ST_CONTAINS(zi.zone_integration_geometrie, commune.commune_geometrie)
	            AND zi.zone_integration_id = #ZONE_COMPETENCE_ID#
	) AS united_options
	ORDER BY
		tricol IS NOT NULL,
	    CAST(SUBSTRING(tricol FROM ''([0-9]+)'') AS INTEGER),
	    libelle;',
    rapport_personnalise_parametre_source_sql_id='commune.commune_id::text',
    rapport_personnalise_parametre_source_sql_libelle='commune.commune_libelle::text'
WHERE rapport_personnalise_parametre_id IN (
	SELECT rpp.rapport_personnalise_parametre_id
	FROM remocra.rapport_personnalise_parametre rpp
		JOIN remocra.rapport_personnalise rp ON rpp.rapport_personnalise_parametre_rapport_personnalise_id = rp.rapport_personnalise_id
	WHERE rpp.rapport_personnalise_parametre_code = 'COMMUNE'
		AND rp.rapport_personnalise_code IN ('PEI_ANOMALIES', 'PEI_DIAMETRE_CANALISATION', 'PEI_COMMUNE', 'PEI_GD')
);

-- Correction paramètre type DEBIT
UPDATE remocra.rapport_personnalise_parametre
SET rapport_personnalise_parametre_source_sql='with dernier_debit_pression as (
	        select distinct on (v.visite_pei_id)
	            vcdp.visite_ctrl_debit_pression_debit as id
	        from remocra.visite_ctrl_debit_pression vcdp
	        join remocra.visite v
	            on vcdp.visite_ctrl_debit_pression_visite_id = v.visite_id
	    ),
	    options as (
	        select   id::text as id, id::text as libelle   from dernier_debit_pression
	        union all
	        select '''' as id, ''tous'' as libelle
	    )
	    select distinct
COALESCE(id, ''nul'') AS id, COALESCE(libelle, ''Débit nul'') AS libelle
from options;',
rapport_personnalise_parametre_source_sql_id='id::text',
rapport_personnalise_parametre_source_sql_libelle='id::text'
WHERE rapport_personnalise_parametre_code='DEBIT'
	AND rapport_personnalise_parametre_rapport_personnalise_id IN
		(SELECT rapport_personnalise_id FROM remocra.rapport_personnalise WHERE rapport_personnalise_code IN ('PEI_DIAMETRE_CANALISATION', 'PEI_ANOMALIES'))
;
