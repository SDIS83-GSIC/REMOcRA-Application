import { useFormikContext } from "formik";
import { useState } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { object } from "yup";
import SortableAddRemoveComponent from "../../../components/DragNDrop/SortableAddRemoveComponent.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FileInput,
  FormContainer,
  Multiselect,
  SelectInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import {
  IconInfo,
  IconNextPage,
  IconPreviousPage,
} from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import {
  TYPE_PARAMETRE_RAPPORT_COURRIER,
  userParamRapportCourrier,
} from "../../../Entities/RapportCourrierEntity.tsx";
import url from "../../../module/fetch.tsx";
import isEmptyOrNull from "../../../utils/fonctionsUtils.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import { createComponentModeleCourrierToRepeat } from "./SortableParametreModeleCourrier.tsx";

type ModeleCourrierType = {
  modeleCourrierId: string | undefined;
  modeleCourrierActif: boolean;
  modeleCourrierCode: string;
  modeleCourrierLibelle: string;
  modeleCourrierSourceSql: string;
  modeleCourrierDescription: string;
  modeleCourrierObjetEmail: string;
  modeleCourrierCorpsEmail: string;
  listeProfilDroitId: string[];
  modeleCourrierModule: string;
  listeModeleCourrierParametre: {
    modeleCourrierParametreLibelle: string;
    modeleCourrierParametreCode: string;
    modeleCourrierParametreIsRequired: boolean;
    modeleCourrierParametreDescription: string | undefined;
    modeleCourrierParametreType: TYPE_PARAMETRE_RAPPORT_COURRIER;
    modeleCourrierParametreSourceSqlDebut: string | undefined;
    modeleCourrierParametreSourceSqlFin: string | undefined;
    modeleCourrierParametreSourceSql: string | undefined;
    modeleCourrierParametreSourceSqlId: string | undefined;
    modeleCourrierParametreSourceSqlLibelle: string | undefined;
    modeleCourrierParametreValeurDefaut: any | undefined;
  }[];
  documentId: string;
  documentNomFichier: string;
  documentRepertoire: string;
  unavailableCode: number[];
  documents: Document;
};

export const getInitialValues = (data?: ModeleCourrierType) => ({
  modeleCourrierId: data?.modeleCourrierId ?? null,
  modeleCourrierActif: data?.modeleCourrierActif ?? true,
  modeleCourrierCode: data?.modeleCourrierCode ?? null,
  modeleCourrierLibelle: data?.modeleCourrierLibelle ?? null,
  modeleCourrierModule: data?.modeleCourrierModule ?? null,
  modeleCourrierDescription: data?.modeleCourrierDescription ?? null,
  modeleCourrierObjetEmail: data?.modeleCourrierObjetEmail ?? null,
  modeleCourrierCorpsEmail: data?.modeleCourrierCorpsEmail ?? null,
  listeProfilDroitId: data?.listeProfilDroitId ?? [],
  listeModeleCourrierParametre:
    data?.listeModeleCourrierParametre.map((e) => ({
      modeleCourrierParametreSourceSqlDebut: e.modeleCourrierParametreSourceSql
        ? e.modeleCourrierParametreSourceSql?.split(
            e.modeleCourrierParametreSourceSqlId
              ? e.modeleCourrierParametreSourceSqlId
              : null,
          )[0]
        : null,
      modeleCourrierParametreSourceSqlFin: e.modeleCourrierParametreSourceSql
        ? e.modeleCourrierParametreSourceSql
            ?.split(
              e.modeleCourrierParametreSourceSqlLibelle
                ? e.modeleCourrierParametreSourceSqlLibelle + " as libelle"
                : null,
            )
            .slice(-1)[0]
        : null,
      // Le composant drag and drop a besoin d'un identifiant unique, donc on passe par un random
      id: Math.random(),
      ...e,
    })) ?? [],
  modeleCourrierSourceSql: data?.modeleCourrierSourceSql,
  documentId: data?.documentId,
  documentNomFichier: data?.documentNomFichier,
  documentRepertoire: data?.documentRepertoire,
  part: null,
});

export const validationSchema = object({});

