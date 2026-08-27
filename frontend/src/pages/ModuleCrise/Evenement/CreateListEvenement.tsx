import { Map as OlMap } from "ol";
import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import ListEvenement from "./ListEvenement.tsx";

const CreateListEvenement = ({
  criseIdentifiant,
  mapType,
  evenementStatutMode,
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
        evenementStatutMode={evenementStatutMode}
      />
    </Container>
  );
};

type CreateListEvenementType = {
  criseIdentifiant: string;
  mapType: OlMap | undefined;
  evenementStatutMode: string;
};

export default CreateListEvenement;
