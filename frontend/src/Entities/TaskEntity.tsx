import { NotificationRawEntity } from "./NotificationRawEntity.tsx";

export type TaskEntity = {
  taskId: string;
  taskType: string;
  taskActif: boolean;
  taskPlanification: string;
  taskExecManuelle: boolean;
  taskParametres: Map<
    string,
    string | boolean | number | ParametreTableSynchroSIG[]
  >;
  taskNotification: NotificationRawEntity;

  isPlanificationEnabled: boolean;
  radioPlanification: PARAMETRE_TASK_PLANIFICATION;
  everyXMinute: number;
  everyHourAtMinuteX: number;
  specifiedTimeHoure: number;
  specifiedTimeMinute: number;
};

export type SqueletteTaskEntity = {
  id: string;
  code: string;
  label: string;
  commentaire: string;
  parametre: Record<string, TaskParametreEntity> | null;
  notification: boolean;
  isConfigurable: boolean;
};

export type TaskParametreEntity = {
  typeTaskParametre: TYPE_TASK_PARAMETRE;
  required: boolean;
  label: string;
  tooltipMessage?: string;
};

export enum TYPE_TASK_PARAMETRE {
  INTEGER = "integer",
  BOOLEAN = "boolean",
  STRING = "string",
  LISTE_TABLE_SYNCHRO_SIG = "listeTableSynchroSIG",
}

export type ParametreTableSynchroSIG = {
  tableSource: string;
  schemaSource: string;
  tableDestination?: string;
  listeChampsAUpdate?: TYPE_CHAMPS_UPDATE_SYNCHRO_COMMUNE[];
  typeSynchronisation: TYPE_SYNCHRONISATION_TABLE_SIG;
  scriptPostRecuperation: string;
  scriptCreationVue: string;
};

export enum TYPE_CHAMPS_UPDATE_SYNCHRO_COMMUNE {
  LIBELLE = "Libellé",
  CODE_POSTAL = "Code postal",
  GEOMETRIE = "Géométrie",
  PPRIF = "PPRIF",
  CODE = "Code",
}

export enum TYPE_SYNCHRONISATION_TABLE_SIG {
  MISE_A_JOUR_REMOCRA_COMMUNE = "Mise à jour REMOCRA.COMMUNE",
  MISE_A_JOUR_REMOCRA_VOIE = "Mise à jour REMOCRA.VOIE",
  MISE_A_JOUR_DFCI_CATEGORIE_PISTE = "Mise à jour REMOCRA.DFCI_CATEGORIE_PISTE",
  MISE_A_JOUR_DFCI_MASSIF = "Mise à jour REMOCRA.DFCI_MASSIF",
  MISE_A_JOUR_DFCI_PRESTATAIRE = "Mise à jour REMOCRA.DFCI_PRESTATAIRE",
  MISE_A_JOUR_DFCI_OUVRAGE = "Mise à jour REMOCRA.DFCI_OUVRAGE",
  MISE_A_JOUR_REMOCRA_DFCI_PISTE = "Mise à jour REMOCRA.DFCI_PISTE",
  MISE_A_JOUR_REMOCRA_DFCI_AIRE = "Mise à jour REMOCRA.DFCI_AIRE",
  MISE_A_JOUR_REMOCRA_DFCI_DEB = "Mise à jour REMOCRA.DFCI_DEB",
  MISE_A_JOUR_REMOCRA_DFCI_PANNEAU = "Mise à jour REMOCRA.DFCI_PANNEAU",
  STOCKAGE_SIMPLE = "Stockage simple",
}

export type TaskPersonnaliseEntity = TaskEntity & {
  taskLibelle: string;
};
