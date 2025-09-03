-- Ajout d'un paramètre permettant de définir le libellé "Non conforme" pour les PEI
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES (gen_random_uuid(), 'PEI_LIBELLE_NON_CONFORME', 'Non conforme', 'STRING'::remocra."TYPE_PARAMETRE");
