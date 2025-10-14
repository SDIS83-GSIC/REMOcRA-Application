import { useRef, useState, useEffect } from "react";
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
import "./ExecuteRapportPersonnalise.css";

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
  const [valuesFormik, setValuesFormik] = useState<any>();
  const [isDownload, setIsDownload] = useState(false);
  const [columnWidths, setColumnWidths] = useState<{ [key: number]: number }>(
    {},
  );
  const tableRef = useRef<HTMLTableElement>(null);

  const table = tableRef.current;

  // Hook pour gérer le redimensionnement des colonnes
  useEffect(() => {
    if (!tableau || !tableau.headers) {
      return;
    }
    let currentColumnIndex = -1;
    let startX = 0;
    let startWidth = 0;

    const handleMouseMove = (e: MouseEvent) => {
      e.preventDefault();
      if (currentColumnIndex === -1) {
        return;
      }

      const newWidth = Math.max(50, startWidth + e.clientX - startX);
      setColumnWidths((prev) => ({ ...prev, [currentColumnIndex]: newWidth }));
    };

    const handleMouseUp = () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
      document.body.classList.remove("resizing");
      currentColumnIndex = -1;
    };

    const handleMouseDown = (e: MouseEvent, columnIndex: number) => {
      e.preventDefault();
      e.stopPropagation();

      const th = (e.target as HTMLElement).closest("th");
      if (!th) {
        return;
      }

      currentColumnIndex = columnIndex;
      startX = e.clientX;
      startWidth = th.offsetWidth;

      document.addEventListener("mousemove", handleMouseMove);
      document.addEventListener("mouseup", handleMouseUp);
      document.body.classList.add("resizing");
    };

    if (table) {
      const resizers = table.querySelectorAll(".column-resizer");
      resizers.forEach((resizer, index) => {
        const handler = (e: Event) => handleMouseDown(e as MouseEvent, index);
        resizer.addEventListener("mousedown", handler);

        // Stocker le handler pour le nettoyage
        (resizer as any)._handler = handler;
      });
    }

    return () => {
      document.removeEventListener("mousemove", handleMouseMove);
      document.removeEventListener("mouseup", handleMouseUp);
      document.body.classList.remove("resizing");

      // Nettoyer les gestionnaires d'événements
      if (table) {
        const resizers = table.querySelectorAll(".column-resizer");
        resizers.forEach((resizer) => {
          const handler = (resizer as any)._handler;
          if (handler) {
            resizer.removeEventListener("mousedown", handler);
          }
        });
      }
    };
  }, [tableau, table]);

  const getColumnStyle = (index: number) => {
    const width = columnWidths[index];
    if (width) {
      return { width: `${width}px`, minWidth: `${width}px` };
    }

    // Calculer la largeur basée sur la longueur du header
    const headerText = tableau?.headers?.[index] || "";
    const estimatedWidth = Math.max(100, headerText.length * 8 + 40); // 8px par caractère + padding

    return { minWidth: `${estimatedWidth}px` };
  };

  const getTableStyle = () => {
    const totalWidth = Object.values(columnWidths).reduce(
      (sum, width) => sum + width,
      0,
    );

    // Calculer la largeur des colonnes non redimensionnées basée sur leurs headers
    let defaultTotalWidth = 0;
    if (tableau?.headers) {
      tableau.headers.forEach((header, index) => {
        if (!(index in columnWidths)) {
          const estimatedWidth = Math.max(100, header.length * 8 + 40);
          defaultTotalWidth += estimatedWidth;
        }
      });
    }

    if (totalWidth > 0 || defaultTotalWidth > 0) {
      return { width: `${totalWidth + defaultTotalWidth}px` };
    }
    return { minWidth: "100%" };
  };

  return (
    <Container fluid style={{ maxWidth: "95vw" }}>
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
                "Export terminé",
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
                  successToast("Export terminé");
                },
                () => {
                  setIsDownload(false);
                  return errorToast("Erreur lors de l'export");
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
            successToastMessage="La requête a bien été exécutée"
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
              // Réinitialiser les largeurs de colonnes pour le nouveau tableau
              setColumnWidths({});
            }}
          >
            <GenererForm
              listeWithParametre={listeRapportPersoWithParametre}
              contexteLibelle="Rapport personnalisé"
            />
          </MyFormik>
        </Col>
        <Col xs={12} lg={9}>
          <Tabs activeKey={activeTab} onSelect={(k) => k && setActiveTab(k)}>
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
                <div>
                  <Table
                    bordered
                    striped
                    ref={tableRef}
                    className="resizable-table"
                    style={getTableStyle()}
                  >
                    <thead>
                      <tr>
                        {tableau?.headers?.map((e, index) => (
                          <th
                            key={index}
                            style={getColumnStyle(index)}
                            title={e}
                          >
                            {e}
                            <div className="column-resizer" />
                          </th>
                        ))}
                      </tr>
                    </thead>
                    <tbody>
                      {tableau?.values
                        ?.slice(offset, offset + LIMIT)
                        ?.map((ligne, index) => {
                          return (
                            <tr key={index} className={"fw-normal"}>
                              {ligne.map((e: any, key: number) => (
                                <td
                                  key={key}
                                  style={getColumnStyle(key)}
                                  title={e?.toString()}
                                >
                                  {e?.toString()}
                                </td>
                              ))}
                            </tr>
                          );
                        })}
                    </tbody>
                  </Table>
                  {tableau?.values && (
                    <PaginationFront
                      values={tableau?.values}
                      setOffset={setOffset}
                    />
                  )}
                </div>
              )}
            </Tab>
            <Tab
              eventKey="map"
              title="Carte"
              disabled={
                tableau?.geometries == null || tableau.geometries.length === 0
              }
            >
              <MapRapportPersonnalise wkt={tableau?.geometries || []} />
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
  values: any,
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
