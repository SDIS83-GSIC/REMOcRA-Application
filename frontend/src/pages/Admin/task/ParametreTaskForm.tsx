import { useFormikContext } from "formik";
import { ReactNode } from "react";
import { Col, Row } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import AddRemoveComponent from "../../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import SeeMoreButton from "../../../components/Button/SeeMoreButton.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FileInput,
  FormContainer,
  Multiselect,
  NumberInput,
  RadioInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import {
  ParametreTableSynchroSIG,
  TaskEntity,
  TYPE_CHAMPS_UPDATE_SYNCHRO_COMMUNE,
  TYPE_SYNCHRONISATION_TABLE_SIG,
  TYPE_TASK_PARAMETRE,
} from "../../../Entities/TaskEntity.tsx";
import PARAMETRE_TASK_PLANIFICATION from "../../../enums/RadioParametreTaskPlanificationEnum.tsx";
import TaskType from "../../../enums/TaskTypeEnum.tsx";
import url from "../../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { TypeOrganismeType } from "../organisme/Organisme.tsx";

export const getInitialValues = (currentTask: TaskEntity) => ({
  taskId: currentTask?.taskId,
  taskType: currentTask?.taskType ?? TaskType.PERSONNALISE.id,
  taskActif: currentTask?.taskActif ?? false,
  taskPlanification: currentTask?.taskPlanification,
  taskExecManuelle: currentTask?.taskExecManuelle ?? false,
  taskParametres: currentTask?.taskParametres ?? null,
  taskNotification: currentTask?.taskNotification ?? null,

  isPlanificationEnabled: currentTask?.isPlanificationEnabled ?? false,
  radioPlanification: "personalized",

  everyXMinute: null,
  everyHourAtMinuteX: null,
  specifiedTimeHoure: null,
  specifiedTimeMinute: null,
});

export const prepareVariables = (values: TaskEntity) => ({
  taskId: values.taskId,
  taskType: values.taskType,
  taskActif: values.taskActif ?? false,
  taskPlanification:
    values.isPlanificationEnabled && getCronTab(values).trim().length > 0
      ? getCronTab(values).trim()
      : null,
  taskExecManuelle: values.taskExecManuelle ?? false,
  taskParametres: JSON.stringify(values.taskParametres) ?? null,
  taskNotification: JSON.stringify(values.taskNotification) ?? null,
});

