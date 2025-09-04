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
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CustomLinkButton from "../../../components/Button/CustomLinkButton.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconSee, IconUtilisateurs } from "../../../components/Icon/Icon.tsx";
import { hasDroit } from "../../../droits.tsx";
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import DISPONIBILITE_PEI from "../../../enums/DisponibiliteEnum.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import TYPE_RESUME_ELEMENT from "../../../enums/TypeResumeElementEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime, { formatDate } from "../../../utils/formatDateUtils.tsx";
import PARAMETRE from "../../../enums/ParametreEnum.tsx";

const FicheResume = ({
  peiId,
  titre = "",
}: {
  peiId: string;
  titre?: string;
}) => {
  const elementFicheResumeState = useGet(url`/api/fiche-resume/${peiId}`);
  const { user } = useAppContext();

  if (!elementFicheResumeState.isResolved) {
    return;
  }

  const mapColonneRow = Object.groupBy(
    elementFicheResumeState?.data,
    (item: { colonne: number }) => item.colonne,
  );

  return (
    <>
      {titre.trim().length > 0 && <h1>{titre}</h1>}
      <Container>
        <Row>
          {Object.entries(mapColonneRow).map(([key, values]) => (
            <Col key={key} className={"mx-0 px-0"}>
              {Array.from(values ?? []).map((e: any, key) => {
                return (
                  (e.type !== TYPE_RESUME_ELEMENT.GESTIONNAIRE ||
                    e.data.gestionnaireId) && (
                    <Row className="mx-0 my-3" key={key}>
                      <ElementResume
                        titre={e.titre}
                        value={e.data}
                        typeResumeElement={e.type}
                        user={user!}
                      />
                    </Row>
                  )
                );
              })}
            </Col>
          ))}
        </Row>
        <Row>
          <HistoriqueDebitPression pibiId={peiId} />
        </Row>
        <VoirHistoriquePei peiId={peiId} user={user!} />
      </Container>
    </>
  );
};

const ElementResume = ({
  titre,
  value,
  typeResumeElement,
  user,
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
          ) : typeResumeElement === TYPE_RESUME_ELEMENT.GESTIONNAIRE ? (
            value.gestionnaireId != null && (
              <ElementResumeGestionnaire value={value} user={user!} />
            )
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

const ElementResumeDisponibilite = ({
  value,
}: {
  value: {
    disponibilite: keyof typeof DISPONIBILITE_PEI;
    hasIndispoTemp: boolean;
  };
}) => {
  let libelleNonConforme: string = "";

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.PEI_LIBELLE_NON_CONFORME]),
    }}`,
  );
  if (listeParametre.isResolved) {
    libelleNonConforme =
      listeParametre?.data?.[
        PARAMETRE.PEI_LIBELLE_NON_CONFORME
      ].parametreValeur.toUpperCase();
  }

  return DISPONIBILITE_PEI[value.disponibilite] ===
    DISPONIBILITE_PEI.DISPONIBLE ? (
    <p>
      <span className="text-white bg-success rounded p-2 text-nowrap">
        {" "}
        OUI{" "}
      </span>
    </p>
  ) : DISPONIBILITE_PEI[value.disponibilite] ===
    DISPONIBILITE_PEI.NON_CONFORME ? (
    <p>
      <span className="text-white bg-warning rounded p-2 text-nowrap">
        {libelleNonConforme}
      </span>
    </p>
  ) : (
    <p>
      <span className="text-white bg-danger rounded p-2 text-nowrap">
        NON {value.hasIndispoTemp ? "- Indisponibilité temporaire " : ""}
      </span>
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
    <>
      {value.map((e, key) => (
        <Row
          key={key}
          className={classNames(
            e.valIndispoTerrestre === 5 && "fw-bold",
            e.valIndispoHbe === 5 && "text-decoration-underline",
          )}
        >
          {e.anomalieLibelle}
        </Row>
      ))}{" "}
    </>
  ) : (
    "Aucune anomalie pour ce PEI."
  );
};

const ElementResumeGestionnaire = ({
  value,
  user,
}: {
  value: {
    gestionnaireId: string;
    gestionnaireLibelle: string;
    siteLibelle: string;
  };
  user: UtilisateurEntity;
}) => (
  <>
    <div>Gestionnaire : {value.gestionnaireLibelle}</div>
    {value.siteLibelle && <div>Site : {value.siteLibelle}</div>}
    {(hasDroit(user, TYPE_DROIT.GEST_SITE_R) ||
      hasDroit(user, TYPE_DROIT.ADMIN_DROITS)) && (
      <CustomLinkButton
        pathname={URLS.LIST_CONTACT(value.gestionnaireId, "gestionnaire")}
        variant="link"
      >
        <IconSee />{" "}
        <span style={{ fontSize: ".85rem" }}>Voir les contacts</span>
      </CustomLinkButton>
    )}
  </>
);

type ElementResumeType = {
  titre: string;
  value: any;
  typeResumeElement: TYPE_RESUME_ELEMENT;
  user: UtilisateurEntity;
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
                {data.data.map((e: any, index: number) => {
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

const VoirHistoriquePei = ({
  peiId,
  user,
}: {
  peiId: string;
  user: UtilisateurEntity;
}) => {
  const searchParams = new URLSearchParams({
    typeObjet: "PEI",
    objetId: peiId,
  });

  return (
    hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E) && (
      <Row className="mt-2">
        <h4>Historique du PEI</h4>
        <Row>
          <Col>
            <p className="text-muted">
              Le bouton ci-dessous vous permet de consulter l&apos;historique
              complet des opérations effectuées sur ce Point d&apos;Eau Incendie
              (PEI). Cette section vous donne accès à :
            </p>
            <ul className="text-muted">
              <li>
                L&apos;ensemble des modifications apportées au PEI au fil du
                temps
              </li>
              <li>Les informations sur les auteurs de chaque changement</li>
              <li>Les dates précises de chaque intervention</li>
            </ul>
          </Col>
        </Row>
        <Row>
          <Col className="text-center mt-2">
            <CustomLinkButton
              pathname={URLS.HISTORIQUE_OPERATIONS}
              search={searchParams.toString()}
              variant="primary"
            >
              <IconUtilisateurs /> Voir l&apos;historique des opérations sur ce
              PEI
            </CustomLinkButton>
          </Col>
        </Row>
      </Row>
    )
  );
};
