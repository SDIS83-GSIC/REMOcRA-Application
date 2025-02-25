-- Retire les valeurs problématiques dans le paramétrage des tâches
---- IT_NOTIF_AVANT_FIN
UPDATE remocra.task
set task_notification='{
        "corps": "Madame, Monsieur,\nDes indisponibilités temporaires dont la date de fin programmée est imminente ont été paramétrées dans REMOCRA.\nLes PEIs concernés par ces indisponibilités temporaires sont les suivants :\n#LISTE_PEI_FIN_INDISPO#\n\nCordialement.\n#FOOTER#Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
        "objet": "REMOCRA - FIN D''INDISPONIBILITE TEMPORAIRE",
        "typeDestinataire": {
            "saisieLibre": [],
            "contactOrganisme": [],
            "contactGestionnaire": "false",
            "utilisateurOrganisme": []
        }
    }'::jsonb
WHERE task_type='IT_NOTIF_AVANT_FIN'::remocra."type_task";

---- IT_NOTIF_RESTE_INDISPO
UPDATE remocra.task
set task_notification='{
        "corps": "Madame, Monsieur,\nDes indisponibilités temporaires, dont la date de fin programmée est passée, ont été clôturées automatiquement dans REMOCRA.\nAttention, la liste des PEI ci-dessous reste malgré tout indisponible pour une autre raison :\n#LISTE_PEI_RESTE_INDISPO#\n\nCordialement.\n#FOOTER#Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
        "objet": "REMOCRA - FIN D''INDISPONIBILITE TEMPORAIRE",
        "typeDestinataire": {
            "saisieLibre": [],
            "contactOrganisme": [],
            "contactGestionnaire": "false",
            "utilisateurOrganisme": []
        }
    }'::jsonb
WHERE task_type='IT_NOTIF_RESTE_INDISPO'::remocra."type_task";

---- IT_NOTIF_AVANT_DEBUT
UPDATE remocra.task
set task_notification='{
        "corps": "Madame, Monsieur,\nDes indisponibilités temporaires dont la date de début programmée est imminente ont été paramétrées dans REMOCRA.\nLes PEIs concernés par ces indisponibilités temporaires sont les suivants :\n#LISTE_PEI_DEBUT_INDISPO#\n\nCordialement.\n#FOOTER#Ce message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
        "objet": "REMOCRA - DEBUT D''INDISPONIBILITE TEMPORAIRE",
        "typeDestinataire": {
            "saisieLibre": [],
            "contactOrganisme": [],
            "contactGestionnaire": "false",
            "utilisateurOrganisme": []
        }
    }'::jsonb
WHERE task_type='IT_NOTIF_AVANT_DEBUT'::remocra."type_task";

---- NOTIFIER_CHANGEMENTS_ETAT
UPDATE remocra.task
set task_notification='{
        "corps": "Bonjour, \n#resultsDispo##resultsIndispo##resultsNonConforme#Cordialement.\n#FOOTER#\n\nCe message vous a été envoyé automatiquement, merci de ne pas répondre à l''expéditeur.",
        "objet": "Objet de la notification",
        "typeDestinataire": {
            "saisieLibre": [],
            "contactOrganisme": [],
            "contactGestionnaire": "false",
            "utilisateurOrganisme": []
        }
    }'::jsonb
WHERE task_type='NOTIFIER_CHANGEMENTS_ETAT'::remocra."type_task";

-- SYNCHRO_UTILISATEUR
UPDATE remocra.task
set task_parametres='{"canSuppressUser": false}'::jsonb
WHERE task_type='SYNCHRO_UTILISATEUR'::remocra."type_task";

-- Assignation d'une valeur de paramètre cohérente avec le type attendu
UPDATE remocra.parametre set parametre_valeur = '[]'
WHERE parametre_code IN (
    'CARACTERISTIQUE_PENA','CARACTERISTIQUE_PIBI','PEI_COLONNES','CARACTERISTIQUES_PENA_TOOLTIP_WEB',
    'CARACTERISTIQUES_PIBI_TOOLTIP_WEB','PEI_LONGUE_INDISPONIBILITE_TYPE_ORGANISME'
);
