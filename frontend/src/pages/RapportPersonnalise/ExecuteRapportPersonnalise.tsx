import { useState } from "react";
import { Button, Col, Container, Row, Tab, Table, Tabs } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconExport, IconList } from "../../components/Icon/Icon.tsx";
import MapRapportPersonnalise from "../../components/Map/MapRapportPersonnalise/MapRapportPersonnalise.tsx";
import PaginationFront, {
  LIMIT,
} from "../../components/PaginationFront/PaginationFront.tsx";
import url from "../../module/fetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { requiredString } from "../../module/validators.tsx";
import {
  DynamicFormWithParametre,
  default as GenererForm,
} from "../../utils/buildDynamicForm.tsx";
import { downloadOutputFile } from "../../utils/fonctionsUtils.tsx";

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
  const [isDownload, setIsDownload] = useState(false);

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
            onClick={() => {
              setIsDownload(true);
              downloadOutputFile(
                "/api/rapport-personnalise/export-shp",
                JSON.stringify({
                  rapportPersonnaliseId: valuesFormik?.rapportPersonnaliseId,
                  listeParametre: valuesFormik?.listeParametre,
                }),
                "rapport-personnalise.zip",
                "",
                () => {
                  setIsDownload(false);
                  successToast("Import terminé");
                },
                () => {
                  setIsDownload(false);
                  return errorToast();
                },
              );
            }}
            disabled={
              valuesFormik == null ||
              tableau == null ||
              tableau?.geometries == null ||
              tableau.geometries.length === 0 ||
              isDownload
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
            <GenererForm
              listeWithParametre={listeRapportPersoWithParametre}
              contexteLibelle="Rapport personnalisé"
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
  dynamicFormId: null,
});

export const validationSchema = object({
  dynamicFormId: requiredString,
});
export const prepareVariables = (
  values,
  listeRapportPersoWithParametre: DynamicFormWithParametre[],
) => {
  // on va récupérer que les paramètres du rapport personnalisé
  const listeParametre = listeRapportPersoWithParametre
    .find((e) => values.dynamicFormId === e.dynamicFormId)
    ?.listeParametre?.map((e) => {
      return {
        rapportPersonnaliseParametreCode: e.dynamicFormParametreCode,
        value:
          values[e.dynamicFormParametreCode]?.toString() ??
          e.dynamicFormParametreValeurDefaut?.toString(),
      };
    });

  return {
    rapportPersonnaliseId: values.dynamicFormId,
    listeParametre: listeParametre,
  };
};

export default ExecuteRapportPersonnalise;
