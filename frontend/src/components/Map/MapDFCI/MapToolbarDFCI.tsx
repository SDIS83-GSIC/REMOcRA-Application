import { ButtonGroup } from "react-bootstrap";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";
import VoletButtonListeDocumentThematique from "../../ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";

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
