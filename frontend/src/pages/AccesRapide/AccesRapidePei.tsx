import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconQuickAccess } from "../../components/Icon/Icon.tsx";

const AccesRapidePei = () => {
  return (
    <>
      <Container>
        <PageTitle icon={<IconQuickAccess />} title={"Accès rapide"} />
      </Container>
      <Container fluid className={"px-5"} />
    </>
  );
};

export default AccesRapidePei;