const ParametreTaskForm = () => {
  const { values, setFieldValue }: { values: TaskEntity } = useFormikContext();

  const typeOrganismeState = useGet(url`/api/type-organisme/get-active`);

  const {
    handleShowClose: handleShowCloseFormulaire,
    activesKeys: activesKeysFormulaire,
  } = useAccordionState([true, false, false]);

  const {
    handleShowClose: handleShowCloseNotification,
    activesKeys: activesKeysNotification,
  } = useAccordionState([]);

  if (!typeOrganismeState.isResolved) {
    return;
  }

  const typeOrganismeList: IdCodeLibelleType[] = typeOrganismeState.data.map(
    (e: TypeOrganismeType) => {
      return {
        id: e.typeOrganismeId,
        libelle: e.typeOrganismeLibelle,
        code: e.typeOrganismeCode,
      };
    },
  );

  const parametreComponentsList: ReactNode[] = Object.entries(
    TaskType[values.taskType]?.parametre ?? {},
  ).map(([key, value]) => {
    switch (value.typeTaskParametre) {
      case TYPE_TASK_PARAMETRE.INTEGER: {
        return (
          <Row className="m-2">
            <NumberInput
              name={"taskParametres[" + key + "]"}
              label={value.label}
              required={value.required}
              tooltipText={value.tooltipMessage}
            />
          </Row>
        );
      }
      case TYPE_TASK_PARAMETRE.STRING: {
        return (
          <Row className="m-2">
            <TextInput
              name={"taskParametres[" + key + "]"}
              label={value.label}
              required={value.required}
              tooltipText={value.tooltipMessage}
            />
          </Row>
        );
      }
      case TYPE_TASK_PARAMETRE.BOOLEAN: {
        return (
          <Row className="m-2">
            <CheckBoxInput
              name={"taskParametres[" + key + "]"}
              label={value.label}
              tooltipText={value.tooltipMessage}
            />
          </Row>
        );
      }
      case TYPE_TASK_PARAMETRE.LISTE_TABLE_SYNCHRO_SIG: {
        if (values?.taskParametres["listeTableASynchroniser"] === undefined) {
          setFieldValue("taskParametres[listeTableASynchroniser]", []);
        }
        return (
          <Row className="m-2">
            <AddRemoveComponent
              name="taskParametres[listeTableASynchroniser]"
              createComponentToRepeat={createIterableParametreSynchroSIG}
              listeElements={values?.taskParametres["listeTableASynchroniser"]}
            />
          </Row>
        );
      }
      default: {
        return (
          <div>Le type de paramètre {value.typeTaskParametre} est inconnu.</div>
        );
      }
    }
  });

  return (
    <FormContainer>
      <Row>
        <Col>
          <CheckBoxInput name="taskActif" label="Tâche active :" />
        </Col>
        <Col>
          <SubmitFormButtons />
        </Col>
      </Row>
      <AccordionCustom
        activesKeys={activesKeysFormulaire}
        handleShowClose={handleShowCloseFormulaire}
        list={[
          {
            header: "Planification",
            content: (
              <>
                <div>
                  <CheckBoxInput
                    name="isPlanificationEnabled"
                    label="Tâche planifiée :"
                  />
                </div>
                <div>
                  <RadioInput
                    name="radioPlanification"
                    label="Toutes les X minutes"
                    value={PARAMETRE_TASK_PLANIFICATION.EVERY_X_MINUTE}
                  />
                  <NumberInput
                    name="everyXMinute"
                    disabled={
                      values.radioPlanification !==
                        PARAMETRE_TASK_PLANIFICATION.EVERY_X_MINUTE ||
                      !values.isPlanificationEnabled
                    }
                    min={0}
                    max={59}
                    step={1}
                  />{" "}
                  min
                </div>
                <div>
                  <RadioInput
                    name="radioPlanification"
                    label="Toutes les heures à minute précise"
                    value={PARAMETRE_TASK_PLANIFICATION.EVERY_HOUR_AT_MINUTE_X}
                  />
                  <NumberInput
                    name="everyHourAtMinuteX"
                    disabled={
                      values.radioPlanification !==
                        PARAMETRE_TASK_PLANIFICATION.EVERY_HOUR_AT_MINUTE_X ||
                      !values.isPlanificationEnabled
                    }
                    min={0}
                    max={59}
                    step={1}
                  />{" "}
                  min
                </div>
                <div>
                  <RadioInput
                    name="radioPlanification"
                    label="Tous les jours à heure précise"
                    value={PARAMETRE_TASK_PLANIFICATION.SPECIFIED_TIME}
                  />
                  <Row className="d-flex align-items-center">
                    <Col xs="3">
                      <NumberInput
                        name="specifiedTimeHoure"
                        disabled={
                          values.radioPlanification !==
                            PARAMETRE_TASK_PLANIFICATION.SPECIFIED_TIME ||
                          !values.isPlanificationEnabled
                        }
                        min={0}
                        max={23}
                        step={1}
                      />
                    </Col>
                    <Col xs="auto" className={"p-0"}>
                      <span className="mx-1">:</span>
                    </Col>
                    <Col xs="3">
                      <NumberInput
                        name="specifiedTimeMinute"
                        disabled={
                          values.radioPlanification !==
                            PARAMETRE_TASK_PLANIFICATION.SPECIFIED_TIME ||
                          !values.isPlanificationEnabled
                        }
                        min={0}
                        max={59}
                        step={1}
                      />
                    </Col>
                  </Row>
                </div>
                <div>
                  <RadioInput
                    name="radioPlanification"
                    label="Personnalisé"
                    value={PARAMETRE_TASK_PLANIFICATION.PERSONALIZED}
                  />
                  <TextInput
                    name="taskPlanification"
                    required={false}
                    disabled={
                      values.radioPlanification !==
                        PARAMETRE_TASK_PLANIFICATION.PERSONALIZED ||
                      !values.isPlanificationEnabled
                    }
                  />
                </div>
                {TaskType[values.taskType] !== TaskType.PERSONNALISE && (
                  <div>
                    <CheckBoxInput
                      name="taskExecManuelle"
                      label="Permettre l'exécution manuelle :"
                    />
                  </div>
                )}
              </>
            ),
          },
          ...(parametreComponentsList.length !== 0
            ? [
                {
                  header: "Paramètres",
                  content: parametreComponentsList,
                },
              ]
            : []),
          ...(TaskType[values.taskType] === TaskType.PERSONNALISE
            ? [
                {
                  header: "Fichiers de la tâche",
                  content: (
                    <FileInput
                      name={"zipFile"}
                      label={
                        "Fichier ZIP contenant le ou les fichiers hwf, et hpl"
                      }
                      accept={".zip"}
                      onChange={(e) => {
                        setFieldValue("zipFile", e.target.files[0]);
                      }}
                      tooltipText="Si des documents sont déjà présents sur le serveur pour ce job, ils seront remplacés par les nouveaux insérés"
                      required={values.taskId == null}
                    />
                  ),
                },
              ]
            : []),
          ...(TaskType[values.taskType].notification === true
            ? [
                {
                  header: "Notification",
                  content: (
                    <AccordionCustom
                      activesKeys={activesKeysNotification}
                      handleShowClose={handleShowCloseNotification}
                      list={[
                        {
                          header: "Type de destinataires",
                          content: (
                            <>
                              <Multiselect
                                name="taskNotification.typeDestinataire.contactOrganisme"
                                label="Contacts d'organisme de type :"
                                options={typeOrganismeList}
                                getOptionValue={(t) => t.id}
                                getOptionLabel={(t) => t.libelle}
                                value={
                                  values?.taskNotification?.typeDestinataire?.contactOrganisme?.map(
                                    (e) =>
                                      typeOrganismeList?.find(
                                        (r: IdCodeLibelleType) => r.id === e,
                                      ),
                                  ) ?? undefined
                                }
                                onChange={(typeOrganisme) => {
                                  const typeOrganismeId = typeOrganisme.map(
                                    (e) => e.id,
                                  );
                                  typeOrganismeId.length > 0
                                    ? setFieldValue(
                                        "taskNotification.typeDestinataire.contactOrganisme",
                                        typeOrganismeId,
                                      )
                                    : setFieldValue(
                                        "taskNotification.typeDestinataire.contactOrganisme",
                                        undefined,
                                      );
                                }}
                                isClearable={true}
                                required={false}
                              />
                              <Multiselect
                                name="taskNotification.typeDestinataire.utilisateurOrganisme"
                                label="Utilisateurs associés à un organisme de type :"
                                options={typeOrganismeList}
                                getOptionValue={(t) => t.id}
                                getOptionLabel={(t) => t.libelle}
                                value={
                                  values?.taskNotification?.typeDestinataire?.utilisateurOrganisme?.map(
                                    (e) =>
                                      typeOrganismeList?.find(
                                        (r: IdCodeLibelleType) => r.id === e,
                                      ),
                                  ) ?? undefined
                                }
                                onChange={(typeOrganisme) => {
                                  const typeOrganismeId = typeOrganisme.map(
                                    (e) => e.id,
                                  );
                                  typeOrganismeId.length > 0
                                    ? setFieldValue(
                                        "taskNotification.typeDestinataire.utilisateurOrganisme",
                                        typeOrganismeId,
                                      )
                                    : setFieldValue(
                                        "taskNotification.typeDestinataire.utilisateurOrganisme",
                                        undefined,
                                      );
                                }}
                                isClearable={true}
                                required={false}
                              />
                              <CheckBoxInput
                                name="taskNotification.typeDestinataire.contactGestionnaire"
                                label="Notifier les contacts de gestionnaire :"
                              />
                              <div className="bg-light p-2 border rounded mx-2">
                                <AddRemoveComponent
                                  name="taskNotification.typeDestinataire.saisieLibre"
                                  createComponentToRepeat={
                                    createIterableNotificationSaisieLibre
                                  }
                                  listeElements={
                                    values?.taskNotification.typeDestinataire
                                      .saisieLibre
                                  }
                                  label="Saisie libre d'adresse email"
                                />
                              </div>
                            </>
                          ),
                        },
                        {
                          header: "Objet",
                          content: <TextInput name="taskNotification.objet" />,
                        },
                        {
                          header: "Corps",
                          content: (
                            <TextAreaInput name="taskNotification.corps" />
                          ),
                        },
                      ]}
                    />
                  ),
                },
              ]
            : []),
        ]}
      />
    </FormContainer>
  );
};

