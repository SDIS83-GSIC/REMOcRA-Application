import { Button, Container, Table } from "react-bootstrap";
import { useState } from "react";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import {
  IconCriseRapportPersonnalise,
  IconExport,
} from "../../../components/Icon/Icon.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "../../RapportPersonnalise/ExecuteRapportPersonnalise.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import GenererForm from "../../../utils/buildDynamicForm.tsx";
import { downloadOutputFile } from "../../../utils/fonctionsUtils.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";

const ExecuteCriseRapportPersonnalise = ({
  onGeometrySelect,
  geometry,
}: {
  onGeometrySelect: (geometryType: string, geometryCode: string) => void;
  geometry: any;
}) => {
  const { data: criseReports } = useGet(url`/api/crise/rapports-personnalises`);
  const { success: successToast, error: errorToast } = useToastContext();
  const [valuesFormik, setValuesFormik] = useState<any>();

  const [tableau, setTableau] = useState<{
    headers: string[];
    values: any[];
    geometries: string[];
  }>({
    headers: [],
    values: [],
    geometries: [],
  });

  return (
    <Container>
      <PageTitle
        icon={<IconCriseRapportPersonnalise />}
        title="Rapport personnalisé"
        displayReturnButton={false}
      />

      <MyFormik
        initialValues={getInitialValues()}
        validationSchema={validationSchema}
        isPost={false}
        successToastMessage="La requête a bien été exécutée"
        submitUrl={`/api/rapport-personnalise/generer`}
        prepareVariables={(values) => {
          const geometryValues = Object.keys(geometry).reduce(
            (acc: any, key: any) => {
              acc[key] = geometry[key];
              return acc;
            },
            {},
          );

          const finalValues = prepareVariables({
            ...values,
            ...geometryValues,
          });
          setValuesFormik(finalValues);

          return finalValues;
        }}
        onSubmit={(e) => {
          setTableau(e);
        }}
      >
        <GenererForm
          listeIdLibelleDescription={criseReports}
          contexteLibelle="Executer un rapport personnalisé"
          url="/api/rapport-personnalise/parametres/"
          onGeometrySelect={onGeometrySelect}
        />
      </MyFormik>

      {tableau && tableau.headers.length > 0 && tableau.values.length > 0 ? (
        <>
          <Button
            onClick={() =>
              downloadOutputFile(
                "/api/rapport-personnalise/export-data",
                JSON.stringify({
                  rapportPersonnaliseId: valuesFormik?.rapportPersonnaliseId,
                  listeParametre: valuesFormik?.listeParametre,
                }),
                "rapport-personnalise.csv",
                "Export terminé",
                successToast,
                errorToast,
              )
            }
          >
            Exporter les données
            <IconExport />
          </Button>

          <Table bordered striped className="resizable-table">
            <thead>
              <tr>
                {tableau?.headers?.map((e, index) => (
                  <th key={index} title={e}>
                    {e}
                    <div className="column-resizer" />
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {tableau?.values?.map((ligne, index) => {
                return (
                  <tr key={index} className={"fw-normal"}>
                    {ligne.map((e: any, key: number) => (
                      <td key={key} title={e?.toString()}>
                        {e?.toString()}
                      </td>
                    ))}
                  </tr>
                );
              })}
            </tbody>
          </Table>
        </>
      ) : (
        <p>Aucun résultat</p> // Message lorsque tableau est vide
      )}
    </Container>
  );
};

export default ExecuteCriseRapportPersonnalise;
