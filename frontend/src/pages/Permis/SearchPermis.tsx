import { WKT } from "ol/format";
import { useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGetRun } from "../../components/Fetch/useFetch.tsx";
import SelectFilterFromUrl from "../../components/Filter/SelectFilterFromUrl.tsx";
import { FieldSet, FormLabel } from "../../components/Form/Form.tsx";
import { IconMapComponent, IconSearch } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

type SearchPermisType = {
  searchNom: string;
  searchCommuneId: string;
  searchNumero: string;
  searchSection: string;
  searchParcelle: string;
  searchAvisId: string;
};

const SearchPermis = () => {
  const [searchParam, setSearchParam] = useState<SearchPermisType>({});
  const [buttonClicked, setButtonClicked] = useState<boolean>(false);

  const { data, run } = useGetRun(url`/api/permis/?${searchParam}`, {});

  useEffect(() => {
    if (buttonClicked) {
      run();
      setButtonClicked(false);
    }
  }, [buttonClicked, run]);

  const setValue = (name: string, value: string) => {
    setSearchParam((previous) => ({
      ...(previous || {}),
      [name]: value,
    }));
  };

  const SearchDisabled = Object.values(searchParam).every(
    (value) => value === "" || value === null,
  );

  return (
    <Container>
      <PageTitle
        icon={<IconSearch />}
        title="Recherche un permis"
        displayReturnButton={false}
      />
      <FieldSet>
        <Row>
          <Col>
            <Row>
              <FormLabel label={"Nom"} required={false} name="searchNom" />
            </Row>
            <Row>
              <input
                id="searchNom"
                onChange={(e) => setValue("searchNom", e.currentTarget.value)}
              />
            </Row>
          </Col>
        </Row>
        <Row>
          <Col>
            <Row>
              <FormLabel
                label={"Commune"}
                required={false}
                name="searchCommuneId"
              />
            </Row>
            <Row>
              <SelectFilterFromUrl
                name="searchCommuneId"
                url={url`/api/commune/get-libelle-commune`}
                onChange={(e) =>
                  setValue("searchCommuneId", e.value === "" ? null : e.value)
                }
              />
            </Row>
          </Col>
        </Row>
        <Row>
          <Col>
            <Row>
              <FormLabel
                label={"N° permis"}
                required={false}
                name="searchNumero"
              />
            </Row>
            <Row>
              <input
                id="searchNumero"
                onChange={(e) =>
                  setValue("searchNumero", e.currentTarget.value)
                }
              />
            </Row>
          </Col>
        </Row>
        <Row>
          <Col>
            <Row>
              <FormLabel
                label={"N° section"}
                required={false}
                name="searchSection"
              />
            </Row>
            <Row>
              <input
                id="searchSection"
                onChange={(e) =>
                  setValue("searchSection", e.currentTarget.value)
                }
              />
            </Row>
          </Col>
        </Row>
        <Row>
          <Col>
            <Row>
              <FormLabel
                label={"N° parcelle"}
                required={false}
                name="searchParcelle"
              />
            </Row>
            <Row>
              <input
                id="searchParcelle"
                onChange={(e) =>
                  setValue("searchParcelle", e.currentTarget.value)
                }
              />
            </Row>
          </Col>
        </Row>
        <Row>
          <Col>
            <Row>
              <FormLabel label={"Avis"} required={false} name="searchAvisId" />
            </Row>
            <Row>
              <SelectFilterFromUrl
                name="searchAvisId"
                url={url`/api/permis/get-libelle-permis-avis`}
                onChange={(e) => setValue("searchAvisId", e.value)}
              />
            </Row>
          </Col>
        </Row>
        {/* Bouton Rechercher */}
        <Row>
          <Col className="text-center">
            <Button
              className="mt-2"
              disabled={SearchDisabled}
              onClick={() => setButtonClicked(true)}
            >
              Rechercher
            </Button>
          </Col>
        </Row>
      </FieldSet>
      {data?.length === 0 && (
        <>
          <Row>
            <p className="fw-bold mt-2">Résultats :</p>
          </Row>
          <Row>
            <p>Aucun permis ne correspond à vos critères</p>
          </Row>
        </>
      )}
      {data?.length > 0 && (
        <>
          <Row>
            <p className="fw-bold mt-2">Résultats :</p>
          </Row>
          {data?.map((element) => (
            <CardPermisToSearchResult
              key={element.permisId}
              permisLibelle={element.permisLibelle}
              permisNumero={element.permisNumero}
              permisGeometrie={element.permisGeometrie}
            />
          ))}
        </>
      )}
    </Container>
  );
};

export default SearchPermis;

const CardPermisToSearchResult = ({
  permisLibelle,
  permisNumero,
  permisGeometrie,
}: {
  permisLibelle: string;
  permisNumero: string;
  permisGeometrie: string;
}) => {
  const navigate = useNavigate();
  const location = useLocation();

  const zoomerSurPermis = () => {
    const [rawSrid, rawFeature] = permisGeometrie.split(";");
    const srid = rawSrid.split("=").pop();
    const extent = new WKT().readGeometry(rawFeature).getExtent();

    navigate(URLS.CARTE_PERMIS, {
      state: {
        ...location.state,
        target: {
          extent,
          srid,
        },
      },
    });
  };

  return (
    <Row className="bg-light p-2 border rounded mx-2">
      <Col xs={"auto"}>
        <Button onClick={() => zoomerSurPermis()}>
          <IconMapComponent /> Localiser
        </Button>
      </Col>
      <Col>
        <Row>Permis : {permisNumero}</Row>
        <Row>{permisLibelle}</Row>
      </Col>
    </Row>
  );
};
