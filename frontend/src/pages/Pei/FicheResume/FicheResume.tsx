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
import PARAMETRE from "../../../enums/ParametreEnum.tsx";
import TYPE_RESUME_ELEMENT from "../../../enums/TypeResumeElementEnum.tsx";
import { TYPE_ROUTE_HISTORIQUE_PEI } from "../../../enums/TypeRouteHistoriquePei.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime, { formatDate } from "../../../utils/formatDateUtils.tsx";

const FicheResume = ({
  peiId,
  peiNumeroComplet,
  titre = "",
}: {
  peiId: string;
  peiNumeroComplet?: string;
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
        {(hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E) ||
          hasDroit(user, TYPE_DROIT.RAPPORT_PERSONNALISE_E)) && (
          <VoirHistoriquePei
            peiId={peiId}
            peiNumeroComplet={peiNumeroComplet}
            user={user!}
          />
        )}
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
    list: DataIndispoTemp[];
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
    <>
      <p>
        <span className="text-white bg-danger rounded p-2 text-nowrap">
          NON {value.hasIndispoTemp ? "- Indisponibilité temporaire" : ""}
        </span>
      </p>

      {value.hasIndispoTemp && value.list.length > 0 && (
        <ul className="mt-2 list-disc list-inside text-sm">
          {value.list.map((indispo, index) => (
            <li key={index} className="mb-2">
              <div>
                <strong>
                  date début : {indispo.startDate}
                  {indispo.endDate && (
                    <>
                      <br />
                      date fin : {indispo.endDate}
                    </>
                  )}
                </strong>
              </div>
              {indispo.motif && (
                <div className="text-red-800 italic mt-1 md:mt-0 md:ml-4">
                  motif : {indispo.motif}
                </div>
              )}
            </li>
          ))}
        </ul>
      )}
    </>
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

type DataIndispoTemp = {
  startDate: string;
  endDate: string | null;
  motif: string;
};

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

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([
        PARAMETRE.VALEUR_HAUTE_MINIMALE_HISTOGRAMME,
      ]),
    }}`,
  );

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
                <YAxis
                  domain={[
                    0,
                    (dataMax: number) =>
                      Math.max(
                        listeParametre?.data?.[
                          PARAMETRE.VALEUR_HAUTE_MINIMALE_HISTOGRAMME
                        ]?.parametreValeur ?? dataMax,
                        dataMax,
                      ),
                  ]}
                />
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
  user,
  peiId,
  peiNumeroComplet,
}: {
  user: UtilisateurEntity;
  peiId: string;
  peiNumeroComplet?: string;
}) => {
  const parametreRouteHistorique = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.PEI_ROUTE_HISTORIQUE]),
    }}`,
  );
  const routeHistorique =
    parametreRouteHistorique?.data?.[PARAMETRE.PEI_ROUTE_HISTORIQUE]
      ?.parametreValeur;

  return (
    ((routeHistorique === TYPE_ROUTE_HISTORIQUE_PEI.HISTORIQUE_OPERATIONS &&
      hasDroit(user, TYPE_DROIT.OPERATIONS_DIVERSES_E)) ||
      (routeHistorique === TYPE_ROUTE_HISTORIQUE_PEI.RAPPORT_PERSO &&
        hasDroit(user, TYPE_DROIT.RAPPORT_PERSONNALISE_E))) && (
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
            {routeHistorique ===
            TYPE_ROUTE_HISTORIQUE_PEI.HISTORIQUE_OPERATIONS ? (
              <HistoriqueOperationsPei peiId={peiId} />
            ) : (
              <RapportPersonnaliseHistoriquePei
                peiNumeroComplet={peiNumeroComplet}
              />
            )}
          </Col>
        </Row>
      </Row>
    )
  );
};

const RapportPersonnaliseHistoriquePei = ({
  peiNumeroComplet,
}: {
  peiNumeroComplet?: string;
}) => {
  const rapportHistoriqueState = useGet(
    url`/api/rapport-personnalise/get-id-rapport-historique-pei`,
  );
  const rapportPersonnaliseId = rapportHistoriqueState?.data;

  return (
    <CustomLinkButton
      pathname={`/rapport-personnalise/execute`}
      state={{
        rapportPersonnaliseId: rapportPersonnaliseId,
        peiNumeroComplet: peiNumeroComplet,
      }}
      variant="primary"
    >
      <IconUtilisateurs /> Voir l&apos;historique personnalisé du PEI
    </CustomLinkButton>
  );
};

const HistoriqueOperationsPei = ({ peiId }: { peiId: string }) => {
  const searchParams = new URLSearchParams({
    typeObjet: "PEI",
    objetId: peiId,
  });

  return (
    <CustomLinkButton
      pathname={URLS.HISTORIQUE_OPERATIONS}
      search={searchParams.toString()}
      variant="primary"
    >
      <IconUtilisateurs /> Voir l&apos;historique des opérations sur ce PEI
    </CustomLinkButton>
  );
};
