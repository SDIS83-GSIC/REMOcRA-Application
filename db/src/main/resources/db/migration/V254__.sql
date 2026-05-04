--Ajout du paramètre ORGANISME_PROFIL_MAJ_SYNCHRO
--si on garde le profil et l'organisme de remocra lors de la synchro ou si on met a jour avec ceux du ldap
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES(gen_random_uuid(), 'ORGANISME_PROFIL_MAJ_SYNCHRO', 'false', 'BOOLEAN'::remocra."TYPE_PARAMETRE");
