import VectorLayer from "ol/layer/Vector";
import { bbox as bboxStrategy } from "ol/loadingstrategy";
import "ol/ol.css";
import { Circle, Fill, Stroke, Style } from "ol/style";
import { useCallback, useEffect, useRef, useState } from "react";
import { Container } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import MapComponent, { toOpenLayer, useMapComponent } from "../Map.tsx";
import MapToolbarCouvertureHydraulique from "./MapToolbarCouvertureHydraulique.tsx";

const MapCouvertureHydraulique = ({
  etudeId,
  disabledEditPeiProjet,
  reseauImporte,
}: {
  etudeId: string;
  disabledEditPeiProjet: boolean;
  reseauImporte: boolean;
}) => {
  const [dataPeiProjetLayer, setDataPeiProjetLayer] = useState();

  const mapElement = useRef<HTMLDivElement>();

  const {
    map,
    workingLayer,
    dataPeiLayer,
    availableLayers,
    addOrRemoveLayer,
    layerListRef,
    mapToolbarRef,
  } = useMapComponent({ mapElement: mapElement });

  /**
   * Permet d'afficher les PEI en projet
   * @param etudeId l'étude concernée
   * @returns
   */
  const createDataPeiProjetLayer = useCallback(
    (etudeId: string) => {
      const vectorSource = toOpenLayer({
        source: "GSON",
        loader: async (extent, resolution, projection, success, failure) => {
          const res = await fetch(
            url`/api/couverture-hydraulique/layer?bbox=` +
              extent.join(",") +
              "&srid=" +
              projection.getCode() +
              "&etudeId=" +
              etudeId,
            getFetchOptions({ method: "GET" }),
          );
          res
            .text()
            .then((text) => {
              const features = vectorSource
                .getFormat()
                .readFeatures(JSON.parse(text));
              vectorSource.addFeatures(features);
              success(features);
            })
            .catch(() => {
              vectorSource.removeLoadedExtent(extent);
              failure();
            });
        },
        extent: map?.getView().calculateExtent(),
        projection: "EPSG:2154",
        strategy: bboxStrategy,
      });

      const dl = new VectorLayer({
        source: vectorSource,
        style: new Style({
          image: new Circle({
            radius: 5,
            fill: new Fill({ color: "green" }),
            stroke: new Stroke({
              color: [255, 0, 0],
              width: 1,
            }),
          }),
        }),
        extent: map?.getView().calculateExtent(),
        opacity: 1,
        visible: true,
        minResolution: 0,
        maxResolution: 99999,
        zIndex: 9999,
      });

      map?.addLayer(dl);

      return dl;
    },
    [map],
  );

  useEffect(() => {
    setDataPeiProjetLayer(createDataPeiProjetLayer(etudeId));
  }, [etudeId, setDataPeiProjetLayer, createDataPeiProjetLayer]);

  return (
    <Container fluid>
      {map && availableLayers && (
        <>
          <MapToolbarCouvertureHydraulique
            map={map}
            etudeId={etudeId}
            workingLayer={workingLayer}
            dataPeiLayer={dataPeiLayer}
            dataPeiProjetLayer={dataPeiProjetLayer}
            disabledEditPeiProjet={disabledEditPeiProjet}
            reseauImporte={reseauImporte}
          />
          <MapComponent
            map={map}
            workingLayer={workingLayer}
            mapElement={mapElement}
            availableLayers={availableLayers}
            addOrRemoveLayer={addOrRemoveLayer}
            layerListRef={layerListRef}
            mapToolbarRef={mapToolbarRef}
          />
        </>
      )}
    </Container>
  );
};

export default MapCouvertureHydraulique;
