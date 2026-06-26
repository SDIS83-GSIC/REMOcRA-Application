import { Feature } from "ol";
import { Geometry } from "ol/geom";
import VectorLayer from "ol/layer/Vector";
import VectorSource from "ol/source/Vector";
import { useMemo, useRef } from "react";
import { DFCI_LISTE_COUCHE } from "../../../enums/DfciListeCoucheEnum.tsx";
import PARAMETRE from "../../../enums/ParametreEnum.tsx";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../Fetch/useFetch.tsx";
import { IconDFCI } from "../../Icon/Icon.tsx";
import { TypeModuleRemocra } from "../../ModuleRemocra/ModuleRemocra.tsx";
import MapComponent, { useMapComponent } from "../Map.tsx";
import { useToolbarContext } from "../MapToolbar.tsx";
import { createPointLayer } from "../MapUtils.tsx";
import MapToolbarDFCI from "./MapToolbarDFCI.tsx";

const MapDFCI = () => {
  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    showOutilI,
    mapToolbarRef,
    projection,
  } = useMapComponent({
    mapElement: mapElement,
    typeModule: TypeModuleRemocra.DFCI,
    displayPei: false,
  });

  const parametreCouche = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.DFCI_LISTE_COUCHE]),
    }}`,
  );

  const listeCouche: DFCI_LISTE_COUCHE[] = useMemo<DFCI_LISTE_COUCHE[]>(() => {
    if (!parametreCouche.isResolved) {
      return [];
    }

    return JSON.parse(
      parametreCouche?.data[PARAMETRE.DFCI_LISTE_COUCHE].parametreValeur,
    );
  }, [parametreCouche]);

  const dataDfciAireLayer:
    | VectorLayer<VectorSource<Feature<Geometry>>, Feature<Geometry>>
    | undefined = useMemo(() => {
    if (!map || !listeCouche.includes(DFCI_LISTE_COUCHE.DFCI_AIRE)) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/dfci-aires/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection, listeCouche]);

  const dataDfciPisteLayer:
    | VectorLayer<VectorSource<Feature<Geometry>>, Feature<Geometry>>
    | undefined = useMemo(() => {
    if (!map || !listeCouche.includes(DFCI_LISTE_COUCHE.DFCI_PISTE)) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/dfci-pistes/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection, listeCouche]);

  const dataDfciDebLayer:
    | VectorLayer<VectorSource<Feature<Geometry>>, Feature<Geometry>>
    | undefined = useMemo(() => {
    if (!map || !listeCouche.includes(DFCI_LISTE_COUCHE.DFCI_DEB)) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/dfci-deb/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection, listeCouche]);

  const dataDfciPanneauLayer:
    | VectorLayer<VectorSource<Feature<Geometry>>, Feature<Geometry>>
    | undefined = useMemo(() => {
    if (!map || !listeCouche.includes(DFCI_LISTE_COUCHE.DFCI_PANNEAU)) {
      return;
    }
    return createPointLayer(
      map,
      (extent, projection) =>
        `/api/dfci-panneau/layer?bbox=` +
        extent.join(",") +
        "&srid=" +
        projection.getCode(),
      projection,
    );
  }, [map, projection, listeCouche]);

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
        title={"Défense de la Forêt Contre les Incendies"}
        icon={<IconDFCI />}
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
        toolbarElement={
          mapToolbarRef.current && (
            <MapToolbarDFCI
              map={map}
              dataDfciAireLayer={dataDfciAireLayer}
              dataDfciPisteLayer={dataDfciPisteLayer}
              dataDfciDebLayer={dataDfciDebLayer}
              dataDfciPanneauLayer={dataDfciPanneauLayer}
            />
          )
        }
      />
    </>
  );
};

export default MapDFCI;
