import { Button, Col, Container, Row } from "react-bootstrap";
import { useCallback, useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { WKT } from "ol/format";
import { GeometryCollection } from "ol/geom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconQuickAccess } from "../../components/Icon/Icon.tsx";
import { FormLabel } from "../../components/Form/Form.tsx";
import { useGet, useGetRun } from "../../components/Fetch/useFetch.tsx";
import url, { getFetchOptions } from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import SelectFilterFromList from "../../components/Filter/SelectFilterFromList.tsx";

enum GET_TYPE_GEOMETRY {
  COMMUNE = "/api/commune",
  TOURNEE = "/api/tournee",
  PEI = "/api/pei",
  VOIE = "/api/voie",
}

const AccesRapidePei = () => {
  const navigate = useNavigate();

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

  const fetchGeometry = useCallback(
    async (typeGeometry: string, idType: string) => {
      (
        await fetch(
          url`${typeGeometry}/${idType}/geometrie`,
          getFetchOptions({
            method: "GET",
          }),
        )
      )
        .json()
        .then((resData) => {
          let bbox;

          if (GET_TYPE_GEOMETRY.PEI === typeGeometry) {
            // PEI
            const feature = new WKT().readGeometry(resData.split(";").pop());
            bbox = feature.getExtent();
          } else if (GET_TYPE_GEOMETRY.TOURNEE === typeGeometry) {
            // Tournée
            const geometrie = new GeometryCollection(
              resData.map((pei) =>
                new WKT().readGeometry(pei.split(";").pop()),
              ),
            );
            bbox = geometrie.getExtent();
          } else if (GET_TYPE_GEOMETRY.COMMUNE === typeGeometry) {
            // Commune
            const feature = new WKT().readGeometry(
              resData.communeGeometry.split(";").pop(),
            );
            bbox = feature.getExtent();
          } else {
            // Voie
            const feature = new WKT().readGeometry(
              resData.voieGeometry.split(";").pop(),
            );
            bbox = feature.getExtent();
          }

          navigate(URLS.DECI_CARTE, {
            state: {
              ...location.state,
              bbox,
            },
          });
        });
    },
    [navigate],
  );

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
