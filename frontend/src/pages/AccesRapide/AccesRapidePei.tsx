import { useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGetRun } from "../../components/Fetch/useFetch.tsx";
import SelectFilterFromList from "../../components/Filter/SelectFilterFromList.tsx";
import { FormLabel } from "../../components/Form/Form.tsx";
import { IconQuickAccess } from "../../components/Icon/Icon.tsx";
import useLocalisation, {
  GET_TYPE_GEOMETRY,
} from "../../components/Localisation/useLocalisation.tsx";
import url from "../../module/fetch.tsx";
import AccesRapideTypeahead from "./AccesRapideTypeahead.tsx";

const AccesRapidePei = () => {
  const [tourneeId, setTourneeId] = useState<string | null>();
  const [peiId, setPeiId] = useState<string | null>();
  const [communeId, setCommuneId] = useState<string | null>();
  const [voieId, setVoieId] = useState<string | null>();

  const { run: fetchOptionVoie, data: optionVoie } = useGetRun(
    url`/api/voie/${communeId}`,
    {},
  );

  const { fetchGeometry } = useLocalisation();

  useEffect(() => {
    if (
      (communeId && !optionVoie) ||
      (optionVoie &&
        optionVoie.find(
          (voie: { voieCommuneId: string }) => voie.voieCommuneId !== communeId,
        ))
    ) {
      fetchOptionVoie();
    }
  }, [communeId, fetchOptionVoie, optionVoie]);

  return (
    <>
      <Container>
        <PageTitle icon={<IconQuickAccess />} title={"Accès rapide"} />
      </Container>
      <Container fluid className={"px-5"}>
        {/* Tournée */}
        <Row className="align-items-end mb-3">
          <Col className="d-flex align-items-start flex-column" sm={2}>
            <FormLabel name={"tournee"} label="Tournée :" required={false} />
            <AccesRapideTypeahead
              label="Tournée"
              queryUrl="/api/tournee/acces-rapide"
              setter={setTourneeId}
            />
          </Col>
          <Col>
            <Button
              variant="primary"
              onClick={() => {
                fetchGeometry(GET_TYPE_GEOMETRY.TOURNEE, tourneeId);
              }}
            >
              Localiser
            </Button>
          </Col>
        </Row>
        {/* PEI */}
        <Row className="align-items-end mb-3">
          <Col className="d-flex align-items-start flex-column" sm={2}>
            <FormLabel name={"pei"} label="PEI :" required={false} />
            <AccesRapideTypeahead
              label="PEI"
              queryUrl="/api/pei/acces-rapide"
              setter={setPeiId}
            />
          </Col>
          <Col>
            <Button
              variant="primary"
              onClick={() => {
                fetchGeometry(GET_TYPE_GEOMETRY.PEI, peiId);
              }}
            >
              Localiser
            </Button>
          </Col>
        </Row>
        {/* Commune */}
        <Row className="align-items-end mb-3">
          <Col className="d-flex align-items-start flex-column" sm={2}>
            <FormLabel name={"commune"} label="Commune :" required={false} />
            <AccesRapideTypeahead
              label="Commune"
              queryUrl="/api/commune/acces-rapide"
              setter={setCommuneId}
            />
          </Col>
          <Col className="d-flex align-items-start flex-column" sm={2}>
            <FormLabel name={"voie"} label="Voie :" required={false} />
            <SelectFilterFromList
              name={"voie"}
              listIdCodeLibelle={
                optionVoie &&
                optionVoie.map((v) => {
                  return {
                    id: v.voieId,
                    libelle: v.voieLibelle,
                  };
                })
              }
              onChange={(e) => setVoieId(e ? e.value : null)}
              isClearable={true}
              disabled={!communeId}
            />
          </Col>
          <Col>
            <Button
              variant="primary"
              onClick={() => {
                if (!voieId || voieId === undefined) {
                  fetchGeometry(GET_TYPE_GEOMETRY.COMMUNE, communeId);
                } else {
                  fetchGeometry(GET_TYPE_GEOMETRY.VOIE, voieId);
                }
              }}
            >
              Localiser
            </Button>
          </Col>
        </Row>
      </Container>
    </>
  );
};

export default AccesRapidePei;
