import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { IconPei } from "../../components/Icon/Icon.tsx";
import ListPei from "../../components/ListePeiTable/ListePeiTable.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import url from "../../module/fetch.tsx";

const ListePeiTournee = () => {
  const { tourneeId } = useParams();

  const tourneeLibelle = useGet(url`/api/tournee/get-libelle/` + tourneeId);

  return (
    <>
      <Container>
        <PageTitle
          icon={<IconPei />}
          title={`Points d'eau de la tournée : ${tourneeLibelle?.data?.libelle ?? ""}`}
        />
      </Container>
      <Container fluid className={"px-5"}>
        <ListPei filterPage={FILTER_PAGE.TOURNEE} filterId={tourneeId} />
      </Container>
    </>
  );
};

export default ListePeiTournee;
