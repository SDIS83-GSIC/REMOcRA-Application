--Corrige les 'null' insérés dans les paramètres des rcci
UPDATE remocra.parametre set parametre_valeur = null
where parametre_valeur='null' and parametre_code in (
'LISTE_TYPE_ORGA_DDTM_ONF',
'LISTE_TYPE_ORGA_SDIS',
'LISTE_TYPE_ORGA_GENDARMERIE',
'LISTE_TYPE_ORGA_POLICE');
