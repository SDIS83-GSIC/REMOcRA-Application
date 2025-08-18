-- Ajout d'un paramètre permettant de masquer/afficher la saisie de Identifiant Gestionnaire dans le formulaire PIBI
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES (gen_random_uuid(), 'PEI_DISPLAY_IDENTIFIANT_GESTIONNAIRE', 'true', 'BOOLEAN'::remocra."TYPE_PARAMETRE");
