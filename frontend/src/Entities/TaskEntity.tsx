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
  LISTE_TABLE_SYNCHRO_SIG = "listeTableSynchroSIG",
}

export type ParametreTableSynchroSIG = {
  tableSource: string;
  schemaSource: string;
  tableDestination?: string;
  listeChampsAUpdate?: TYPE_CHAMPS_UPDATE_SYNCHRO_COMMUNE[];
  typeSynchronisation: TYPE_SYNCHRONISATION_TABLE_SIG;
  scriptPostRecuperation: string;
};

export enum TYPE_CHAMPS_UPDATE_SYNCHRO_COMMUNE {
  LIBELLE = "Libellé",
  CODE_POSTAL = "Code postal",
  GEOMETRIE = "Géométrie",
  PPRIF = "PPRIF",
}

export enum TYPE_SYNCHRONISATION_TABLE_SIG {
  MISE_A_JOUR_REMOCRA_COMMUNE = "Mise à jour REMOCRA.COMMUNE",
  MISE_A_JOUR_REMOCRA_VOIE = "Mise à jour REMOCRA.VOIE",
  STOCKAGE_SIMPLE = "Stockage simple",
}
