import { useFormikContext } from "formik";
import { useState } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { object } from "yup";
import SortableAddRemoveComponent from "../../../components/DragNDrop/SortableAddRemoveComponent.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
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
import { createComponentRapportPersoToRepeat } from "./SortableParametreRapportPersonnalise.tsx";

type RapportPersonnaliseType = {
  rapportPersonnaliseProtected: boolean;
  rapportPersonnaliseActif: boolean;
  rapportPersonnaliseCode: string;
  rapportPersonnaliseLibelle: string;
  rapportPersonnaliseIsSpatial: boolean;
  rapportPersonnaliseSourceSql: string;
  rapportPersonnaliseDescription: string;
  rapportPersonnaliseChampGeometrie: string;
  listeProfilDroitId: string[];
  rapportPersonnaliseModule: string;
  listeRapportPersonnaliseParametre: {
    rapportPersonnaliseParametreLibelle: string;
    rapportPersonnaliseParametreCode: string;
    rapportPersonnaliseParametreIsRequired: boolean;
    rapportPersonnaliseParametreDescription: string | undefined;
    rapportPersonnaliseParametreType: TYPE_PARAMETRE_RAPPORT_COURRIER;
    rapportPersonnaliseParametreSourceSqlDebut: string | undefined;
    rapportPersonnaliseParametreSourceSqlFin: string | undefined;
    rapportPersonnaliseParametreSourceSql: string | undefined;
    rapportPersonnaliseParametreSourceSqlId: string | undefined;
    rapportPersonnaliseParametreSourceSqlLibelle: string | undefined;
    rapportPersonnaliseParametreValeurDefaut: any | undefined;
  }[];
  unavailableCode: number[];
};

export const getInitialValues = (data?: RapportPersonnaliseType) => ({
  rapportPersonnaliseProtected: data?.rapportPersonnaliseProtected ?? false,
  rapportPersonnaliseActif: data?.rapportPersonnaliseActif ?? true,
  rapportPersonnaliseCode: data?.rapportPersonnaliseCode ?? null,
  rapportPersonnaliseLibelle: data?.rapportPersonnaliseLibelle ?? null,
  rapportPersonnaliseIsSpatial: data?.rapportPersonnaliseChampGeometrie != null,
  rapportPersonnaliseModule: data?.rapportPersonnaliseModule ?? null,
  rapportPersonnaliseDescription: data?.rapportPersonnaliseDescription ?? null,
  rapportPersonnaliseChampGeometrie:
    data?.rapportPersonnaliseChampGeometrie ?? null,
  listeProfilDroitId: data?.listeProfilDroitId ?? [],
  listeRapportPersonnaliseParametre:
    data?.listeRapportPersonnaliseParametre.map((e) => ({
      rapportPersonnaliseParametreSourceSqlDebut:
        e.rapportPersonnaliseParametreSourceSql
          ? e.rapportPersonnaliseParametreSourceSql?.split(
              e.rapportPersonnaliseParametreSourceSqlId
                ? e.rapportPersonnaliseParametreSourceSqlId
                : null,
            )[0]
          : null,
      rapportPersonnaliseParametreSourceSqlFin:
        e.rapportPersonnaliseParametreSourceSql
          ? e.rapportPersonnaliseParametreSourceSql
              ?.split(
                e.rapportPersonnaliseParametreSourceSqlLibelle
                  ? e.rapportPersonnaliseParametreSourceSqlLibelle +
                      " as libelle"
                  : null,
              )
              .slice(-1)[0]
          : null,
      // Le composant drag and drop a besoin d'un identifiant unique, donc on passe par un random
      id: Math.random(),
      ...e,
    })) ?? [],
  rapportPersonnaliseSourceSql:
    data?.rapportPersonnaliseChampGeometrie == null
      ? data?.rapportPersonnaliseSourceSql
      : null,
  rapportPersonnaliseSourceSqlDebut:
    data?.rapportPersonnaliseChampGeometrie !== null
      ? data?.rapportPersonnaliseSourceSql.split("ST_astext(")[0]
      : null,
  rapportPersonnaliseSourceSqlFin:
    data?.rapportPersonnaliseChampGeometrie !== null
      ? data?.rapportPersonnaliseSourceSql.split(") as geometrie ").slice(-1)[0]
      : null,
});

export const validationSchema = object({});

