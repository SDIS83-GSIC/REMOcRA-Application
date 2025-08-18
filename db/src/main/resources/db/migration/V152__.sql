-- Ajout de paramètres permettant de définir les informations nécessaires à la notification lors de la réception de travaux
INSERT INTO remocra.parametre (parametre_id, parametre_code, parametre_valeur, parametre_type)
VALUES
(gen_random_uuid(), 'DFCI_TRAVAUX_DESTINATAIRE_EMAIL', 'nomail@nomail.com', 'STRING'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(), 'DFCI_TRAVAUX_OBJET_EMAIL', 'REMOcRA - Réception de travaux', 'STRING'::remocra."TYPE_PARAMETRE"),
(gen_random_uuid(),
'DFCI_TRAVAUX_CORPS_EMAIL',
'Bonjour,
Une déclaration de travaux a été déposée par un utilisateur de l''organisme #[ORGANISME_UTILISATEUR]#. Le fichier est disponible <a href = #[LIEN_TELECHARGEMENT]#>ici</a>.
Cordialement.
En cas d''incompréhension de ce message, merci de prendre contact avec votre SDIS.
Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.',
'STRING'::remocra."TYPE_PARAMETRE");
