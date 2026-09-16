import { useState } from "react";
import { Button, Container, Table } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import {
  IconCriseRapportPersonnalise,
  IconExport,
} from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import GenererForm, {
  DynamicFormParametreFront,
} from "../../../utils/buildDynamicForm.tsx";
import { downloadOutputFile } from "../../../utils/fonctionsUtils.tsx";
import {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "../../RapportPersonnalise/ExecuteRapportPersonnalise.tsx";

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
  const [listeParametres, setListeParametres] = useState<
    DynamicFormParametreFront[]
  >([]);

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
          const finalValues = prepareVariables(values, listeParametres);

          // Transformer la géométrie en paramètres
          if (geometry && Object.keys(geometry).length > 0) {
            Object.entries(geometry).forEach(([key, value]) => {
              // Chercher si ce paramètre existe déjà
              const existingIndex = finalValues.listeParametre.findIndex(
                (p) => p.rapportPersonnaliseParametreCode === key,
              );

              if (existingIndex !== -1) {
                // Remplacer la valeur existante
                finalValues.listeParametre[existingIndex].value = value;
              } else {
                // Ajouter le nouveau paramètre
                finalValues.listeParametre.push({
                  rapportPersonnaliseParametreCode: key,
                  value: value,
                });
              }
            });
          }

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
          onParametresChange={setListeParametres}
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