export const prepareVariables = (values: RapportPersonnaliseType) => ({
  rapportPersonnaliseProtected: values.rapportPersonnaliseProtected,
  rapportPersonnaliseActif: values.rapportPersonnaliseActif,
  rapportPersonnaliseCode: values.rapportPersonnaliseCode,
  rapportPersonnaliseLibelle: values.rapportPersonnaliseLibelle,
  rapportPersonnaliseChampGeometrie: values.rapportPersonnaliseChampGeometrie,
  rapportPersonnaliseDescription: values.rapportPersonnaliseDescription,
  rapportPersonnaliseSourceSql: values.rapportPersonnaliseIsSpatial
    ? values.rapportPersonnaliseSourceSqlDebut +
      " ST_astext(" +
      values.rapportPersonnaliseChampGeometrie +
      ") as geometrie " +
      values.rapportPersonnaliseSourceSqlFin
    : values.rapportPersonnaliseSourceSql,
  rapportPersonnaliseModule: values.rapportPersonnaliseModule,
  listeProfilDroitId: values.listeProfilDroitId,
  listeRapportPersonnaliseParametre:
    values.listeRapportPersonnaliseParametre.map((e, index) => {
      return {
        ...e,
        rapportPersonnaliseParametreOrdre: index,
        rapportPersonnaliseParametreSourceSql:
          e.rapportPersonnaliseParametreSourceSqlDebut +
          " " +
          e.rapportPersonnaliseParametreSourceSqlId +
          " as id, " +
          e.rapportPersonnaliseParametreSourceSqlLibelle +
          " as libelle " +
          e.rapportPersonnaliseParametreSourceSqlFin,
      };
    }),
});

