import classNames from "classnames";
import { Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import DISPONIBILITE_PEI from "../../../enums/DisponibiliteEnum.tsx";
import TYPE_RESUME_ELEMENT from "../../../enums/TypeResumeElementEnum.tsx";
import url from "../../../module/fetch.tsx";

const FicheResume = () => {
  const { peiId } = useParams();

  const elementFicheResumeState = useGet(url`/api/fiche-resume/` + peiId);

  if (!elementFicheResumeState.isResolved) {
    return;
  }

  const mapLigneRow = Object.groupBy(
    elementFicheResumeState?.data,
    ({ ligne }) => ligne,
  );

  return (
    <>
      <Container>
        <Col>
          {Object.entries(mapLigneRow).map(([key, values]) => (
            <Row key={key}>
              {Array.from(values).map((e, key) => {
                return (
                  <Col className="m-3" key={key}>
                    <ElementResume
                      titre={e.titre}
                      value={e.data}
                      typeResumeElement={e.type}
                    />
                  </Col>
                );
              })}
            </Row>
          ))}
        </Col>
      </Container>
    </>
  );
};

const ElementResume = ({
  titre,
  value,
  typeResumeElement,
}: ElementResumeType) => {
  return (
    <>
      <h1>{titre}</h1>
      <div>
        {typeResumeElement === TYPE_RESUME_ELEMENT.DISPONIBILITE ? (
          <ElementResumeDisponibilite value={value} />
        ) : typeResumeElement === TYPE_RESUME_ELEMENT.ANOMALIES ? (
          <ElementResumeAnomalie value={value} />
        ) : (
          value?.split("\n").map((ligne: string, key: number) => {
            return <div key={key}>{ligne}</div>;
          })
        )}
      </div>
    </>
  );
};

const ElementResumeDisponibilite = ({ value }: { value: string }) => {
  return DISPONIBILITE_PEI[value] === DISPONIBILITE_PEI.DISPONIBLE ? (
    <span className="text-white bg-success rounded p-2"> OUI </span>
  ) : DISPONIBILITE_PEI[value] === DISPONIBILITE_PEI.NON_CONFORME ? (
    <span className="text-white bg-warning rounded p-2">NON CONFORME</span>
  ) : (
    <span className="text-white bg-danger rounded p-2">NON</span>
  );
};

const ElementResumeAnomalie = ({
  value,
}: {
  value: {
    anomalieLibelle: string;
    valIndispoTerrestre: number;
    valIndispoHbe: number;
  }[];
}) => {
  return value.length > 0
    ? value.map((e, key) => (
        <span
          key={key}
          className={classNames(
            e.valIndispoTerrestre === 5 && "fw-bold",
            e.valIndispoHbe === 5 && "text-decoration-underline",
          )}
        >
          {e.anomalieLibelle}
        </span>
      ))
    : "Aucune anomalie pour ce PEI.";
};

type ElementResumeType = {
  titre: string;
  value: any;
  typeResumeElement: TYPE_RESUME_ELEMENT;
};

export default FicheResume;