export default ParametreTaskForm;

function createIterableNotificationSaisieLibre(index: number) {
  return <NotificationSaisieLibre index={index} />;
}

const NotificationSaisieLibre = ({ index }: { index: number }) => {
  return (
    <TextInput
      name={`taskNotification.typeDestinataire.saisieLibre[${index}]`}
      required={false}
    />
  );
};

function createIterableParametreSynchroSIG(
  index: number,
  listeElements: any[],
) {
  return (
    <ParametreSynchroSIGIterableForm
      index={index}
      listeElements={listeElements}
    />
  );
}

export const TooltipScriptVue = ({
  type,
}: {
  type: TYPE_SYNCHRONISATION_TABLE_SIG;
}) => {
  switch (type) {
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_COMMUNE:
      return (
        <>
          La vue doit s'appeler "v_commune_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_commune_id</strong>: Identifiant unique de la commune en
          base de données.
          <br />
          <strong>v_commune_libelle</strong>: Nom de la commune.
          <br />
          <strong>v_commune_code_insee</strong>: Code INSEE de la commune.
          <br />
          <strong>v_commune_code_postal</strong>: Code postal de la commune.
          <br />
          <strong>v_commune_geometrie</strong>: Emplacement géometrique de la
          commune.
          <br />
          <strong>v_commune_pprif</strong>: PPRIF de la commune.
          <br />
          <strong>v_commune_code</strong>: Code unique de la commune.
          <br />
          Toutes les colonnes doivent être définies dans la vue. Seule la
          colonne v_commune_code peut contenir des valeurs nulles.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_VOIE:
      return (
        <>
          La vue doit s'appeler "v_voie_sig" et contenir ces colonnes : <br />
          <strong>v_voie_id</strong>: Identifiant unique de la voie en base de
          données.
          <br />
          <strong>v_voie_libelle</strong>: Nom de la voie.
          <br />
          <strong>v_voie_geometrie</strong>: Emplacement géometrique de la voie.
          <br />
          <strong>v_voie_commune_id</strong>: Identifiant unique de la commune
          où se trouve la voie.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_AIRE:
      return (
        <>
          La vue doit s'appeler "v_dfci_aire_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_dfci_aire_sig_id</strong>: Identifiant unique de l'aire en
          base de données.
          <br />
          <strong>v_dfci_aire_sig_amenagement</strong>: Aménagement de l'aire.
          <br />
          <strong>v_dfci_aire_sig_date_gps</strong>: Date du relevé GPS de
          l'aire.
          <br />
          <strong>v_dfci_aire_sig_grande_dimension</strong>: Plus grand
          dimension de l'aire. Peut être nul.
          <br />
          <strong>v_dfci_aire_sig_petite_dimension</strong>: Plus petite
          dimension de l'aire. Peut être nul.
          <br />
          <strong>v_dfci_aire_sig_type</strong>: Type de l'aire.
          <br />
          <strong>v_dfci_aire_sig_dfci_piste_id</strong>: Piste référencée par
          l'aire. Peut être nul.
          <br />
          <strong>v_dfci_aire_sig_remarque</strong>: Remarque sur l'aire l'aire.
          Peut être nul.
          <br />
          <strong>v_dfci_aire_sig_geometrie</strong>: Emplacement géometrique de
          l'aire.
          <br />
          <strong>v_dfci_aire_sig_code</strong>: Code unique de l'aire.
          <br />
          <strong>v_dfci_aire_sig_version</strong>: Version en base de données
          de l'aire.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_PISTE:
      return (
        <>
          La vue doit s'appeler "v_dfci_piste_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_dfci_piste_sig_id</strong>: Identifiant unique de la piste
          en base de données.
          <br />
          <strong>v_dfci_piste_sig_adresse</strong>: Adresse de la piste. Peut
          être nul.
          <br />
          <strong>v_dfci_piste_sig_annee_programme</strong>: Année de
          programmation de la piste. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_annee_travaux</strong> : Année des derniers
          travaux. Peut être nul.
          <strong>v_dfci_piste_sig_circulation</strong>: Condition de
          circulation de la piste.
          <br />
          <strong>v_dfci_piste_sig_date_gps</strong>: Date du relevé GPS de la
          piste.
          <br />
          <strong>v_dfci_piste_sig_libelle</strong>: Libellé de la piste.
          <br />
          <strong>v_dfci_piste_sig_numero</strong>: Numéro de la piste.
          <br />
          <strong>v_dfci_piste_sig_ouverture</strong>: Ouverture de la piste à
          la cirulation publique.
          <br />
          <strong>v_dfci_piste_sig_est_dfci</strong>: La piste est une piste
          DFCI.
          <br />
          <strong>v_dfci_piste_sig_retournement</strong>: Retournement possible
          tous les km.
          <br />
          <strong>v_dfci_piste_sig_num_troncon</strong>: Numéro du tronçon dans
          la piste.
          <br />
          <strong>v_dfci_piste_sig_num_objectif</strong>: Numéro de l'objectif.
          Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_libelle_objectif</strong>: Libellé de
          l'objectif. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_geometrie</strong>: Emplacement géometrique
          de la piste.
          <br />
          <strong>v_dfci_piste_sig_impraticabilite</strong>: Cause de
          l'impraticabilité de la piste. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_travaux</strong>: Type de travaux sur la
          piste. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_voie</strong>: Type de voie DFCI.
          <br />
          <strong>v_dfci_piste_sig_impasse</strong>: Présence d'une impasse sur
          la piste.
          <br />
          <strong>v_dfci_piste_sig_foncier</strong>: Assise foncière du tronçon
          de piste. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_croisement</strong>: Possibilité de
          croisement sur la piste.
          <br />
          <strong>v_dfci_piste_sig_programme</strong>: Type de programme sur la
          piste. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_praticabilite</strong>: La piste est
          praticable.
          <br />
          <strong>v_dfci_piste_sig_remarque</strong>: Remarque sur la piste.
          Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_code</strong>: Code unique de la piste
          <br />
          <strong>v_dfci_piste_sig_dfci_categorie_piste_id</strong>: Identifiant
          de la catégorie de piste.
          <br />
          <strong>v_dfci_piste_sig_dfci_massif_id</strong>: Identifiant du
          massif où se trouve la piste.
          <br />
          <strong>v_dfci_piste_sig_dfci_prestataire_id</strong>: Identifiant du
          prestataire. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_dfci_ouvrage_id</strong>: Identifiant de
          l'ouvrage. Peut être nul.
          <br />
          <strong>v_dfci_piste_sig_version</strong>: Version en base de donnée
          de la piste.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_DEB:
      return (
        <>
          La vue doit s'appeler "v_dfci_deb_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_dfci_deb_sig_id</strong>: Identifiant unique du
          débroussaillement en base de données.
          <br />
          <strong>v_dfci_deb_sig_libelle</strong>: Libellé du débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_annee_programme</strong>: Année de
          programmation du débroussaillement. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_annee_travaux</strong> : Année des derniers
          travaux. Peut être nul.
          <strong>v_dfci_deb_sig_mois_travaux</strong>: Mois des travaux. Peut
          être nul.
          <br />
          <strong>v_dfci_deb_sig_annee_edition</strong>: Année d'édition du
          débroussaillement. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_largeur</strong>: Largeur du débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_surface</strong>: Surface du débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_geometrie</strong>: Emplacement géometrique du
          débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_travaux</strong>: Type de travaux du
          débroussaillement. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_type</strong>: Type de débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_programme</strong>: Type de programme sur le
          débroussaillement. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_remarque</strong>: Remarque sur le
          débroussaillement. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_code</strong>: Code unique du
          débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_dfci_massif_id</strong>: Identifiant du massif
          où se trouve le débroussaillement.
          <br />
          <strong>v_dfci_deb_sig_dfci_prestataire_id</strong>: Identifiant du
          prestataire. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_dfci_ouvrage_id</strong>: Identifiant de
          l'ouvrage. Peut être nul.
          <br />
          <strong>v_dfci_deb_sig_version</strong>: Version en base de donnée du
          débroussaillement.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_PANNEAU:
      return (
        <>
          La vue doit s'appeler "v_dfci_panneau_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_dfci_panneau_sig_id</strong>: Identifiant unique du panneau
          en base de données.
          <br />
          <strong>v_dfci_panneau_sig_type</strong>: Type de panneau.
          <br />
          <strong>v_dfci_panneau_sig_etat</strong>: Etat du panneau.
          <br />
          <strong>v_dfci_panneau_sig_bzero</strong>: B0 du panneau.
          <br />
          <strong>v_dfci_panneau_sig_date_gps</strong> : Relevé GPS du panneau.
          <strong>v_dfci_panneau_sig_position</strong>: Position du panneau.
          <br />
          <strong>v_dfci_panneau_sig_equipement</strong>: Equipement panneauté.
          <br />
          <strong>v_dfci_panneau_sig_dfci_piste_id</strong>: Piste où se trouve
          le panneau.
          <br />
          <strong>v_dfci_panneau_sig_num_piste</strong>: Présence du numéro de
          piste sur le panneau.
          <br />
          <strong>v_dfci_panneau_sig_libelle_piste</strong>: Présence du libelle
          de piste sur le panneau.
          <br />
          <strong>v_dfci_panneau_sig_geometrie</strong>: Emplacement géometrique
          du panneau.
          <br />
          <strong>v_dfci_panneau_sig_remarque</strong>: Remarque sur le panneau.
          Peut être nul.
          <br />
          <strong>v_dfci_panneau_sig_code</strong>: Code unique du panneau.
          <br />
          <strong>v_dfci_panneau_sig_version</strong>: Version en base de donnée
          du panneau.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );

    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_MASSIF:
      return (
        <>
          La vue doit s'appeler "v_dfci_massif_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_dfci_massif_sig_id</strong>: Identifiant unique du massif.
          en base de données.
          <br />
          <strong>v_dfci_massif_sig_code</strong>: Code unique du massif.
          <br />
          <strong>v_dfci_massif_sig_libelle</strong>: Libellé du massif.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_CATEGORIE_PISTE:
      return (
        <>
          La vue doit s'appeler "v_dfci_categorie_piste_sig" et contenir ces
          colonnes : <br />
          <strong>v_dfci_categorie_piste_sig_id</strong>: Identifiant unique de
          la catégorie de piste. en base de données.
          <br />
          <strong>v_dfci_categorie_piste_sig_code</strong>: Code unique de la
          catégorie de piste.
          <br />
          <strong>v_dfci_categorie_piste_sig_libelle</strong>: Libellé de la
          catégorie de piste.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_OUVRAGE:
      return (
        <>
          La vue doit s'appeler "v_dfci_ouvrage_sig" et contenir ces colonnes :{" "}
          <br />
          <strong>v_dfci_ouvrage_sig_id</strong>: Identifiant unique de
          l'ouvrage en base de données.
          <br />
          <strong>v_dfci_ouvrage_sig_code</strong>: Code unique de l'ouvrage.
          <br />
          <strong>v_dfci_ouvrage_sig_libelle</strong>: Libellé de l'ouvrage.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_PRESTATAIRE:
      return (
        <>
          La vue doit s'appeler "v_dfci_prestataire_sig" et contenir ces
          colonnes : <br />
          <strong>v_dfci_prestataire_sig_id</strong>: Identifiant unique du
          prestataire. en base de données.
          <br />
          <strong>v_dfci_prestataire_sig_code</strong>: Code unique du
          prestataire.
          <br />
          <strong>v_dfci_prestataire_sig_libelle</strong>: Libellé du
          prestataire.
          <br />
          Toutes les colonnes sont obligatoires.
        </>
      );
  }
};

export function getDefaultValueForScriptVue(
  type: TYPE_SYNCHRONISATION_TABLE_SIG,
) {
  switch (type) {
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_COMMUNE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_commune_sig AS SELECT commune.commune_id AS v_commune_sig_id, commune.commune_libelle AS v_commune_sig_libelle, commune.commune_code_insee AS v_commune_sig_code_insee, commune.commune_code_postal AS v_commune_sig_code_postal, commune.commune_geometrie AS v_commune_sig_geometrie, commune.commune_pprif AS v_commune_sig_pprif, commune.commune_code AS v_commune_sig_code FROM remocra.commune;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_VOIE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_voie_sig AS SELECT voie.voie_id AS v_voie_sig_id, voie.voie_libelle AS v_voie_sig_libelle, voie.voie_geometrie AS v_voie_sig_geometrie, voie.voie_commune_id AS v_voie_sig_commune_id FROM remocra.voie;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.STOCKAGE_SIMPLE:
      return null;
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_AIRE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_aire_sig AS SELECT dfci_aire_id AS v_dfci_aire_sig_id, dfci_aire_amenagement AS v_dfci_aire_sig_amenagement, dfci_aire_date_gps AS v_dfci_aire_sig_date_gps, dfci_aire_grande_dimension AS v_dfci_aire_sig_grande_dimension, dfci_aire_petite_dimension AS v_dfci_aire_sig_petite_dimension, dfci_aire_type AS v_dfci_aire_sig_type, dfci_aire_dfci_piste_id AS v_dfci_aire_sig_dfci_piste_id, dfci_aire_remarque AS v_dfci_aire_sig_remarque, dfci_aire_geometrie AS v_dfci_aire_sig_geometrie, dfci_aire_code AS v_dfci_aire_sig_code, dfci_aire_version AS v_dfci_aire_sig_version FROM remocra.dfci_aire;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_CATEGORIE_PISTE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_categorie_piste_sig AS SELECT dfci_categorie_piste_id AS v_dfci_categorie_piste_sig_id, dfci_categorie_piste_code AS v_dfci_categorie_piste_sig_code, dfci_categorie_piste_libelle AS v_dfci_categorie_piste_sig_libelle FROM remocra.dfci_categorie_piste;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_DEB:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_deb_sig AS SELECT dfci_deb_id AS v_dfci_deb_sig_id, dfci_deb_libelle AS v_dfci_deb_sig_libelle, dfci_deb_annee_programme AS v_dfci_deb_sig_annee_programme, dfci_deb_annee_travaux AS v_dfci_deb_sig_annee_travaux, dfci_deb_mois_travaux AS v_dfci_deb_sig_mois_travaux, dfci_deb_annee_edition AS v_dfci_deb_sig_annee_edition, dfci_deb_largeur AS v_dfci_deb_sig_largeur, dfci_deb_surface AS v_dfci_deb_sig_surface, dfci_deb_geometrie AS v_dfci_deb_sig_geometrie, dfci_deb_type AS v_dfci_deb_sig_type, dfci_deb_programme AS v_dfci_deb_sig_programme, dfci_deb_travaux AS v_dfci_deb_sig_travaux, dfci_deb_remarque AS v_dfci_deb_sig_remarque, dfci_deb_code AS v_dfci_deb_sig_code, dfci_deb_dfci_massif_id AS v_dfci_deb_sig_dfci_massif_id, dfci_deb_dfci_prestataire_id AS v_dfci_deb_sig_dfci_prestataire_id, dfci_deb_dfci_ouvrage_id AS v_dfci_deb_sig_dfci_ouvrage_id, dfci_deb_version AS v_dfci_deb_sig_version FROM remocra.dfci_deb;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_MASSIF:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_massif_sig AS SELECT dfci_massif_id AS v_dfci_massif_sig_id, dfci_massif_code AS v_dfci_massif_sig_code, dfci_massif_libelle AS v_dfci_massif_sig_libelle FROM remocra.dfci_massif;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_OUVRAGE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_ouvrage_sig AS SELECT dfci_ouvrage_id AS v_dfci_ouvrage_sig_id, dfci_ouvrage_code AS v_dfci_ouvrage_sig_code, dfci_ouvrage_libelle AS v_dfci_ouvrage_sig_libelle FROM remocra.dfci_ouvrage;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_PANNEAU:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_panneau_sig AS SELECT dfci_panneau_id AS v_dfci_panneau_sig_id, dfci_panneau_type AS v_dfci_panneau_sig_type, dfci_panneau_etat AS v_dfci_panneau_sig_etat, dfci_panneau_bzero AS v_dfci_panneau_sig_bzero, dfci_panneau_date_gps AS v_dfci_panneau_sig_date_gps, dfci_panneau_position AS v_dfci_panneau_sig_position, dfci_panneau_equipement AS v_dfci_panneau_sig_equipement, dfci_panneau_dfci_piste_id AS v_dfci_panneau_sig_dfci_piste_id, dfci_panneau_num_piste AS v_dfci_panneau_sig_num_piste, dfci_panneau_libelle_piste AS v_dfci_panneau_sig_libelle_piste, dfci_panneau_remarque AS v_dfci_panneau_sig_remarque, dfci_panneau_geometrie AS v_dfci_panneau_sig_geometrie, dfci_panneau_code AS v_dfci_panneau_sig_code, dfci_panneau_version AS v_dfci_panneau_sig_version FROM remocra.dfci_panneau;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_DFCI_PISTE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_piste_sig AS SELECT dfci_piste_id AS v_dfci_piste_sig_id, dfci_piste_adresse AS v_dfci_piste_sig_adresse, dfci_piste_annee_programme AS v_dfci_piste_sig_annee_programme, dfci_piste_annee_travaux AS v_dfci_piste_sig_annee_travaux, dfci_piste_circulation AS v_dfci_piste_sig_circulation, dfci_piste_date_gps AS v_dfci_piste_sig_date_gps, dfci_piste_libelle AS v_dfci_piste_sig_libelle, dfci_piste_numero AS v_dfci_piste_sig_numero, dfci_piste_ouverture AS v_dfci_piste_sig_ouverture, dfci_piste_est_dfci AS v_dfci_piste_sig_est_dfci, dfci_piste_retournement AS v_dfci_piste_sig_retournement, dfci_piste_num_troncon AS v_dfci_piste_sig_num_troncon, dfci_piste_num_objectif AS v_dfci_piste_sig_num_objectif, dfci_piste_libelle_objectif AS v_dfci_piste_sig_libelle_objectif, dfci_piste_geometrie AS v_dfci_piste_sig_geometrie, dfci_piste_impraticabilite AS v_dfci_piste_sig_impraticabilite, dfci_piste_travaux AS v_dfci_piste_sig_travaux, dfci_piste_voie AS v_dfci_piste_sig_voie, dfci_piste_impasse AS v_dfci_piste_sig_impasse, dfci_piste_foncier AS v_dfci_piste_sig_foncier, dfci_piste_croisement AS v_dfci_piste_sig_croisement, dfci_piste_programme AS v_dfci_piste_sig_programme, dfci_piste_praticabilite AS v_dfci_piste_sig_praticabilite, dfci_piste_remarque AS v_dfci_piste_sig_remarque, dfci_piste_code AS v_dfci_piste_sig_code, dfci_piste_dfci_categorie_piste_id AS v_dfci_piste_sig_dfci_categorie_piste_id, dfci_piste_dfci_massif_id AS v_dfci_piste_sig_dfci_massif_id, dfci_piste_dfci_prestataire_id AS v_dfci_piste_sig_dfci_prestataire_id, dfci_piste_dfci_ouvrage_id AS v_dfci_piste_sig_dfci_ouvrage_id, dfci_piste_version AS v_dfci_piste_sig_version FROM remocra.dfci_piste;";
    case TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_DFCI_PRESTATAIRE:
      return "CREATE OR REPLACE VIEW entrepotsig.v_dfci_prestataire_sig AS SELECT dfci_prestataire_id AS v_dfci_prestataire_sig_id, dfci_prestataire_code AS v_dfci_prestataire_sig_code, dfci_prestataire_libelle AS v_dfci_prestataire_sig_libelle FROM remocra.dfci_prestataire;";
  }
}

const ParametreSynchroSIGIterableForm = ({
  index,
  listeElements,
}: {
  index: number;
  listeElements: ParametreTableSynchroSIG[];
}) => {
  const { setFieldValue } = useFormikContext();

  const listeTypeSynchronisationTableSig: IdCodeLibelleType[] = Object.entries(
    TYPE_SYNCHRONISATION_TABLE_SIG,
  ).map(([key, value]) => ({
    id: key,
    code: key,
    libelle: value.toString(),
  }));

  const listeTypeChampsUpdateSynchroCommune: IdCodeLibelleType[] =
    Object.entries(TYPE_CHAMPS_UPDATE_SYNCHRO_COMMUNE).map(([key, value]) => ({
      id: key,
      code: key,
      libelle: value.toString(),
    }));

  return (
    <Col>
      <Row>
        <TextInput
          name={`taskParametres[listeTableASynchroniser][${index}].schemaSource`}
          label="Schema source"
          placeholder="public"
        />
        <TextInput
          name={`taskParametres[listeTableASynchroniser][${index}].tableSource`}
          label="Table source"
          placeholder="commune"
        />
      </Row>
      <Row>
        <TextInput
          name={`taskParametres[listeTableASynchroniser][${index}].tableDestination`}
          label="Table destination"
          placeholder={"commune_from_sig"}
          required={false}
        />
      </Row>
      <Row>
        <SelectForm
          name={`taskParametres[listeTableASynchroniser][${index}].typeSynchronisation`}
          label="Type de synchronisation"
          listIdCodeLibelle={listeTypeSynchronisationTableSig}
          defaultValue={listeTypeSynchronisationTableSig.find(
            (e) => e.id === listeElements[index]?.typeSynchronisation,
          )}
          onChange={(e) => {
            setFieldValue(
              `taskParametres[listeTableASynchroniser][${index}].typeSynchronisation`,
              e.id,
            );
            setFieldValue(
              `taskParametres[listeTableASynchroniser][${index}].scriptCreationVue`,
              getDefaultValueForScriptVue(TYPE_SYNCHRONISATION_TABLE_SIG[e.id]),
            );
          }}
          required={true}
        />
        <SeeMoreButton id={"infoSynchroSig"}>
          {
            <>
              <p>
                Le type de synchronisation permet de définir le traitement à
                effectuer sur les données récupérées :
              </p>
              <ul>
                <li>
                  <strong>MISE_A_JOUR_REMOCRA_COMMUNE</strong> : Met à jour les
                  informations de la table <code>remocra.commune</code>, en
                  ajoutant de nouveaux éléments si nécessaire.
                  L&apos;identification se fait sur le{" "}
                  <strong>Code INSEE</strong>.
                </li>
                <li>
                  <strong>MISE_A_JOUR_REMOCRA_VOIE</strong> : Met à jour les
                  informations de la table <code>remocra.voie</code>. Ici, seule
                  la géométrie est mise à jour. L&apos;identification se fait
                  sur le <strong>nom de la voie ET la commune</strong>
                </li>
                <li>
                  <strong>STOCKAGE_SIMPLE</strong> : Permet d&apos;obtenir des
                  données d&apos;une table source pour les intégrer dans la base
                  de données REMOcRA. Après récupération, un{" "}
                  <strong>script SQL</strong> est exécuté pour, par exemple,
                  transformer les données géographiques afin de les mettre en
                  conformité avec le format attendu par REMOcRA.
                </li>
                <li>
                  <strong>MISE_A_JOUR_DFCI</strong> : Permet de synchroniser les
                  tables pour le module DFCI. La synchronisation est composée de
                  8 tables. Il est indispensable de paramètrer plusieurs pour en
                  synchroniser une.
                  <ul>
                    <li>
                      La table <strong>dfci_piste</strong> a besoin de :{" "}
                      <code>
                        dfci_categorie_piste, dfci_massif, dfci_prestataire,
                        dfci_ouvrage
                      </code>
                      .
                    </li>
                    <li>
                      La table <strong>dfci_deb</strong> a besoin de :{" "}
                      <code>dfci_massif, dfci_prestataire, dfci_ouvrage</code>.
                    </li>
                    <li>
                      La table <strong>dfci_aire</strong> a besoin de :{" "}
                      <code>dfci_piste</code>.
                    </li>
                    <li>
                      La table <strong>dfci_panneau</strong> a besoin de :{" "}
                      <code>dfci_piste</code>.
                    </li>
                  </ul>
                  Les autres tables{" "}
                  <strong>
                    (dfci_categorie_piste, dfci_massif, dfci_prestataire,
                    dfci_ouvrage)
                  </strong>{" "}
                  sont indépendantes étant des nomenclatures. Chaque
                  identification se fait sur le <strong>Code</strong>.
                </li>
              </ul>
            </>
          }
        </SeeMoreButton>
      </Row>
      {TYPE_SYNCHRONISATION_TABLE_SIG[
        listeElements[index]?.typeSynchronisation
      ] === TYPE_SYNCHRONISATION_TABLE_SIG.MISE_A_JOUR_REMOCRA_COMMUNE && (
        <Row>
          <Multiselect
            name={`taskParametres[listeTableASynchroniser][${index}].listeChampsAUpdate`}
            label="Champs REMOcRA à mettre à jour :"
            options={listeTypeChampsUpdateSynchroCommune}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            value={
              listeElements[index]?.listeChampsAUpdate?.map((e) =>
                listeTypeChampsUpdateSynchroCommune?.find(
                  (r: IdCodeLibelleType) => r.id === e,
                ),
              ) ?? undefined
            }
            onChange={(champsASynchro) => {
              const champsASynchroId = champsASynchro.map((e) => e.id);
              champsASynchroId.length > 0
                ? setFieldValue(
                    `taskParametres[listeTableASynchroniser][${index}].listeChampsAUpdate`,
                    champsASynchroId,
                  )
                : setFieldValue(
                    `taskParametres[listeTableASynchroniser][${index}].listeChampsAUpdate`,
                    undefined,
                  );
            }}
            isClearable={false}
            required={false}
            tooltipText="REMOcRA mettra à jour uniquement les informations sélectionnées, à partir des données extraites de la base SIG."
          />
        </Row>
      )}
      {TYPE_SYNCHRONISATION_TABLE_SIG[
        listeElements[index]?.typeSynchronisation
      ] === TYPE_SYNCHRONISATION_TABLE_SIG.STOCKAGE_SIMPLE && (
        <Row>
          <TextAreaInput
            required={false}
            name={`taskParametres[listeTableASynchroniser][${index}].scriptPostRecuperation`}
            label="Script post-récupération :"
          />
        </Row>
      )}
      {TYPE_SYNCHRONISATION_TABLE_SIG[
        listeElements[index]?.typeSynchronisation
      ] !== TYPE_SYNCHRONISATION_TABLE_SIG.STOCKAGE_SIMPLE &&
        TYPE_SYNCHRONISATION_TABLE_SIG[
          listeElements[index]?.typeSynchronisation
        ] !== undefined && (
          <Row>
            <TextAreaInput
              required={true}
              name={`taskParametres[listeTableASynchroniser][${index}].scriptCreationVue`}
              label="Script création de la vue :"
              tooltipText={
                <TooltipScriptVue
                  type={
                    TYPE_SYNCHRONISATION_TABLE_SIG[
                      listeElements[index]?.typeSynchronisation
                    ]
                  }
                />
              }
            />
          </Row>
        )}
    </Col>
  );
};

// * * * * * ?
export function getCronTab(values: TaskEntity) {
  switch (values.radioPlanification) {
    case PARAMETRE_TASK_PLANIFICATION.EVERY_X_MINUTE: {
      return `0 /${values.everyXMinute ?? 0} * * * ?`;
    }
    case PARAMETRE_TASK_PLANIFICATION.EVERY_HOUR_AT_MINUTE_X: {
      return `0 0 /${values.everyHourAtMinuteX ?? 0} * * ?`;
    }
    case PARAMETRE_TASK_PLANIFICATION.SPECIFIED_TIME: {
      return `0 ${values.specifiedTimeMinute ?? 0} ${values.specifiedTimeHoure ?? 0} * * ?`;
    }
    case PARAMETRE_TASK_PLANIFICATION.PERSONALIZED: {
      return values.taskPlanification;
    }
  }
}
