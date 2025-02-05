import { Button, FloatingLabel, Form } from "react-bootstrap";
import { useState } from "react";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";
import { AdresseElementEntity } from "../../Entities/AdresseElementEntity.tsx";

const CreateElementAdresse = ({
  onClick,
  geometryString,
  srid,
  sousTypeElement,
}: {
  layer: any;
  onClick: (element: AdresseElementEntity) => void;
  geometryString: string;
  srid: string;
  sousTypeElement: string;
}) => {
  const anomalies = useGet(url`/api/adresses/type-anomalie`)?.data;
  const [adresseElement, setAdresseElement] = useState<AdresseElementEntity>({
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
                label={anomalie.adresseTypeAnomalieLibelle}
                id={"checkbox" + anomalie.adresseTypeAnomalieCode}
                onChange={(event) => {
                  const checked = event.currentTarget.checked;
                  if (checked) {
                    const tableauAnomalie = [
                      ...adresseElement.anomalies,
                      anomalie.adresseTypeAnomalieCode,
                    ];
                    setAdresseElement((data) => ({
                      ...data,
                      anomalies: tableauAnomalie.filter(
                        (item, pos) => tableauAnomalie.indexOf(item) === pos,
                      ),
                    }));
                  } else {
                    setAdresseElement((data) => ({
                      ...data,
                      anomalies: data.anomalies.filter(
                        (e) => e !== anomalie.adresseTypeAnomalieCode,
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
              setAdresseElement((data) => ({
                ...data,
                description: event.target.value,
              }));
            }}
          />
        </FloatingLabel>
        <Button onClick={() => onClick(adresseElement)}>Valider</Button>
      </Form>
    )
  );
};

export default CreateElementAdresse;
