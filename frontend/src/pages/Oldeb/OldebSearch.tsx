import { useLocation, useNavigate } from "react-router-dom";
import React, { useState } from "react";
import { WKT } from "ol/format";
import {
  Button,
  ButtonGroup,
  Col,
  Container,
  Row,
  ToggleButton,
} from "react-bootstrap";
import url from "../../module/fetch.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconOldeb } from "../../components/Icon/Icon.tsx";
import { URLS } from "../../routes.tsx";
import { AsyncTypeahead, FieldSet } from "../../components/Form/Form.tsx";
import SelectFilterFromUrl from "../../components/Filter/SelectFilterFromUrl.tsx";

const OldebSearch = () => {
  const navigate = useNavigate();
  const location = useLocation();
  const [radioValue, setRadioValue] = useState("oldeb");
  const [communeId, setCommuneId] = useState<string>(null);
  const [section, setSection] = useState<string>(null);
  const [parcelle, setParcelle] = useState<string>(null);

  return (
    <Container>
      <PageTitle
        icon={<IconOldeb />}
        title={"Recherche des Obligations Légales de Débroussaillement"}
      />
      <FieldSet>
        <Row>
          <Col className={"py-2"}>
            <ButtonGroup>
              <ToggleButton
                id="oldeb"
                type="radio"
                variant={"outline-primary"}
                name="radio"
                value={"oldeb"}
                checked={radioValue === "oldeb"}
                onChange={(e) => setRadioValue(e.currentTarget.value)}
              >
                Parcelles soumises à une obligation légale de débroussaillement
              </ToggleButton>
              <ToggleButton
                id="cadastre"
                type="radio"
                variant={"outline-primary"}
                name="radio"
                value={"cadastre"}
                checked={radioValue === "cadastre"}
                onChange={(e) => setRadioValue(e.currentTarget.value)}
              >
                Parcelles cadastrales
              </ToggleButton>
            </ButtonGroup>
          </Col>
        </Row>
        <Row>
          <Col xl={6} lg={9} className={"py-2"}>
            <SelectFilterFromUrl
              name={"communeId"}
              url={url`/api/commune/get-libelle-commune`}
              onChange={(e) => setCommuneId(e.value)}
            />
          </Col>
        </Row>
        <Row>
          <Col xl={6} lg={9} className={"py-2"}>
            <AsyncTypeahead
              labelKey={"cadastreSectionNumero"}
              url={
                communeId ? url`/api/cadastre/commune/${communeId}/section` : ""
              }
              onChange={(e) => setSection(e)}
              disabled={!communeId}
            />
          </Col>
        </Row>
        <Row>
          <Col xl={6} lg={9} className={"py-2"}>
            <AsyncTypeahead
              labelKey={"cadastreParcelleNumero"}
              url={
                section
                  ? url`/api/cadastre/section/${section.cadastreSection}/parcelle${radioValue === "oldeb" ? "-old" : ""}`
                  : ""
              }
              onChange={(e) => {
                setParcelle(e);
              }}
              disabled={!section}
            />
          </Col>
        </Row>
        <Row>
          <Col className={"py-3"}>
            <Button
              disabled={!parcelle}
              onClick={() => {
                if (parcelle) {
                  const [rawSrid, rawFeature] =
                    parcelle.cadastreParcelleGeometrie.split(";");
                  const srid = rawSrid.split("=").pop();
                  const extent = new WKT().readGeometry(rawFeature).getExtent();
                  navigate(URLS.OLDEB_LOCALISATION, {
                    state: {
                      ...location.state,
                      target: {
                        extent,
                        srid,
                      },
                    },
                  });
                }
              }}
            >
              Localiser
            </Button>
          </Col>
        </Row>
      </FieldSet>
    </Container>
  );
};

export default OldebSearch;