export const prepareVariables = (values: ModeleCourrierType) => {
  const formData = new FormData();

  formData.append("part", values.part);

  formData.append(
    "modeleCourrier",
    JSON.stringify({
      modeleCourrierId: values?.modeleCourrierId,
      modeleCourrierActif: values.modeleCourrierActif,
      modeleCourrierCode: values.modeleCourrierCode,
      modeleCourrierLibelle: values.modeleCourrierLibelle,
      modeleCourrierDescription: values.modeleCourrierDescription,
      modeleCourrierSourceSql: values.modeleCourrierSourceSql,
      modeleCourrierModule: values.modeleCourrierModule,
      modeleCourrierObjetEmail: values.modeleCourrierObjetEmail,
      modeleCourrierCorpsEmail: values.modeleCourrierCorpsEmail,
      listeProfilDroitId: values.listeProfilDroitId,
      documentId: values.documentId,
      documentNomFichier: values.documentNomFichier,
      documentRepertoire: values.documentRepertoire,
      listeModeleCourrierParametre: values.listeModeleCourrierParametre.map(
        (e, index) => {
          return {
            ...e,
            modeleCourrierParametreOrdre: index,
            modeleCourrierParametreSourceSql:
              e.modeleCourrierParametreSourceSqlDebut +
              " " +
              e.modeleCourrierParametreSourceSqlId +
              " as id, " +
              e.modeleCourrierParametreSourceSqlLibelle +
              " as libelle " +
              e.modeleCourrierParametreSourceSqlFin,
          };
        },
      ),
    }),
  );

  return formData;
};

