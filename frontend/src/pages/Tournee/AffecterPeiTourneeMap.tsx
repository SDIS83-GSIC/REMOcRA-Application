import { useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import ReactSelect from "react-select";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  IconCreate,
  IconEdit,
  IconNextPage,
  IconPreviousPage,
  IconTournee,
} from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import CreateTournee from "./CreateTournee.tsx";
import TourneePei from "./TourneePei.tsx";

const AffecterPeiTourneeMap = ({
  listePei,
  isPrive,
  closeVolet,
}: {
  listePei: { peiId: string; numeroComplet: string }[];
  listePeiNumeroComplet: string[];
  isPrive: boolean;
  closeVolet: () => void;
}) => {
  const [create, setCreate] = useState(false);
  const [update, setUpdate] = useState(false);
  const [showListePei, setShowListePei] = useState(false);
  const [tourneeId, setTourneeId] = useState<string>();

  return (
    <Container>
      <b>PEI sélectionnés :</b>{" "}
      {listePei.map((e) => e.numeroComplet).join(", ")}
      {!create && !update ? (
        <>
          <PageTitle
            displayReturnButton={false}
            icon={<IconTournee />}
            title={"Ajouter les PEI à une tournée"}
          />

          <Row>
            <Row className="mt-3 text-center">
              <Col>
                <Button
                  variant="primary"
                  onClick={() => {
                    setCreate(true);
                  }}
                >
                  <IconCreate /> Créer une tournée avec ces PEI
                </Button>
              </Col>
            </Row>
            <Row className="mt-3 text-center">
              <Col>
                <Button
                  variant="primary"
                  onClick={() => {
                    setUpdate(true);
                  }}
                >
                  <IconEdit /> Affecter ces PEI à une tournée existante
                </Button>
              </Col>
            </Row>
          </Row>
        </>
      ) : showListePei ? (
        <>
          <TourneePei
            listePeiId={listePei.map((e) => e.peiId)}
            tourneeMapId={tourneeId}
            closeVolet={closeVolet}
          />
        </>
      ) : create ? (
        <CreateTournee isFromMap={true} setTourneeId={setTourneeId} />
      ) : (
        update && (
          <>
            <Update isPrive={isPrive} setTourneeId={setTourneeId} />
          </>
        )
      )}
      {(create || update) && (
        <Row className="mt-3 text-center">
          <Col>
            <Button
              type="submit"
              variant="primary"
              onClick={() => {
                setCreate(false);
                setUpdate(false);
                setTourneeId(undefined);
                setShowListePei(false);
              }}
            >
              <IconPreviousPage /> Précédent
            </Button>
          </Col>
          {!showListePei && (
            <Col>
              <Button
                type="submit"
                variant="primary"
                disabled={tourneeId === undefined}
                onClick={() => setShowListePei(true)}
              >
                Suivant <IconNextPage />
              </Button>
            </Col>
          )}
        </Row>
      )}
    </Container>
  );
};

const Update = ({
  isPrive,
  setTourneeId,
}: {
  isPrive: boolean;
  setTourneeId: (id: string) => void;
}) => {
  const tournees = useGet(url`/api/tournee/actives?${{ isPrive: isPrive }}`);

  return (
    <>
      <PageTitle
        icon={<IconTournee />}
        title={"Choisissez la tournée"}
        displayReturnButton={false}
      />
      <ReactSelect
        isMulti={false}
        placeholder={"Sélectionnez"}
        noOptionsMessage={() => "Aucune donnée trouvée"}
        options={tournees?.data}
        getOptionValue={(t) => t.tourneeId}
        getOptionLabel={(t) => t.tourneeLibelle}
        onChange={(e) => setTourneeId(e.tourneeId)}
      />
    </>
  );
};

export default AffecterPeiTourneeMap;
