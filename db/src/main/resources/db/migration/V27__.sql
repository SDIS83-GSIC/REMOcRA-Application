-- Mise en état des paramètres
UPDATE remocra.parametre
SET parametre_valeur =
    COALESCE(
        (SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PRIVE'), '365')
WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PRIVE';

UPDATE remocra.parametre
SET parametre_valeur =
    COALESCE(
        (SELECT parametre_valeur FROM remocra.parametre WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PUBLIC'), '365')
WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PUBLIC';

-- Suppression des paramètres non utilisés depuis le patch "update_nature_deci"
DELETE FROM remocra.parametre
WHERE parametre.parametre_code IN
    ('PEI_RENOUVELLEMENT_RECO_PUBLIC_CONVENTIONNE','PEI_RENOUVELLEMENT_RECO_PRIVE_CONVENTIONNE');


-- Suppression de l'ancienne vue obsolète
DROP VIEW IF EXISTS remocra.v_pei_date_recop;

-- Ajout de la nouvelle vue
CREATE OR REPLACE VIEW remocra.v_pei_visite_date AS
WITH last_visites AS
(
    SELECT
        visite.visite_pei_id AS pei_id,
        max(CASE WHEN visite.visite_type_visite = 'RECEPTION'::"TYPE_VISITE"
            THEN visite.visite_date ELSE NULL::timestamp with time ZONE END) AS last_reception,
        max(CASE WHEN visite.visite_type_visite = 'RECO_INIT'::"TYPE_VISITE"
            THEN visite.visite_date ELSE NULL::timestamp with time ZONE END) AS last_reco_init,
        max(CASE WHEN visite.visite_type_visite = 'CTP'::"TYPE_VISITE"
            THEN visite.visite_date ELSE NULL::timestamp with time ZONE END) AS last_ctp,
        max(CASE WHEN visite.visite_type_visite = 'RECOP'::"TYPE_VISITE"
            THEN visite.visite_date ELSE NULL::timestamp with time ZONE END) AS last_recop,
        max(CASE WHEN visite.visite_type_visite = 'NP'::"TYPE_VISITE"
            THEN visite.visite_date ELSE NULL::timestamp with time ZONE END) AS last_np
    FROM visite
    GROUP BY visite.visite_pei_id
), param_values AS
(
    SELECT
        nd.nature_deci_code,
        CASE -- En V2, la date de prochaine reco pour les conventionnés était basé sur le paramètre des publics
            WHEN nd.nature_deci_code = 'PRIVE' THEN
                (SELECT parametre_valeur::integer FROM remocra.parametre
                WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PRIVE')
            WHEN nd.nature_deci_code = 'PUBLIC' OR nd.nature_deci_code = 'CONVENTIONNE' THEN
                (SELECT parametre_valeur::integer FROM remocra.parametre
                WHERE parametre_code = 'PEI_RENOUVELLEMENT_RECO_PUBLIC')
        END AS delta_days_reco,
        CASE -- Idem pour les CTP
            WHEN nd.nature_deci_code = 'PRIVE' THEN
                (SELECT parametre_valeur::integer FROM remocra.parametre
                WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PRIVE')
            WHEN nd.nature_deci_code = 'PUBLIC' OR nd.nature_deci_code = 'CONVENTIONNE' THEN
                (SELECT parametre_valeur::integer FROM remocra.parametre
                WHERE parametre_code = 'PEI_RENOUVELLEMENT_CTRL_PUBLIC')
        END AS delta_days_ctp
    FROM nature_deci nd
)
SELECT
    pei.pei_id,
    last_visites.last_reception,
    last_visites.last_reco_init,
    last_visites.last_ctp,
    last_visites.last_recop,
    last_visites.last_np,
    COALESCE(
        last_visites.last_recop,
        last_visites.last_reco_init
    ) + ((p.delta_days_reco || ' day'::text)::interval) AS pei_next_recop,
    last_visites.last_ctp + ((p.delta_days_ctp || ' day'::text)::interval) AS pei_next_ctp
FROM pei
    LEFT JOIN last_visites ON pei.pei_id = last_visites.pei_id
    JOIN nature_deci nd ON pei.pei_nature_deci_id = nd.nature_deci_id
    JOIN param_values p ON nd.nature_deci_code::text = p.nature_deci_code;
