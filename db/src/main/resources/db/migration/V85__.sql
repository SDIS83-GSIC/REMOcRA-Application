INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type)
values(gen_random_uuid(), 'CARACTERISTIQUES_PENA_TOOLTIP_WEB', null, 'STRING');
INSERT INTO parametre(parametre_id, parametre_code, parametre_valeur, parametre_type)
values(gen_random_uuid(), 'CARACTERISTIQUES_PIBI_TOOLTIP_WEB', null, 'STRING');


-- Ajout de la nouvelle vue
CREATE OR REPLACE VIEW remocra.v_pei_last_mesures AS
WITH last_ctrl_debit_pression AS (
	SELECT visite.visite_pei_id AS pei_id,
            max(visite.visite_date) AS date
           FROM visite
           JOIN visite_ctrl_debit_pression vcdp ON vcdp.visite_ctrl_debit_pression_visite_id = visite.visite_id
          GROUP BY visite.visite_pei_id
)
    SELECT pei_id,
        visite_ctrl_debit_pression.visite_ctrl_debit_pression_debit as debit,
        visite_ctrl_debit_pression.visite_ctrl_debit_pression_pression as presion,
        visite_ctrl_debit_pression.visite_ctrl_debit_pression_pression_dyn as pression_dyn
    FROM last_ctrl_debit_pression
    JOIN visite ON visite.visite_pei_id = last_ctrl_debit_pression.pei_id AND visite.visite_date = last_ctrl_debit_pression.date
    JOIN visite_ctrl_debit_pression ON visite_ctrl_debit_pression.visite_ctrl_debit_pression_visite_id  = visite.visite_id;
