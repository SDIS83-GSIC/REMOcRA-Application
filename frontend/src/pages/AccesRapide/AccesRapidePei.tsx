import { useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet, useGetRun } from "../../components/Fetch/useFetch.tsx";
import SelectFilterFromList from "../../components/Filter/SelectFilterFromList.tsx";
import { FormLabel } from "../../components/Form/Form.tsx";
import { IconQuickAccess } from "../../components/Icon/Icon.tsx";
import useLocalisation, {
  GET_TYPE_GEOMETRY,
} from "../../components/Localisation/useLocalisation.tsx";
import url from "../../module/fetch.tsx";

const AccesRapidePei = () => {
  const [tourneeId, setTourneeId] = useState<string | null>();
  const [peiId, setPeiId] = useState<string | null>();
  const [communeId, setCommuneId] = useState<string | null>();
  const [voieId, setVoieId] = useState<string | null>();

  const optionsTournee = useGet(url`/api/tournee`);
  const optionPei = useGet(url`/api/pei`);
  const optionCommune = useGet(url`/api/commune`);
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
            {optionsTournee.isResolved && (
              <>
                <FormLabel
                  name={"tournee"}
                  label="Tournée :"
                  required={false}
                />
                <SelectFilterFromList
                  name={"tournee"}
                  listIdCodeLibelle={optionsTournee.data.map((v) => {
                    return {
                      id: v.tourneeId,
                      libelle: v.tourneeLibelle,
                    };
                  })}
                  onChange={(e) => setTourneeId(e.value)}
                />
              </>
            )}
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
            {optionPei.isResolved && (
              <>
                <FormLabel name={"pei"} label="PEI :" required={false} />
                <SelectFilterFromList
                  name={"pei"}
                  listIdCodeLibelle={optionPei.data.map((v) => {
                    return {
                      id: v.peiId,
                      libelle: v.peiNumeroComplet,
                    };
                  })}
                  onChange={(e) => setPeiId(e.value)}
                />
              </>
            )}
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
            {optionCommune.isResolved && (
              <>
                <FormLabel
                  name={"commune"}
                  label="Commune :"
                  required={false}
                />
                <SelectFilterFromList
                  name={"commune"}
                  listIdCodeLibelle={
                    optionCommune.data &&
                    optionCommune.data.map((v) => {
                      return {
                        id: v.communeId,
                        libelle: v.communeLibelle,
                      };
                    })
                  }
                  onChange={(e) => {
                    setCommuneId(e.value);
                  }}
                />
              </>
            )}
          </Col>
          <Col className="d-flex align-items-start flex-column" sm={2}>
            {optionCommune.isResolved && (
              <>
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
              </>
            )}
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
