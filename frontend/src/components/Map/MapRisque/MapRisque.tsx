import { useMemo, useRef } from "react";
import SquelettePage from "../../../pages/SquelettePage.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import Header from "../../Header/Header.tsx";
import { IconRisque } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { TooltipMapRisque } from "../TooltipsMap.tsx";
import PARAMETRE from "../../../enums/ParametreEnum.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import { useAppContext } from "../../App/AppProvider.tsx";
import url from "../../../module/fetch.tsx";

const MapRisque = () => {
  const { user } = useAppContext();
  const mapElement = useRef<HTMLDivElement>();

  const paramPeiFicheResumeStandalone = PARAMETRE.PEI_FICHE_RESUME_STANDALONE;

  const parametresState = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(paramPeiFicheResumeStandalone),
    }}`,
    {},
  );

  const isFicheResumeStandalone = useMemo<boolean>(() => {
    if (!parametresState.isResolved) {
      return false;
    }

    return JSON.parse(
      parametresState?.data[paramPeiFicheResumeStandalone].parametreValeur,
    );
  }, [parametresState, paramPeiFicheResumeStandalone]);

  const {
    map,
    availableLayers,
    addOrRemoveLayer,
    workingLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.RISQUES,
    displayPei: true,
  });

  const { toggleTool, activeTool } = useToolbarContext({
    map: map,
    workingLayer: workingLayer,
    extraTools: {},
  });

  return (
    <SquelettePage navbar={<Header />}>
      <PageTitle title="Carte des Risques" icon={<IconRisque />} />
      <MapComponent
        map={map}
        workingLayer={workingLayer}
        availableLayers={availableLayers}
        addOrRemoveLayer={addOrRemoveLayer}
        layerListRef={layerListRef}
        mapToolbarRef={mapToolbarRef}
        mapElement={mapElement}
        toggleTool={toggleTool}
        activeTool={activeTool}
        toolbarElement={
          <TooltipMapRisque
            map={map}
            displayButtonSeeFichePei={!user || isFicheResumeStandalone}
          />
        }
      />
    </SquelettePage>
  );
};

export default MapRisque;
