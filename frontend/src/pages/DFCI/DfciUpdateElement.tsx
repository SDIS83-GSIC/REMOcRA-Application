import { DFCI_ELEMENT } from "../../enums/DfciElementEnum.tsx";
import { UpdateDfciAire } from "./UpdateDfciAire.tsx";
import { UpdateDfciPiste } from "./UpdateDfciPiste.tsx";

/**
 * Composant permettant de visualiser l'élément
 * @param DfciElement Element sélectionné sur la carte
 * @returns Le composant de visualisation de l'élément
 */
const DfciUpdateElement = ({
  typeElem,
  elementId,
  onSubmit,
  readOnly,
}: {
  typeElem: DFCI_ELEMENT;
  elementId: string;
  onSubmit: () => void;
  readOnly: boolean;
}) => {
  switch (typeElem) {
    case DFCI_ELEMENT.AIRE:
      return (
        <UpdateDfciAire
          aireId={elementId}
          onSubmit={onSubmit}
          readOnly={readOnly}
        />
      );
    case DFCI_ELEMENT.PISTE:
      return (
        <UpdateDfciPiste
          pisteId={elementId}
          onSubmit={onSubmit}
          readOnly={readOnly}
        />
      );
  }
};
export default DfciUpdateElement;
