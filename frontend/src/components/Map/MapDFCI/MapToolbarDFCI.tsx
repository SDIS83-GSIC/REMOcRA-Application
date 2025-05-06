import { useState } from "react";
import { Button, ButtonGroup } from "react-bootstrap";
import { IconDocument } from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import ListeDocumentThematique from "../../ListeDocumentThematique/ListeDocumentThematique.tsx";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";

const MapToolbarDFCI = () => {
  const [showDocs, setShowDocs] = useState(false);
  const handleCloseDocs = () => setShowDocs(false);

  return (
    <>
      <ButtonGroup>
        <TooltipCustom
          tooltipText={"Afficher la liste des documents DFCI"}
          tooltipId={"afficher-docs-dfci"}
        >
          <Button
            variant="outline-primary"
            onClick={() => setShowDocs(true)}
            className="rounded m-2"
          >
            <IconDocument />
          </Button>
        </TooltipCustom>
      </ButtonGroup>
      <Volet handleClose={handleCloseDocs} show={showDocs} className="w-auto">
        <ListeDocumentThematique
          codeThematique={THEMATIQUE.DFCI}
          titre="Liste des documents DFCI"
          icon={<IconDocument />}
        />
      </Volet>
    </>
  );
};

MapToolbarDFCI.displayName = "MapToolbarDFCI";

export default MapToolbarDFCI;
