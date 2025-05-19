import { useMemo, useRef, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { URLS } from "../../../routes.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";
import { SelectDateTimeInput } from "../../../components/Filter/SelectDateTimeInput.tsx";
import CheckBoxInput from "../../../components/Filter/SelectCheckBoxInput.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconExport } from "../../../components/Icon/Icon.tsx";

export type ExportParams = {
  dateDebExtraction: Date;
  dateFinExtraction: Date;
  hasMessage: boolean;
  hasDoc: boolean;
};

const ExportCrise = () => {
  const { criseId } = useParams();
  const crise = useGet(url`/api/crise/${criseId}`);
  const [exportParams, setExportParams] = useState<ExportParams>({
    dateDebExtraction: new Date(),
    dateFinExtraction: new Date(),
    hasMessage: true,
    hasDoc: true,
  });
  const loop = useRef(0);

  useMemo(() => {
    if (crise.isResolved && loop.current === 0) {
      loop.current += 1;
      if (crise.data?.criseDateDebut && crise.data?.criseDateFin) {
        setExportParams({
          ...exportParams,
          dateDebExtraction: new Date(crise.data?.criseDateDebut),
          dateFinExtraction: new Date(crise.data?.criseDateFin),
        });
      }
    }
  }, [crise, loop, exportParams]);

  return (
    <Container>
      <PageTitle
        icon={<IconExport />}
        title={"Export des données de la crise"}
      />
      <Container>
        <Row>
          <h1>Période d&apos;extraction</h1>
        </Row>
        <Row className="my-3" xs={3}>
          <Col className="text-center mx-auto">
            <Row>
              <SelectDateTimeInput
                name="dateDebExtraction"
                label="Extraire les informations entre le"
                required={true}
                value={formatDateTimeForDateTimeInput(
                  exportParams.dateDebExtraction,
                )}
                onChange={(e) =>
                  setExportParams({
                    ...exportParams,
                    dateDebExtraction: e.value,
                  })
                }
              />
            </Row>
            <Row>
              <SelectDateTimeInput
                name="dateFinExtraction"
                label="et le"
                required={true}
                value={formatDateTimeForDateTimeInput(
                  exportParams.dateFinExtraction,
                )}
                onChange={(e) =>
                  setExportParams({
                    ...exportParams,
                    dateFinExtraction: e.value,
                  })
                }
              />
            </Row>
          </Col>
        </Row>
        <Row className="my-3" xs={3}>
          <Col className="mx-auto">
            <Row>
              <CheckBoxInput
                name="hasMessage"
                label="Exporter les messages associés aux évènements"
                checked={exportParams.hasMessage}
                onChange={(e) =>
                  setExportParams({ ...exportParams, hasMessage: e.value })
                }
              />
            </Row>
            <Row>
              <CheckBoxInput
                name="hasDoc"
                label="Exporter les documents associés aux évènements"
                checked={exportParams.hasDoc}
                onChange={(e) =>
                  setExportParams({ ...exportParams, hasDoc: e.value })
                }
              />
            </Row>
          </Col>
        </Row>
        <Row xs={4} className="mx-auto">
          <Button
            className="mx-auto"
            variant="primary"
            onClick={() => {
              const exp = {
                dateDebExtraction: new Date(
                  exportParams?.dateDebExtraction,
                ).toISOString(),
                dateFinExtraction: new Date(
                  exportParams?.dateFinExtraction,
                ).toISOString(),
                hasMessage: exportParams?.hasMessage,
                hasDoc: exportParams?.hasDoc,
              };
              window.open(url`/api/crise/${criseId}/export?${exp}`, "_blank");
            }}
            href={URLS.LIST_CRISES}
          >
            Valider
          </Button>
        </Row>
      </Container>
    </Container>
  );
};

export default ExportCrise;
