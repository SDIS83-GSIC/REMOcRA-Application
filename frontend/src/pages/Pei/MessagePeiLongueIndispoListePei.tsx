import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../components/Icon/Icon.tsx";
import ListPei from "../../components/ListePeiTable/ListePeiTable.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import url from "../../module/fetch.tsx";

const MessagePeiLongueIndispoListePei = () => {
  const messagePeiLongueIndispoState = useGet(
    url`/api/message-pei-longue-indispo/`,
  );

  return (
    <>
      <Container>
        <PageTitle
          icon={<IconPei />}
          title={`Liste des PEI indisponibles depuis plus de ${messagePeiLongueIndispoState?.data?.nbMois} mois et ${messagePeiLongueIndispoState?.data?.nbJour} jours`}
        />
      </Container>
      <Container fluid className={"px-5"}>
        <ListPei filterPage={FILTER_PAGE.PEI_LONGUE_INDISPO} />
      </Container>
    </>
  );
};

export default MessagePeiLongueIndispoListePei;
