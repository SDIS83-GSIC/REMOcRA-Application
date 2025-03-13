import { Container } from "react-bootstrap";
import Map from "ol/Map";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconDocument } from "../../../components/Icon/Icon.tsx";
import ListDocument from "./ListDocument.tsx";

const CreateListDocuments = ({
  criseIdentifiant,
  onSubmit,
  map,
}: CreateListDocumentsType) => {
  return (
    <Container>
      <PageTitle
        icon={<IconDocument />}
        title="Liste des documents"
        displayReturnButton={false}
      />
      <ListDocument criseId={criseIdentifiant} onSubmit={onSubmit} map={map} />
    </Container>
  );
};

type CreateListDocumentsType = {
  criseIdentifiant: string;
  onSubmit: any;
  map: Map;
};

export default CreateListDocuments;
