-- Ce bloc "BEGIN ... COMMIT" permet de modifier la vue matérialisée v_pei_visite_date sans intervention humaine ;
-- le bloc s'occupe de créer la nouvelle vue matérialisée, de recréer toutes les vues dépendantes, de supprimer l'ancienne vue matérialisée et de renommer la nouvelle.
-- Ainsi, en sortie, on n'observe que la modification de la vue matérialisée, comme si on avait fait un "CREATE OR REPLACE" (qui n'est pas possible pour les vues matérialisées).

BEGIN;

--------------------------------------------------------------------------------
-- 1. Crée la nouvelle vue matérialisée
--------------------------------------------------------------------------------
CREATE MATERIALIZED VIEW remocra.v_pei_visite_date_new TABLESPACE pg_default AS
WITH last_visites AS (
    SELECT
        visite.visite_pei_id AS pei_id,
        max(
            CASE
                WHEN visite.visite_type_visite = 'RECEPTION'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time ZONE
            END
        ) AS last_reception,
        max(
            CASE
                WHEN visite.visite_type_visite = 'RECO_INIT'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time ZONE
            END
        ) AS last_reco_init,
        max(
            CASE
                WHEN visite.visite_type_visite = 'CTP'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time ZONE
            END
        ) AS last_ctp,
        max(
            CASE
                WHEN visite.visite_type_visite = 'ROP'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time ZONE
            END
        ) AS last_recop,
        max(
            CASE
                WHEN visite.visite_type_visite = 'NP'::"TYPE_VISITE" THEN visite.visite_date
                ELSE NULL::timestamp with time ZONE
            END
        ) AS last_np
    FROM visite
    GROUP BY visite.visite_pei_id
), param_mapping AS (
    SELECT
        t.nature_code,
        t.reco_param_code,
        t.ctrl_param_code
    FROM (VALUES ('PRIVE'::text,'PEI_RENOUVELLEMENT_RECO_PRIVE'::text,'PEI_RENOUVELLEMENT_CTRL_PRIVE'::text), ('PUBLIC'::text,'PEI_RENOUVELLEMENT_RECO_PUBLIC'::text,'PEI_RENOUVELLEMENT_CTRL_PUBLIC'::text), ('CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_RECO_CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_CTRL_CONVENTIONNE'::text), ('ICPE'::text,'PEI_RENOUVELLEMENT_RECO_ICPE'::text,'PEI_RENOUVELLEMENT_CTRL_ICPE'::text), ('ICPE_CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_RECO_ICPE_CONVENTIONNE'::text,'PEI_RENOUVELLEMENT_CTRL_ICPE_CONVENTIONNE'::text)) t(nature_code, reco_param_code, ctrl_param_code)
), param_values AS (
    SELECT
        param_mapping.nature_code AS nature_deci_code,
        p_reco.parametre_valeur::integer AS delta_days_reco,
        p_ctrl.parametre_valeur::integer AS delta_days_ctp
    FROM param_mapping
        LEFT JOIN parametre p_reco ON param_mapping.reco_param_code = p_reco.parametre_code
        LEFT JOIN parametre p_ctrl ON param_mapping.ctrl_param_code = p_ctrl.parametre_code
)
SELECT
    pei.pei_id,
    last_visites.last_reception,
    last_visites.last_reco_init,
    last_visites.last_ctp,
    last_visites.last_recop AS last_rop,
    last_visites.last_np,
    COALESCE(last_visites.last_recop, last_visites.last_reco_init) + ((p.delta_days_reco || ' day'::text)::interval) AS pei_next_rop,
    COALESCE(last_visites.last_ctp, last_visites.last_reception) + ((p.delta_days_ctp || ' day'::text)::interval) AS pei_next_ctp
FROM pei
    LEFT JOIN last_visites ON pei.pei_id = last_visites.pei_id
    JOIN nature_deci nd ON pei.pei_nature_deci_id = nd.nature_deci_id
    JOIN param_values p ON nd.nature_deci_code::text = p.nature_deci_code
WITH DATA;
-- On omet la création de l'index pour le moment, il n'est pas nécessaire pour l'instant et ça évitera de le renommer ensuite.

--------------------------------------------------------------------------------
-- 2. Recrée toutes les vues dépendantes (analyse du SQL) de la vue matérialisée
--------------------------------------------------------------------------------
DO $$
DECLARE
dep RECORD;
    original_sql TEXT;
    new_sql TEXT;

BEGIN
    FOR dep IN
    SELECT
        n.nspname AS schemaname,
        c.relname AS viewname,
        pg_get_viewdef(c.oid, true) AS view_definition
    FROM pg_class c
             JOIN pg_namespace n ON n.oid = c.relnamespace
    WHERE c.relkind = 'v'
      AND pg_get_viewdef(c.oid, true) ILIKE '%v_pei_visite_date%'

    LOOP
        original_sql := dep.view_definition;
        new_sql := format(
            'CREATE OR REPLACE VIEW %I.%I AS %s',
            dep.schemaname,
            dep.viewname,
            replace(original_sql, 'v_pei_visite_date', 'v_pei_visite_date_new')
        );
        -- RAISE NOTICE 'SQL: %', new_sql;
        EXECUTE new_sql;
    END LOOP;
END $$;

--------------------------------------------------------------------------------
-- 3. Drop CASCADE l’ancienne vue matérialisée
--------------------------------------------------------------------------------
DROP MATERIALIZED VIEW remocra.v_pei_visite_date CASCADE;

--------------------------------------------------------------------------------
-- 4. Renommer la nouvelle vue matérialisée
--------------------------------------------------------------------------------
ALTER MATERIALIZED VIEW remocra.v_pei_visite_date_new RENAME TO v_pei_visite_date;

--------------------------------------------------------------------------------
-- 5. Ajout de l'index sur la vue matérialisée
--------------------------------------------------------------------------------
CREATE UNIQUE INDEX idx_v_date_pei_id ON remocra.v_pei_visite_date(pei_id);

COMMIT;
