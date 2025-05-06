import { ButtonGroup } from "react-bootstrap";
import VoletButtonListeDocumentThematique from "../../ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";

const MapToolbarDFCI = () => {
  return (
    <>
      <ButtonGroup>
        <VoletButtonListeDocumentThematique
          codeThematique={THEMATIQUE.DFCI}
          titreVolet="Liste des documents DFCI"
        />
      </ButtonGroup>
    </>
  );
};

MapToolbarDFCI.displayName = "MapToolbarDFCI";

export default MapToolbarDFCI;
