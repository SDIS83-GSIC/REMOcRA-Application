import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { IconPei } from "../../components/Icon/Icon.tsx";
import ListPei from "../../components/ListePeiTable/ListePeiTable.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";
import { formatDateWithFallback } from "../../utils/formatDateUtils.tsx";

const ListePeiIndisponibiliteTemporaire = () => {
  const { indisponibiliteTemporaireId } = useParams();

  const indisponibiliteTemporaireInfo = useGet(
    url`/api/indisponibilite-temporaire/` + indisponibiliteTemporaireId,
  );

  const data = indisponibiliteTemporaireInfo?.data;

  return (
    <>
      <Container>
        <PageTitle
          icon={<IconPei />}
          title="Liste des points d'eau indisponibles temporairement"
        />
        {data && (
          <div className="mt-1 fs-6">
            <div className="mb-2">
              <span className="fw-bold">Motif :</span>
              <span className="ms-2">
                {data?.indisponibiliteTemporaireMotif}
              </span>
            </div>

            <div className="mb-2">
              <span className="fw-bold">
                {data?.indisponibiliteTemporaireDateFin
                  ? "Période :"
                  : "À partir du :"}
              </span>
              <span className="ms-2">
                {formatDateWithFallback(
                  data?.indisponibiliteTemporaireDateDebut,
                )}
                {data?.indisponibiliteTemporaireDateFin &&
                  ` - ${formatDateWithFallback(data.indisponibiliteTemporaireDateFin)}`}
              </span>
            </div>
          </div>
        )}
      </Container>
      <Container fluid className={"px-5"}>
        <ListPei
          filterPage={FILTER_PAGE.INDISPONIBILITE_TEMPORAIRE}
          filterId={indisponibiliteTemporaireId}
        />
      </Container>
    </>
  );
};

export default ListePeiIndisponibiliteTemporaire;
