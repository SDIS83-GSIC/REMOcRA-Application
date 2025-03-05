import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import ListEvenement from "./ListEvenement.tsx";

const CreateListEvenement = ({ criseIdentifiant }: CreateListEvenementType) => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title="Liste des événements"
        displayReturnButton={false}
      />
      <ListEvenement criseId={criseIdentifiant} />
    </Container>
  );
};

type CreateListEvenementType = {
  criseIdentifiant: string;
};

export default CreateListEvenement;
