import { useState } from "react";
import { Button } from "react-bootstrap";
import { IconDocument } from "../Icon/Icon.tsx";
import Volet from "../Volet/Volet.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import THEMATIQUE from "../../enums/ThematiqueEnum.tsx";
import ListeDocumentThematique from "./ListeDocumentThematique.tsx";

const VoletButtonListeDocumentThematique = ({
  codeThematique,
  titreVolet,
}: {
  codeThematique: THEMATIQUE;
  titreVolet: string;
}) => {
  const [showDocs, setShowDocs] = useState(false);
  const handleCloseDocs = () => setShowDocs(false);
  return (
    <>
      <TooltipCustom
        tooltipText={"Afficher la liste des documents"}
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
      <Volet handleClose={handleCloseDocs} show={showDocs} className="w-auto">
        <ListeDocumentThematique
          codeThematique={codeThematique}
          titre={titreVolet}
          icon={<IconDocument />}
        />
      </Volet>
    </>
  );
};

export default VoletButtonListeDocumentThematique;