const RapportPersonnalise = () => {
  const { setFieldValue, values } = useFormikContext<RapportPersonnaliseType>();
  const rapportPersonnaliseTypeModule = useGet(
    url`/api/rapport-personnalise/get-type-module`,
  );

  const profilDroitState = useGet(url`/api/profil-droit`);

  const listeModule = rapportPersonnaliseTypeModule.data?.map((e) => ({
    id: e,
    code: e,
    libelle: e,
  }));

  const [stepActive, setStepActive] = useState(0);

  function setListeParametres(value: any) {
    setFieldValue("listeRapportPersonnaliseParametre", value);
  }

  return (
    <FormContainer>
      {stepActive === 0 ? (
        <>
          <Row className="mt-3">
            <Col>
              <CheckBoxInput name="rapportPersonnaliseActif" label="Actif" />
            </Col>
            <Col>
              <TextInput
                label={
                  <>
                    Code
                    <TooltipCustom
                      tooltipText={"Code unique du rapport"}
                      tooltipId={"code-rapport-personnalise"}
                    >
                      <IconInfo />
                    </TooltipCustom>
                  </>
                }
                name="rapportPersonnaliseCode"
                required={true}
                disabled={values?.rapportPersonnaliseProtected}
              />
            </Col>
            <Col>
              <TextInput
                label={
                  <>
                    Libellé
                    <TooltipCustom
                      tooltipText={"Libellé du rapport"}
                      tooltipId={"libelle-rapport-personnalise"}
                    >
                      <IconInfo />
                    </TooltipCustom>
                  </>
                }
                name="rapportPersonnaliseLibelle"
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
                name={`rapportPersonnaliseModule`}
                label="Rattaché au module"
                options={listeModule}
                getOptionValue={(t) => t.id}
                getOptionLabel={(t) => t.libelle}
                onChange={(e) => {
                  setFieldValue(
                    `rapportPersonnaliseModule`,
                    listeModule?.find((type) => type.id === e.id).id,
                  );
                }}
                defaultValue={listeModule?.find(
                  (type) => type.id === values.rapportPersonnaliseModule,
                )}
                required={true}
                readOnly={values?.rapportPersonnaliseProtected}
              />
            </Col>
          </Row>
        </>
      ) : // Si l'élément est protected, on ne donne pas accès à la suite du stepper
      stepActive === 1 && !values.rapportPersonnaliseProtected ? (
        <>
          <h3>Gestion des paramètres de la requête</h3>
          <p>
            Les codes des paramètres seront utilisé dans la requête. Eviter les
            caractères spéciaux et espace.
          </p>
          <Row className="mt-3">
            <Col>
              <SortableAddRemoveComponent
                createComponentToRepeat={createComponentRapportPersoToRepeat}
                nomListe={"listeRapportPersonnaliseParametre"}
                setData={setListeParametres}
                defaultElement={{
                  rapportPersonnaliseParametreLibelle: "",
                  rapportPersonnaliseParametreCode: "",
                  rapportPersonnaliseParametreIsRequired: false,
                  rapportPersonnaliseParametreDescription: "",
                  rapportPersonnaliseParametreType: null,
                }}
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
                {values.listeRapportPersonnaliseParametre.length > 0 &&
                  values.listeRapportPersonnaliseParametre.map((e, key) => (
                    <li key={key}>{e.rapportPersonnaliseParametreCode}</li>
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
                name="rapportPersonnaliseDescription"
                label="Description de la requête"
                required={false}
              />
            </Col>
          </Row>
          <Row className="mt-3">
            <Col>
              <CheckBoxInput
                name="rapportPersonnaliseIsSpatial"
                label="Remonte un champ spatial"
                required={false}
                onChange={(v) => {
                  setFieldValue(
                    "rapportPersonnaliseIsSpatial",
                    v.target.checked,
                  );
                  if (
                    v.target.checked &&
                    values.rapportPersonnaliseSourceSqlDebut == null
                  ) {
                    setFieldValue(
                      "rapportPersonnaliseSourceSqlDebut",
                      "SELECT",
                    );
                    setFieldValue("rapportPersonnaliseSourceSqlFin", "FROM");
                  } else {
                    setFieldValue("rapportPersonnaliseChampGeometrie", null);
                  }
                }}
              />
            </Col>
          </Row>
          <Row className="mt-3">
            {!values.rapportPersonnaliseIsSpatial ? (
              <Col>
                <TextAreaInput
                  name="rapportPersonnaliseSourceSql"
                  label="Requête SQL"
                />
              </Col>
            ) : (
              <>
                <Col>
                  <TextAreaInput
                    name="rapportPersonnaliseSourceSqlDebut"
                    label="Requête SQL"
                  />
                </Col>
                <Row className="m-2">
                  <Col>St_Astext&#40;</Col>
                  <Col>
                    <TextInput name={`rapportPersonnaliseChampGeometrie`} />
                  </Col>
                  <Col>&#41; as geometrie</Col>
                </Row>
                <Col>
                  <TextAreaInput name="rapportPersonnaliseSourceSqlFin" />
                </Col>
              </>
            )}
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
        {/* Si l'élément est protected, on ne donne pas accès à la suite du stepper, donc pas de bouton suivant */}
        {stepActive !== 2 && !values.rapportPersonnaliseProtected && (
          <Col>
            <Button
              disabled={
                (stepActive === 1 &&
                  values.unavailableCode &&
                  values.unavailableCode.length > 0) ||
                isEmptyOrNull(values.rapportPersonnaliseModule) ||
                isEmptyOrNull(values.rapportPersonnaliseCode) ||
                isEmptyOrNull(values.rapportPersonnaliseLibelle) ||
                (stepActive === 1 &&
                  (values.listeRapportPersonnaliseParametre.some((e) =>
                    isEmptyOrNull(e.rapportPersonnaliseParametreType),
                  ) ||
                    values.listeRapportPersonnaliseParametre.some((e) =>
                      isEmptyOrNull(e.rapportPersonnaliseParametreCode),
                    ) ||
                    values.listeRapportPersonnaliseParametre.some((e) =>
                      isEmptyOrNull(e.rapportPersonnaliseParametreLibelle),
                    ) ||
                    values.listeRapportPersonnaliseParametre
                      .filter(
                        (e) =>
                          TYPE_PARAMETRE_RAPPORT_COURRIER[
                            e.rapportPersonnaliseParametreType
                          ] === TYPE_PARAMETRE_RAPPORT_COURRIER.SELECT_INPUT,
                      )
                      .some(
                        (e) =>
                          isEmptyOrNull(
                            e.rapportPersonnaliseParametreSourceSqlId,
                          ) ||
                          isEmptyOrNull(
                            e.rapportPersonnaliseParametreSourceSqlLibelle,
                          ),
                      )))
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
      {/* Si l'élément est protected, la première étape est aussi la dernière, on permet la sauvegarde */}
      {(stepActive === 2 || values.rapportPersonnaliseProtected) && (
        <SubmitFormButtons returnLink={true} />
      )}
    </FormContainer>
  );
};

export default RapportPersonnalise;
