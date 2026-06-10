import { Feature } from "ol";
import { Geometry } from "ol/geom";
import VectorLayer from "ol/layer/Vector";
import OLMap from "ol/Map";
import VectorSource from "ol/source/Vector";
import { ButtonGroup } from "react-bootstrap";
import THEMATIQUE from "../../../enums/ThematiqueEnum.tsx";
import VoletButtonListeDocumentThematique from "../../ListeDocumentThematique/VoletButtonListeDocumentThematique.tsx";
import { TooltipMapDFCI } from "../TooltipsMap.tsx";

const MapToolbarDFCI = ({
  map,
  dataDfciAireLayer,
}: {
  map?: OLMap;
  dataDfciAireLayer:
    | VectorLayer<VectorSource<Feature<Geometry>>, Feature<Geometry>>
    | undefined;
}) => {
  return (
    <>
      <ButtonGroup>
        <VoletButtonListeDocumentThematique
          codeThematique={THEMATIQUE.DFCI}
          titreVolet="Liste des documents DFCI"
        />
      </ButtonGroup>
      <TooltipMapDFCI map={map} dataDfciAireLayer={dataDfciAireLayer} />
    </>
  );
};

MapToolbarDFCI.displayName = "MapToolbarDFCI";

export default MapToolbarDFCI;
