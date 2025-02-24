import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconDocument } from "../../../components/Icon/Icon.tsx";
import ListDocument from "./ListDocument.tsx";

const CreateListDocuments = ({
  criseIdentifiant,
  onSubmit,
}: CreateListDocumentsType) => {
  return (
    <Container>
      <PageTitle
        icon={<IconDocument />}
        title="Liste des documents"
        displayReturnButton={false}
      />
      <ListDocument criseId={criseIdentifiant} onSubmit={onSubmit} />
    </Container>
  );
};

type CreateListDocumentsType = {
  criseIdentifiant: string;
  onSubmit: any;
};

export default CreateListDocuments;
