import classNames from "classnames";
import { Col, Container, Row, Table } from "react-bootstrap";
import {
  Bar,
  BarChart,
  CartesianGrid,
  Legend,
  Rectangle,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import DISPONIBILITE_PEI from "../../../enums/DisponibiliteEnum.tsx";
import TYPE_RESUME_ELEMENT from "../../../enums/TypeResumeElementEnum.tsx";
import url from "../../../module/fetch.tsx";
import formatDateTime, { formatDate } from "../../../utils/formatDateUtils.tsx";

const FicheResume = ({
  peiId,
  titre = "",
}: {
  peiId: string;
  titre?: string;
}) => {
  const elementFicheResumeState = useGet(url`/api/fiche-resume/${peiId}`);

  if (!elementFicheResumeState.isResolved) {
    return;
  }

  const mapColonneRow = Object.groupBy(
    elementFicheResumeState?.data,
    ({ colonne }) => colonne,
  );

  return (
    <>
      {titre.trim().length > 0 && <h1>{titre}</h1>}
      <Container>
        <Row>
          {Object.entries(mapColonneRow).map(([key, values]) => (
            <Col key={key} className={"mx-0 px-0"}>
              {Array.from(values).map((e, key) => {
                return (
                  <Row className="mx-0 my-3" key={key}>
                    <ElementResume
                      titre={e.titre}
                      value={e.data}
                      typeResumeElement={e.type}
                    />
                  </Row>
                );
              })}
            </Col>
          ))}
        </Row>
        <Row>
          <HistoriqueDebitPression pibiId={peiId} />
        </Row>
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
    <div className={"mx-0 px-0"}>
      <div className="h2">{titre}</div>
      <Row>
        <Col xs={"auto"}>
          {typeResumeElement === TYPE_RESUME_ELEMENT.DISPONIBILITE ? (
            <ElementResumeDisponibilite value={value} />
          ) : typeResumeElement === TYPE_RESUME_ELEMENT.ANOMALIES ? (
            <ElementResumeAnomalie value={value} />
          ) : (
            value?.split("\n").map((ligne: string, key: number) => {
              return <div key={key}>{ligne}</div>;
            })
          )}
        </Col>
      </Row>
    </div>
  );
};

const ElementResumeDisponibilite = ({ value }: { value: string }) => {
  return DISPONIBILITE_PEI[value] === DISPONIBILITE_PEI.DISPONIBLE ? (
    <p>
      <span className="text-white bg-success rounded p-2"> OUI </span>
    </p>
  ) : DISPONIBILITE_PEI[value] === DISPONIBILITE_PEI.NON_CONFORME ? (
    <p>
      <span className="text-white bg-warning rounded p-2">NON CONFORME</span>
    </p>
  ) : (
    <p>
      <span className="text-white bg-danger rounded p-2">NON</span>
    </p>
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
  return value.length > 0 ? (
    <Row>
      {value.map((e, key) => (
        <Col
          key={key}
          className={classNames(
            e.valIndispoTerrestre === 5 && "fw-bold",
            e.valIndispoHbe === 5 && "text-decoration-underline",
          )}
        >
          {e.anomalieLibelle}
        </Col>
      ))}{" "}
    </Row>
  ) : (
    "Aucune anomalie pour ce PEI."
  );
};

type ElementResumeType = {
  titre: string;
  value: any;
  typeResumeElement: TYPE_RESUME_ELEMENT;
};

export default FicheResume;

const HistoriqueDebitPression = ({ pibiId }: { pibiId: string }) => {
  const { data } = useGet(url`/api/pibi/historique/` + pibiId);

  const primary = "#00293E";

  return (
    data &&
    data.data.length !== 0 && (
      <>
        <h4>Historique des mesures</h4>
        <Row>
          <Col xs={7} className="mx-auto">
            <ResponsiveContainer aspect={1.5}>
              <BarChart
                width={500}
                height={300}
                data={data.data}
                margin={{
                  top: 5,
                  right: 30,
                  left: 20,
                  bottom: 5,
                }}
              >
                <CartesianGrid strokeDasharray="3 3" />
                <XAxis dataKey="visiteDate" tickFormatter={formatDate} />
                <YAxis />
                <Tooltip />
                <Legend />
                <Bar
                  name={"Débit"}
                  dataKey="debit"
                  fill={primary}
                  activeBar={<Rectangle fill={primary} stroke="dark" />}
                />
              </BarChart>
            </ResponsiveContainer>
          </Col>
          <Col xs={12}>
            <Table bordered striped>
              <thead>
                <tr>
                  <th>Date de contrôle</th>
                  <th>Débit (m³/h)</th>
                  <th>Pression (bar)</th>
                  <th>Pression dynamique (bar)</th>
                </tr>
              </thead>
              <tbody>
                {data.data.map((e, index) => {
                  return (
                    <tr key={index}>
                      <td>{formatDateTime(e.visiteDate)}</td>
                      <td>{e.debit}</td>
                      <td>{e.pression}</td>
                      <td>{e.pressionDyn}</td>
                    </tr>
                  );
                })}
              </tbody>
              <tfoot>
                <tr>
                  <td className="fw-bold">Moyenne</td>
                  <td>{data.moyenneDebit}</td>
                  <td>{data.moyennePression}</td>
                  <td>{data.moyennePressionDyn}</td>
                </tr>
              </tfoot>
            </Table>
          </Col>
        </Row>
      </>
    )
  );
};
