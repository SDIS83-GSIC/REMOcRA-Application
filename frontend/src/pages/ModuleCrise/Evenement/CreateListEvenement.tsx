import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import ListEvenement from "./ListEvenement.tsx";

const CreateListEvenement = ({
  criseIdentifiant,
  mapType,
  state,
}: CreateListEvenementType) => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title="Liste des événements"
        displayReturnButton={false}
      />
      <ListEvenement
        criseId={criseIdentifiant}
        map={mapType}
        stateEvent={state}
      />
    </Container>
  );
};

type CreateListEvenementType = {
  criseIdentifiant: string;
  mapType: Map | undefined;
  state: string;
};

export default CreateListEvenement;
