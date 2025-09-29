import { Button, FloatingLabel, Form } from "react-bootstrap";
import { useState } from "react";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import { SignalementElementEntity } from "../../Entities/SignalementElementEntity.tsx";

const CreateElementSignalement = ({
  onClick,
  geometryString,
  srid,
  sousTypeElement,
}: {
  layer: any;
  onClick: (element: SignalementElementEntity) => void;
  geometryString: string;
  srid: string;
  sousTypeElement: string;
}) => {
  const anomalies = useGet(url`/api/signalements/type-anomalie`)?.data;
  const [signalementElement, setSignalementElement] =
    useState<SignalementElementEntity>({
      geometryString: geometryString,
      anomalies: [],
      description: null,
      srid: srid,
      sousType: sousTypeElement,
    });
  return (
    anomalies && (
      <Form>
        {anomalies.map((anomalie) => {
          return (
            <>
              <Form.Check
                type="switch"
                label={anomalie.signalementTypeAnomalieLibelle}
                id={"checkbox" + anomalie.signalementTypeAnomalieCode}
                onChange={(event) => {
                  const checked = event.currentTarget.checked;
                  if (checked) {
                    const tableauAnomalie = [
                      ...signalementElement.anomalies,
                      anomalie.signalementTypeAnomalieCode,
                    ];
                    setSignalementElement((data) => ({
                      ...data,
                      anomalies: tableauAnomalie.filter(
                        (item, pos) => tableauAnomalie.indexOf(item) === pos,
                      ),
                    }));
                  } else {
                    setSignalementElement((data) => ({
                      ...data,
                      anomalies: data.anomalies.filter(
                        (e) => e !== anomalie.signalementTypeAnomalieCode,
                      ),
                    }));
                  }
                }}
              />
            </>
          );
        })}
        <FloatingLabel
          controlId={"textarea"}
          label="Description"
          className="mb-3 text-muted"
        >
          <Form.Control
            as="textarea"
            placeholder="Description"
            id={"textarea"}
            style={{ height: "100px" }}
            onChange={(event) => {
              setSignalementElement((data) => ({
                ...data,
                description: event.target.value,
              }));
            }}
          />
        </FloatingLabel>
        <Button onClick={() => onClick(signalementElement)}>Valider</Button>
      </Form>
    )
  );
};

export default CreateElementSignalement;
