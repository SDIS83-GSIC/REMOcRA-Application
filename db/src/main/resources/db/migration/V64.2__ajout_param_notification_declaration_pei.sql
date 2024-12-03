-- Ajout de paramètres permettant de définir les informations nécessaires à la notification lors du dépot d'un fichier dans la déclaration d'un PEI
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES
(gen_random_uuid(), 'DECLARATION_PEI_DESTINATAIRE_EMAIL', 'nomail@nomail.com', 'STRING'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(), 'DECLARATION_PEI_OBJET_EMAIL', 'REMOcRA - Déclaration d''un PEI', 'STRING'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(),
'DECLARATION_PEI_CORPS_EMAIL',
'Bonjour,
Un dossier de déclaration de PEI(s) a été déposé par un utilisateur de l''organisme #[ORGANISME_UTILISATEUR]#. Le fichier est disponible <a href = #[LIEN_TELECHARGEMENT]#>ici</a>.
Cordialement.

En cas d''incompréhension de ce message, merci de prendre contact avec votre SDIS.
Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.',
'STRING'::remocra."TYPE_PARAMETRE");
