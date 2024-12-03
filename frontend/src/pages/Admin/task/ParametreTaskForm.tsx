import { useFormikContext } from "formik";
import { ReactNode } from "react";
import { Col, Row } from "react-bootstrap";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import AddRemoveComponent from "../../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import { useGet } from "../../../components/Fetch/useFetch.js";
import {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  NumberInput,
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
import TaskType from "../../../enums/TaskTypeEnum.tsx";
import url from "../../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { TypeOrganismeType } from "../organisme/Organisme.tsx";

export const getInitialValues = (currentTask: TaskEntity) => ({
  taskId: currentTask.taskId,
  taskType: currentTask.taskType,
  taskActif: currentTask.taskActif,
  taskPlanification: currentTask.taskPlanification,
  taskExecManuelle: currentTask.taskExecManuelle,
  taskParametres: currentTask.taskParametres,
  taskNotification: currentTask.taskNotification,
  isPlanificationEnabled: currentTask.isPlanificationEnabled,
});

export const prepareVariables = (values: TaskEntity) => ({
  taskId: values.taskId,
  taskType: values.taskType,
  taskActif: values.taskActif ?? false,
  taskPlanification: values.isPlanificationEnabled
    ? (values.taskPlanification ?? null)
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

  const parametreComponentsList: ReactNode[] = [];
  Object.entries(TaskType[values.taskType].parametre).map(([key, value]) => {
    switch (value.typeTaskParametre) {
      case TYPE_TASK_PARAMETRE.INTEGER: {
        parametreComponentsList.push(
          <NumberInput
            name={"taskParametres[" + key + "]"}
            label={value.label}
            required={value.required}
          />,
        );
        break;
      }
      case TYPE_TASK_PARAMETRE.BOOLEAN: {
        parametreComponentsList.push(
          <CheckBoxInput
            name={"taskParametres[" + key + "]"}
            label={value.label}
          />,
        );
        break;
      }
      case TYPE_TASK_PARAMETRE.LISTE_TABLE_SYNCHRO_SIG: {
        if (values?.taskParametres["listeTableASynchroniser"] === undefined) {
          setFieldValue("taskParametres[listeTableASynchroniser]", []);
        }
        parametreComponentsList.push(
          <AddRemoveComponent
            name="taskParametres[listeTableASynchroniser]"
            createComponentToRepeat={createIterableParametreSynchroSIG}
            listeElements={values?.taskParametres["listeTableASynchroniser"]}
          />,
        );
        break;
      }
      default: {
        parametreComponentsList.push(
          <a>Le type de paramètre {value.typeTaskParametre} est inconnue.</a>,
        );
        break;
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
          <SubmitFormButtons update={true} />
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
                {/* TODO : Gestion des la planification "simplifiée" (dans un autre commit)
                <div>
                  <p>Radio: Toutes les X minutes</p>
                  <NumberInput name="everyMinute" />
                </div>
                <div>
                  <p>Radio: Toutes les heures à minute précise</p>
                  <NumberInput name="everyMinute" />
                </div>
                <div>
                  <p>Radio: Toutes les jours à heure précise</p>
                  <NumberInput name="everyMinute" />
                </div>
                */}
                <div>
                  <TextInput
                    name="taskPlanification"
                    label="Radio: Personnalisé"
                    required={false}
                    disabled={!values.isPlanificationEnabled}
                  />
                </div>
                <div>
                  <CheckBoxInput
                    name="taskExecManuelle"
                    label="Permettre l'exécution manuelle :"
                  />
                </div>
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
    <>
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
              e.target.value,
            );
          }}
          required={true}
        />
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
          />
        </Row>
      )}
      {listeElements[index]?.typeSynchronisation ===
        TYPE_SYNCHRONISATION_TABLE_SIG.STOCKAGE_SIMPLE && (
        <Row>
          <TextAreaInput
            name={`taskParametres[listeTableASynchroniser][${index}].scriptPostRecuperation`}
            label="Script post-récupération :"
          />
        </Row>
      )}
    </>
  );
};
