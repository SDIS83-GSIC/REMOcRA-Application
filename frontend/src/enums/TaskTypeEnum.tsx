import {
  SqueletteTaskEntity,
  TYPE_TASK_PARAMETRE,
} from "../Entities/TaskEntity.tsx";

const TaskType: Record<string, SqueletteTaskEntity> = {
  SYNCHRO_UTILISATEUR: {
    id: "SYNCHRO_UTILISATEUR",
    code: "SynchroUtilisateurTask",
    label: "Synchronisation des utilisateurs",
    commentaire: "",
    parametre: {
      canSuppressUser: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.BOOLEAN,
        required: false,
        label: "Supprimer les utilisateurs :",
        tooltipMessage: "",
      },
    },
    notification: false,
    isConfigurable: true,
  },
  NOTIFIER_CHANGEMENTS_ETAT: {
    id: "NOTIFIER_CHANGEMENTS_ETAT",
    code: "ChangementEtatPeiTask",
    label: "Changement d'état des PEI",
    commentaire: "",
    parametre: {},
    notification: true,
    isConfigurable: true,
  },
  IT_NOTIF_AVANT_DEBUT: {
    id: "IT_NOTIF_AVANT_DEBUT",
    code: "NotifAvantDebutIndispoTempTask",
    label: "Notifier avant le début d'une IT",
    commentaire: "",
    parametre: {
      deltaMinuteNotificationDebut: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.INTEGER,
        required: true,
        label: "Délai de récupération des événements (en minutes) :",
        tooltipMessage:
          "Doit être en adéquation avec la fréquence d'exécution de la tâche.",
      },
    },
    notification: true,
    isConfigurable: true,
  },
  IT_NOTIF_AVANT_FIN: {
    id: "IT_NOTIF_AVANT_FIN",
    code: "NotifAvantFinIndispoTempTask",
    label: "Notifier avant la fin d'une IT",
    commentaire: "",
    parametre: {
      deltaMinuteNotificationFin: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.INTEGER,
        required: true,
        label: "Délai de récupération des événements (en minutes) :",
        tooltipMessage:
          "Doit être en adéquation avec la fréquence d'exécution de la tâche.",
      },
    },
    notification: true,
    isConfigurable: true,
  },
  IT_NOTIF_RESTE_INDISPO: {
    id: "IT_NOTIF_RESTE_INDISPO",
    code: "NotifResteIndispoIndispoTempTask",
    label: "Notifier les PEI restés indisponibles après la levée d'une IT",
    commentaire: "",
    parametre: {},
    notification: true,
    isConfigurable: true,
  },
  BASCULE_AUTO_INDISPO_TEMP: {
    id: "BASCULE_AUTO_INDISPO_TEMP",
    code: "BasculeAutoIndispoTempTask",
    label: "Bascule automatique des indisponibilités temporaires",
    commentaire:
      "Cette tâche est nécessaire au bon fonctionnement des indisponibilités temporaires.\nSi celle-ci n'est pas active, le statut de disponibilité des PEI ne sera pas mis à jour automatiquement au départ ou à la levée d'une indisponibilité temporaire.",
    parametre: {},
    notification: false,
    isConfigurable: true,
  },
  SYNCHRONISATION_SIG: {
    id: "SYNCHRONISATION_SIG",
    code: "SynchronisationSIGTask",
    label: "Synchronisation SIG vers REMOcRA",
    commentaire: "",
    parametre: {
      listeTableASynchroniser: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.LISTE_TABLE_SYNCHRO_SIG,
        required: true,
        label: "",
        tooltipMessage: "",
      },
    },
    notification: false,
    isConfigurable: true,
  },
  PURGER: {
    id: "PURGER",
    code: "PurgerTask",
    label: "Purge",
    commentaire:
      "Permet de purger le contenu du répertoire /Document/tmp/. Seuls les éléments datant de moins de 24h seront conservés.\nPermet de purger les informations relatives à l'exécution des tâches terminées.",
    parametre: {
      purgerDocumentTemp: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.BOOLEAN,
        required: false,
        label:
          "Activer la suppresion des éléments du répertoire /Document/tmp/ :",
        tooltipMessage:
          "Seuls les éléments datant de moins de 24h seront conservés.",
      },
      purgerJobTermine: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.BOOLEAN,
        required: false,
        label:
          "Activer la suppresion des informations d'exécution des tâches terminées :",
        tooltipMessage: "Les logs des tâches en erreur seront conservés",
      },
      purgerJobJours: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.INTEGER,
        required: false,
        label:
          "Délai de conservation des informations d'exécution des tâches terminées (en jours) :",
        tooltipMessage:
          "Les logs des tâches en erreur seront conservés. Les logs des tâches Terminée, Notifié et bloquées au statut En cours seront supprimés si la date d'exécution est antérieure à ce paramètre.",
      },
    },
    notification: false,
    isConfigurable: true,
  },
  PERSONNALISEE: {
    id: "PERSONNALISEE",
    code: "personnalisee",
    label: "Perso",
    commentaire: "",
    parametre: {
      taskLibelle: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.STRING,
        required: true,
        label: "Libellé de la tâche spécifique",
      },
      taskCode: {
        typeTaskParametre: TYPE_TASK_PARAMETRE.STRING,
        required: true,
        label: "Nom du worflow dans Apache HOP",
        tooltipMessage:
          "Il s'agit généralement du nom du fichier. Vous pouvez ouvrir le fichier xml et reprendre la valeur qui est dans la balise <name> (3ème ligne)",
      },
    },
    notification: false,
    isConfigurable: false,
  },
  RELANCER_CALCUL_DISPONIBILITE: {
    id: "RELANCER_CALCUL_DISPONIBILITE",
    code: "RELANCER_CALCUL_DISPONIBILITE",
    label: "Relancer le calcul de disponibilité",
    commentaire: "",
    parametre: {},
    notification: false,
    isConfigurable: false,
  },
  RELANCER_CALCUL_NUMEROTATION: {
    id: "RELANCER_CALCUL_NUMEROTATION",
    code: "RELANCER_CALCUL_NUMEROTATION",
    label: "Relancer le calcul de la numérotation",
    commentaire: "",
    parametre: {},
    notification: false,
    isConfigurable: false,
  },
};

export default TaskType;
