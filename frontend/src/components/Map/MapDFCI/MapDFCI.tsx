import { useRef } from "react";
import { Button } from "react-bootstrap";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { downloadOutputFile } from "../../../utils/fonctionsUtils.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import { IconDFCI, IconExport } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import TooltipCustom from "../../Tooltip/Tooltip.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import MapToolbarDFCI from "./MapToolbarDFCI.tsx";

const AtlasManagment = () => {
  const { success: successToast, error: errorToast } = useToastContext();
  const { data: hasElements = false } = useGet("/api/atlas/has-element", {});

  return (
    <>
      {hasElements && (
        <TooltipCustom
          tooltipText={"Télécharger l'atlas en PDF"}
          tooltipId={"afficher-docs-dfci"}
        >
          <Button
            variant="outline-primary"
            onClick={() =>
              downloadOutputFile(
                `/api/atlas/download-atlas`,
                null,
                `atlas.zip`,
                "Téléchargement terminé",
                successToast,
                errorToast,
              )
            }
            className="rounded m-2"
          >
            <IconExport />
          </Button>
        </TooltipCustom>
      )}
    </>
  );
};

const MapDFCI = () => {
  const { user } = useAppContext();
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    showOutilI,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.DFCI,
    displayPei: false,
  });

  const { toggleTool, activeTool, infoOutilI, handleCloseInfoI } =
    useToolbarContext({
      availableLayers: availableLayers,
      map: map,
      workingLayer: workingLayer,
      extraTools: {},
    });

  return (
    <>
      <PageTitle
        title="Défense de la Forêt Contre les Incendies"
        icon={<IconDFCI />}
        right={
          (hasDroit(user, TYPE_DROIT.DFCI_EXPORTATLAS_C) ||
            hasDroit(user, TYPE_DROIT.ATLAS_A)) && <AtlasManagment />
        }
      />

      <MapComponent
        map={map}
        outilI={infoOutilI}
        showOutilI={showOutilI}
        handleCloseInfoI={handleCloseInfoI}
        availableLayers={availableLayers}
        addOrRemoveLayer={addOrRemoveLayer}
        layerListRef={layerListRef}
        mapToolbarRef={mapToolbarRef}
        mapElement={mapElement}
        toggleTool={toggleTool}
        activeTool={activeTool}
        toolbarElement={mapToolbarRef.current && <MapToolbarDFCI />}
      />
    </>
  );
};

export default MapDFCI;
