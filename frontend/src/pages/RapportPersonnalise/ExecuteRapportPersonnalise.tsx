import { useFormikContext } from "formik";
import { useState } from "react";
import { Button, Col, Container, Row, Tab, Table, Tabs } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  NumberInput,
  SelectInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconExport, IconList } from "../../components/Icon/Icon.tsx";
import MapRapportPersonnalise from "../../components/Map/MapRapportPersonnalise/MapRapportPersonnalise.tsx";
import PaginationFront, {
  LIMIT,
} from "../../components/PaginationFront/PaginationFront.tsx";
import url from "../../module/fetch.tsx";
import { requiredString } from "../../module/validators.tsx";
import { TYPE_PARAMETRE_RAPPORT_PERSONNALISE } from "../Admin/rapportPersonnalise/SortableParametreRapportPersonnalise.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { downloadOutputFile } from "../../utils/fonctionsUtils.tsx";

type RapportPersonnaliseParametreType = {
  listeSelectInput: { id: string; libelle: string }[];
  rapportPersonnaliseParametreCode: string;
  rapportPersonnaliseParametreDescription: string | undefined;
  rapportPersonnaliseParametreId: string;
  rapportPersonnaliseParametreIsRequired: boolean;
  rapportPersonnaliseParametreLibelle: string;
  rapportPersonnaliseParametreValeurDefaut: string;
  rapportPersonnaliseParametreType: TYPE_PARAMETRE_RAPPORT_PERSONNALISE;
};

type RapportPersoWithParametreType = {
  rapportPersonnaliseDescription: string | undefined;
  rapportPersonnaliseId: string | undefined;
  rapportPersonnaliseLibelle: string | undefined;
  listeParametre: RapportPersonnaliseParametreType[];
};
const ExecuteRapportPersonnalise = () => {
  const { success: successToast, error: errorToast } = useToastContext();
  const { data: listeRapportPersoWithParametre } = useGet(
    url`/api/rapport-personnalise/parametres`,
  );

  const [tableau, setTableau] = useState<{
    headers: string[];
    values: any[];
    geometries: string[];
  }>();

  const [offset, setOffset] = useState<number>(0);
  const [activeTab, setActiveTab] = useState<string>("data");
  const [valuesFormik, setValuesFormik] = useState();

  return (
    <Container fluid>
      <PageTitle
        icon={<IconList />}
        title={"Exécuter des rapports personnalisés"}
      />
      <Row>
        <Col xs="auto" className="ms-auto">
          <Button
            onClick={() =>
              downloadOutputFile(
                "/api/rapport-personnalise/export-data",
                JSON.stringify({
                  rapportPersonnaliseId: valuesFormik?.rapportPersonnaliseId,
                  listeParametre: valuesFormik?.listeParametre,
                }),
                "rapport-personnalise.csv",
                "Import terminé",
                successToast,
                errorToast,
              )
            }
            disabled={valuesFormik == null || tableau == null}
          >
            Exporter les données <IconExport />
          </Button>
        </Col>
        <Col xs="auto">
          <Button
            onClick={() =>
              downloadOutputFile(
                "/api/rapport-personnalise/export-shp",
                JSON.stringify({
                  rapportPersonnaliseId: valuesFormik?.rapportPersonnaliseId,
                  listeParametre: valuesFormik?.listeParametre,
                }),
                "rapport-personnalise.zip",
                "Import terminé",
                successToast,
                errorToast,
              )
            }
            disabled={
              valuesFormik == null ||
              tableau == null ||
              tableau?.geometries == null ||
              tableau.geometries.length === 0
            }
          >
            Exporter les données carto <IconExport />
          </Button>
        </Col>
      </Row>
      <Row>
        <Col xs={12} lg={3}>
          <MyFormik
            initialValues={getInitialValues()}
            validationSchema={validationSchema}
            isPost={false}
            submitUrl={`/api/rapport-personnalise/generer`}
            prepareVariables={(values) => {
              const value = prepareVariables(
                values,
                listeRapportPersoWithParametre,
              );
              setValuesFormik(value);
              return value;
            }}
            onSubmit={(e) => {
              setTableau(e);
              setActiveTab("data");
            }}
          >
            <ExecuteRapportPersonnaliseForm
              listeRapportPersoWithParametre={listeRapportPersoWithParametre}
            />
          </MyFormik>
        </Col>
        <Col xs={12} lg={9}>
          <Tabs activeKey={activeTab} onSelect={(k) => setActiveTab(k)}>
            <Tab
              eventKey="data"
              title={"Données"}
              className="overflow-scroll h-75"
            >
              {tableau === null ? (
                <Row className="m-3 text-center">
                  <Col className="text-center">Aucune donnée à afficher</Col>
                </Row>
              ) : (
                <>
                  <Table bordered striped>
                    <thead>
                      <tr>
                        {tableau?.headers?.map((e, index) => (
                          <th key={index}>{e}</th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {tableau?.values
                        ?.slice(offset, offset + LIMIT)
                        ?.map((ligne, index) => {
                          return (
                            <tr key={index}>
                              {ligne.map((e, key) => (
                                <td key={key}>{e?.toString()}</td>
                              ))}
                            </tr>
                          );
                        })}
                    </tbody>
                  </Table>
                  {tableau?.values && (
                    <PaginationFront
                      values={tableau?.values}
                      offset={offset}
                      setOffset={setOffset}
                    />
                  )}
                </>
              )}
            </Tab>
            <Tab
              eventKey="map"
              title="Carte"
              disabled={
                tableau?.geometries == null || tableau.geometries.length === 0
              }
            >
              <MapRapportPersonnalise wkt={tableau?.geometries} />
            </Tab>
          </Tabs>
        </Col>
      </Row>
    </Container>
  );
};

export const getInitialValues = () => ({
  rapportPersonnaliseId: null,
});

export const validationSchema = object({
  rapportPersonnaliseId: requiredString,
});
export const prepareVariables = (
  values,
  listeRapportPersoWithParametre: RapportPersoWithParametreType[],
) => {
  // on va récupérer que les paramètres du rapport personnalisé
  const listeParametre = listeRapportPersoWithParametre
    .find((e) => values.rapportPersonnaliseId === e.rapportPersonnaliseId)
    ?.listeParametre?.map((e) => {
      return {
        rapportPersonnaliseParametreCode: e.rapportPersonnaliseParametreCode,
        value:
          values[e.rapportPersonnaliseParametreCode]?.toString() ??
          e.rapportPersonnaliseParametreValeurDefaut?.toString(),
      };
    });

  return {
    rapportPersonnaliseId: values.rapportPersonnaliseId,
    listeParametre: listeParametre,
  };
};

const ExecuteRapportPersonnaliseForm = ({
  listeRapportPersoWithParametre,
}: {
  listeRapportPersoWithParametre: RapportPersoWithParametreType[];
}) => {
  const { setFieldValue, values } = useFormikContext();

  const listeRapportPerso = listeRapportPersoWithParametre?.map((e) => ({
    id: e.rapportPersonnaliseId,
    libelle: e.rapportPersonnaliseLibelle,
  }));

  const rapportPersonnaliseCourant = listeRapportPersoWithParametre?.find(
    (e) => e.rapportPersonnaliseId === values.rapportPersonnaliseId,
  );

  return (
    <FormContainer>
      <Row>
        <SelectInput
          name={`rapportPersonnaliseId`}
          label="Rapport personnalisé"
          options={listeRapportPerso}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              `rapportPersonnaliseId`,
              listeRapportPerso?.find((r) => r.id === e.id).id,
            );
          }}
          defaultValue={listeRapportPerso?.find(
            (r) => r.id === values.rapportPersonnaliseId,
          )}
          required={true}
        />
      </Row>
      {rapportPersonnaliseCourant?.rapportPersonnaliseDescription && (
        <Row className="mt-3">
          <Col>
            <b>Description : </b>
            {rapportPersonnaliseCourant.rapportPersonnaliseDescription}
          </Col>
        </Row>
      )}
      <Row className="mt-3">
        {// Pour chacun des paramètres, on affiche le bon composant
        rapportPersonnaliseCourant?.listeParametre?.map((element) => {
          return buildComponent(element, values, setFieldValue);
        })}
      </Row>
      <SubmitFormButtons />
    </FormContainer>
  );
};

