import { useState, useRef } from "react";
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
import Loading from "../../components/Elements/Loading/Loading.tsx";
import CreateTournee from "./CreateTournee.tsx";
import TourneePei from "./TourneePei.tsx";

const AffecterPeiTourneeMap = ({
  listePei,
  isPrive,
  isIcpe,
  closeVolet,
}: {
  listePei: { peiId: string; numeroComplet: string }[];
  isPrive: boolean;
  isIcpe: boolean;
  closeVolet: () => void;
}) => {
  const [create, setCreate] = useState(false);
  const [update, setUpdate] = useState(false);
  const [showListePei, setShowListePei] = useState(false);
  const [tourneeId, setTourneeId] = useState<string>();

  // Callback pour recevoir l'id de la tournée créée et afficher la liste
  const handleTourneeCreated = (id: string) => {
    setTourneeId(id);
    setShowListePei(true);
  };

  // Ref pour contrôler la soumission du formulaire CreateTournee
  const createTourneeRef = useRef<{ submit: () => void }>(null);

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
        <CreateTournee
          ref={createTourneeRef}
          isFromMap={true}
          setTourneeId={handleTourneeCreated}
          listePei={listePei.map((e) => e.peiId)}
        />
      ) : (
        update && (
          <>
            <Update
              isPrive={isPrive}
              isIcpe={isIcpe}
              listePei={listePei.map((e) => e.peiId)}
              setTourneeId={setTourneeId}
            />
          </>
        )
      )}
      {(create || update) && (
        <Row className="mt-3 text-center">
          <Col>
            <Button
              type="button"
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
          {/* Si on est en création, le bouton Suivant déclenche la soumission du formulaire via la ref */}
          {create && !showListePei && (
            <Col>
              <Button
                type="button"
                variant="primary"
                onClick={() => {
                  if (
                    createTourneeRef.current &&
                    createTourneeRef.current.submit
                  ) {
                    createTourneeRef.current.submit();
                  }
                }}
              >
                Suivant <IconNextPage />
              </Button>
            </Col>
          )}
          {/* Si on est en update, juste passer a l'étape suiavnte */}
          {update && !showListePei && (
            <Col>
              <Button
                type="button"
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
  isIcpe,
  listePei,
  setTourneeId,
}: {
  isPrive: boolean;
  isIcpe: boolean;
  listePei: string[];
  setTourneeId: (id: string) => void;
}) => {
  const tournees = useGet(
    url`/api/tournee/actives?${{ isPrive: isPrive, isIcpe: isIcpe, listePei: JSON.stringify(listePei) }}`,
  );
  if (!tournees.isResolved) {
    return <Loading />;
  }
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