const ModeleCourrier = () => {
  const { setFieldValue, values } = useFormikContext<ModeleCourrierType>();
  const modeleCourrierTypeModule = useGet(
    url`/api/rapport-personnalise/get-type-module`,
  );

  const profilDroitState = useGet(url`/api/profil-droit`);

  const listeModule = modeleCourrierTypeModule.data?.map((e) => ({
    id: e,
    code: e,
    libelle: e,
  }));

  const [stepActive, setStepActive] = useState(0);

  function setListeParametres(value: any) {
    setFieldValue("listeModeleCourrierParametre", value);
  }

  return (
    <FormContainer>
      {stepActive === 0 ? (
        <>
          <Row className="mt-3">
            <Col>
              <CheckBoxInput name="modeleCourrierActif" label="Actif" />
            </Col>
            <Col>
              <TextInput
                label={
                  <>
                    Code
                    <TooltipCustom
                      tooltipText={"Code unique du modèle de courrier"}
                      tooltipId={"code-modele-courrier"}
                    >
                      <IconInfo />
                    </TooltipCustom>
                  </>
                }
                name="modeleCourrierCode"
                required={true}
              />
            </Col>
            <Col>
              <TextInput
                label={
                  <>
                    Libellé
                    <TooltipCustom
                      tooltipText={"Libellé du modèle"}
                      tooltipId={"libelle-mdoele-courrier"}
                    >
                      <IconInfo />
                    </TooltipCustom>
                  </>
                }
                name="modeleCourrierLibelle"
                required={true}
              />
            </Col>
          </Row>
          <Row className="mt-3">
            <Col>
              <Multiselect
                name={"listeProfilDroitId"}
                label="Profils droit ayant le droit d'exécuter la requête"
                options={profilDroitState?.data}
                getOptionValue={(t) => t.id}
                getOptionLabel={(t) => t.libelle}
                value={
                  values?.listeProfilDroitId?.map((e) =>
                    profilDroitState?.data?.find(
                      (r: IdCodeLibelleType) => r.id === e,
                    ),
                  ) ?? undefined
                }
                onChange={(profilDroit) => {
                  const profilDroitId = profilDroit.map(
                    (e: IdCodeLibelleType) => e.id,
                  );
                  profilDroitId.length > 0
                    ? setFieldValue("listeProfilDroitId", profilDroitId)
                    : setFieldValue("listeProfilDroitId", undefined);
                }}
                isClearable={true}
                required={false}
              />
            </Col>
            <Col>
              <SelectInput
                name={`modeleCourrierModule`}
                label="Rattaché au module"
                options={listeModule}
                getOptionValue={(t) => t.id}
                getOptionLabel={(t) => t.libelle}
                onChange={(e) => {
                  setFieldValue(
                    `modeleCourrierModule`,
                    listeModule?.find((type) => type.id === e.id).id,
                  );
                }}
                defaultValue={listeModule?.find(
                  (type) => type.id === values.modeleCourrierModule,
                )}
                required={true}
              />
            </Col>
          </Row>
        </>
      ) : stepActive === 1 ? (
        <>
          <h3>Gestion des paramètres de la requête</h3>
          <p>
            Les codes des paramètres seront utilisé dans la requête. Eviter les
            caractères spéciaux et espace.
          </p>
          <Row className="mt-3">
            <Col>
              <SortableAddRemoveComponent
                createComponentToRepeat={createComponentModeleCourrierToRepeat}
                nomListe={"listeModeleCourrierParametre"}
                setData={setListeParametres}
                defaultElement={{
                  modeleCourrierParametreLibelle: "",
                  modeleCourrierParametreCode: "",
                  modeleCourrierParametreIsRequired: false,
                  modeleCourrierParametreDescription: "",
                  modeleCourrierParametreType: null,
                }}
              />
            </Col>
          </Row>
        </>
      ) : stepActive === 2 ? (
        <>
          <Row className="mt-3">
            <Col className="bg-light border p-2 rounded">
              <IconInfo /> Vous pouvez utiliser le code
              &apos;#[LIEN_TELECHARGEMENT]#&apos; dans le corps de mail. Ce
              dernier sera automatiquement remplacé par le lien de
              téléchargement du courrier.
            </Col>
          </Row>
          <Row className="mt-3">
            <Col>
              <TextInput
                name="modeleCourrierObjetEmail"
                label="Objet de l'email"
              />
              <TextAreaInput
                name="modeleCourrierCorpsEmail"
                label="Corps de l'email"
              />
            </Col>
          </Row>
        </>
      ) : (
        <>
          <Row className="mt-3">
            <Col className="bg-light border p-2 rounded">
              <IconInfo /> Les codes des paramètres que vous pouvez utiliser
              dans la requête :
              <ul>
                {values.listeModeleCourrierParametre.length > 0 &&
                  values.listeModeleCourrierParametre.map((e, key) => (
                    <li key={key}>{e.modeleCourrierParametreCode}</li>
                  ))}
                {userParamRapportCourrier.map((param: string) => (
                  <li key={param}>{param}</li>
                ))}
              </ul>
            </Col>
          </Row>
          <Row className="mt-3">
            <Col>
              <TextAreaInput
                name="modeleCourrierDescription"
                label="Description de la requête"
                required={false}
              />
            </Col>
          </Row>
          <Row className="mt-3">
            <Col>
              <TextAreaInput
                name="modeleCourrierSourceSql"
                label="Requête SQL"
              />
            </Col>
          </Row>
          <Row className="mt-3">
            <Col>
              <FileInput
                name="part"
                accept="*.odt"
                label="Template"
                onChange={(e) => setFieldValue("part", e.target.files[0])}
              />
            </Col>
          </Row>
        </>
      )}
      <Row className="m-3 text-center">
        {stepActive !== 0 && (
          <Col>
            <Button
              variant="primary"
              onClick={() => {
                setStepActive(stepActive - 1);
              }}
            >
              <IconPreviousPage /> Précédent
            </Button>
          </Col>
        )}
        {stepActive !== 3 && (
          <Col>
            <Button
              disabled={
                (stepActive === 1 &&
                  values.unavailableCode &&
                  values.unavailableCode.length > 0) ||
                isEmptyOrNull(values.modeleCourrierModule) ||
                isEmptyOrNull(values.modeleCourrierCode) ||
                isEmptyOrNull(values.modeleCourrierLibelle) ||
                (stepActive === 1 &&
                  (values.listeModeleCourrierParametre.some((e) =>
                    isEmptyOrNull(e.modeleCourrierParametreType),
                  ) ||
                    values.listeModeleCourrierParametre.some((e) =>
                      isEmptyOrNull(e.modeleCourrierParametreCode),
                    ) ||
                    values.listeModeleCourrierParametre.some((e) =>
                      isEmptyOrNull(e.modeleCourrierParametreLibelle),
                    ) ||
                    values.listeModeleCourrierParametre
                      .filter(
                        (e) =>
                          TYPE_PARAMETRE_RAPPORT_COURRIER[
                            e.modeleCourrierParametreType
                          ] === TYPE_PARAMETRE_RAPPORT_COURRIER.SELECT_INPUT,
                      )
                      .some(
                        (e) =>
                          isEmptyOrNull(e.modeleCourrierParametreSourceSqlId) ||
                          isEmptyOrNull(
                            e.modeleCourrierParametreSourceSqlLibelle,
                          ),
                      ))) ||
                (stepActive === 2 &&
                  (isEmptyOrNull(values.modeleCourrierCorpsEmail) ||
                    isEmptyOrNull(values.modeleCourrierObjetEmail)))
              }
              variant="primary"
              onClick={() => setStepActive(stepActive + 1)}
            >
              Suivant <IconNextPage />
            </Button>
          </Col>
        )}
      </Row>
      {/* Si c'est la dernière step, on permet la sauvegarde */}
      {stepActive === 3 && <SubmitFormButtons returnLink={true} />}
    </FormContainer>
  );
};

export default ModeleCourrier;