function buildComponent(
  element: RapportPersonnaliseParametreType,
  values: any,
  setFieldValue: (name: string, e: any) => void,
) {
  switch (
    TYPE_PARAMETRE_RAPPORT_PERSONNALISE[
      element.rapportPersonnaliseParametreType
    ]
  ) {
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.CHECKBOX_INPUT:
      return (
        <CheckBoxInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          checked={
            values[element.rapportPersonnaliseParametreCode] ??
            element.rapportPersonnaliseParametreValeurDefaut === "true"
          }
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.NUMBER_INPUT:
      return (
        <NumberInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          value={element.rapportPersonnaliseParametreValeurDefaut}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.TEXT_INPUT:
      return (
        <TextInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          value={element.rapportPersonnaliseParametreValeurDefaut}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.DATE_INPUT:
      return (
        <DateTimeInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          defaultValue={element.rapportPersonnaliseParametreValeurDefaut}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.SELECT_INPUT:
      return (
        <SelectInput
          name={element.rapportPersonnaliseParametreCode}
          label={element.rapportPersonnaliseParametreLibelle}
          required={element.rapportPersonnaliseParametreIsRequired}
          options={element.listeSelectInput}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              element.rapportPersonnaliseParametreCode,
              element.listeSelectInput?.find((r) => r.id === e.id).id,
            );
          }}
          defaultValue={element.listeSelectInput?.find(
            (r) => r.id === values[element.rapportPersonnaliseParametreCode],
          )}
          tooltipText={element.rapportPersonnaliseParametreDescription}
        />
      );
    default:
      return;
  }
}

export default ExecuteRapportPersonnalise;
