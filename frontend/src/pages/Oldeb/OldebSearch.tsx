import { useNavigate } from "react-router-dom";
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
  const [radioValue, setRadioValue] = useState("oldeb");
  const [communeId, setCommuneId] = useState<string>(null);
  const [sectionId, setSectionId] = useState<string>(null);
  const [parcelleId, setParcelleId] = useState<string>(null);

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
              onChange={(e) => setSectionId(e)}
              disabled={!communeId}
            />
          </Col>
        </Row>
        <Row>
          <Col xl={6} lg={9} className={"py-2"}>
            <AsyncTypeahead
              labelKey={"cadastreParcelleNumero"}
              url={
                sectionId
                  ? url`/api/cadastre/section/${sectionId.cadastreSectionId}/parcelle${radioValue === "oldeb" ? "-old" : ""}`
                  : ""
              }
              onChange={(e) => {
                setParcelleId(e);
              }}
              disabled={!sectionId}
            />
          </Col>
        </Row>
        <Row>
          <Col className={"py-3"}>
            <Button
              disabled={!parcelleId}
              onClick={() => {
                if (parcelleId) {
                  const featurePei = new WKT().readFeature(
                    parcelleId.cadastreParcelleGeometrie.split(";")[1],
                  );
                  const bbox = featurePei.getGeometry().getExtent();
                  navigate(URLS.OLDEB_LOCALISATION, {
                    state: { bbox },
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
