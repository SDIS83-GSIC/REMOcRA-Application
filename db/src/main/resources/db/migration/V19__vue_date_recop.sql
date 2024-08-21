-- Mise en état des paramètres et ajout des paramètres manquants
UPDATE remocra.parametre
SET parametre_valeur =
    COALESCE(
        (SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE'),
        '365'
    )
WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE';

UPDATE remocra.parametre
SET parametre_valeur =
    COALESCE(
        (SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC'),
        '365'
    )
WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC';

INSERT INTO remocra.parametre (parametre_id,parametre_code,parametre_valeur,parametre_type)
VALUES
	((SELECT gen_random_uuid()),'PEI_RENOUVELLEMENT_RECO_PUBLIC_CONVENTIONNE',(SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC'),'INTEGER'::remocra."TYPE_PARAMETRE"),
	((SELECT gen_random_uuid()),'PEI_RENOUVELLEMENT_RECO_PRIVE_CONVENTIONNE',(SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE'),'INTEGER'::remocra."TYPE_PARAMETRE")
ON CONFLICT (parametre_code) DO NOTHING;

-- Insertion de la vue
CREATE OR REPLACE VIEW remocra.v_pei_date_recop AS
WITH last_visites AS (
	SELECT
        visite.visite_pei_id AS pei_id,
        MAX(CASE
	            WHEN visite.visite_type_visite = 'RECOP'
	            	THEN visite.visite_date
	            WHEN visite.visite_type_visite = 'RECO_INIT'
	            	THEN visite.visite_date
	            ELSE NULL
        	END
    	) AS max_date
    FROM remocra.visite
    WHERE visite.visite_type_visite IN ('RECOP', 'RECO_INIT')
    GROUP BY visite.visite_pei_id
),	param_values AS (
	SELECT
		'PUBLIC' AS nature_deci_code,
		parametre_valeur::INTEGER AS delta_days
	FROM remocra.parametre
	WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC'
	UNION ALL
	SELECT
		'PRIVE' AS nature_deci_code,
		parametre_valeur::INTEGER AS delta_days
	FROM remocra.parametre
	WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE'
	UNION ALL
	SELECT
		'PUBLIC_CONVENTIONNE' AS nature_deci_code,
		parametre_valeur::INTEGER AS delta_days
	FROM remocra.parametre
	WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC_CONVENTIONNE'
	UNION ALL
	SELECT
		'PRIVE_CONVENTIONNE' AS nature_deci_code,
		parametre_valeur::INTEGER AS delta_days
	FROM remocra.parametre
	WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE_CONVENTIONNE'
)
SELECT
	pei.pei_id,
	last_visites.max_date AS pei_last_recop,
	last_visites.max_date + (p.delta_days || ' day')::INTERVAL AS pei_next_recop
FROM remocra.pei
	LEFT JOIN last_visites ON pei.pei_id = last_visites.pei_id
	JOIN remocra.nature_deci nd ON pei.pei_nature_deci_id = nd.nature_deci_id
	JOIN param_values p ON nd.nature_deci_code = p.nature_deci_code;
