import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../components/Icon/Icon.tsx";
import ListPei from "../../components/ListePeiTable/ListePeiTable.tsx";

const AccueilPei = () => {
  return (
    <>
      <Container>
        <PageTitle icon={<IconPei />} title={"Liste des points d'eau"} />
      </Container>
      <Container fluid>
        <ListPei />
      </Container>
    </>
  );
};

export default AccueilPei;
