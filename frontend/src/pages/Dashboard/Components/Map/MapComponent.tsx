import { Map, View } from "ol";
import { GeoJSON, WKT } from "ol/format";
import LayerGroup from "ol/layer/Group";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import Overlay from "ol/Overlay";
import { transformExtent } from "ol/proj";
import OSM from "ol/source/OSM";
import VectorSource from "ol/source/Vector";
import { Fill, Stroke, Style } from "ol/style";
import { useEffect, useRef, useState } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { useAppContext } from "../../../../components/App/AppProvider.tsx";
import { useToastContext } from "../../../../module/Toast/ToastProvider.tsx";
import { EPSG_3857 } from "../../../../utils/constantsUtils.tsx";
import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";

// ne pas partager la même instance entre plusieurs carte
function createOSMLayer() {
  return new TileLayer({
    source: new OSM(),
  });
}

const MapDashboardComponent = (data: any) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const tooltipRef = useRef<HTMLDivElement>(null);

  const baseLayerRef = useRef<TileLayer | null>(null);
  const {
    user,
    epsg: projection,
    extent: defaultExtent,
    extentSRID,
  } = useAppContext();

  const { warning: warningToast } = useToastContext();
  const dataMapped = setSimpleValueMapped(
    data?.data ? data.data : [],
    data.config ? data.config : [],
  );

  // Fonction pour déterminer la couleur en fonction du pourcentage
  const getColorForPercentage = (percentage: number, limits: any[]) => {
    if (!limits || limits.length === 0) {
      return "#000000";
    }

    // Trier les limites par valeur croissante
    const sortedLimits = limits.sort(
      (a, b) => parseFloat(a.value) - parseFloat(b.value),
    );

    // Trouver la couleur correspondante
    for (const limit of sortedLimits) {
      if (percentage <= parseFloat(limit.value)) {
        return limit.color;
      }
    }

    // Si le pourcentage dépasse toutes les limites, utiliser la dernière couleur
    return sortedLimits[sortedLimits.length - 1].color;
  };
  const [map, setMap] = useState<Map | null>(null);
  const [, setTooltip] = useState<Overlay | null>(null);

  // Initialisation de la carte
  useEffect(() => {
    const currentRef = mapRef.current;
    const tooltipElement = tooltipRef.current;
    if (!currentRef || !tooltipElement) {
      return;
    }

    baseLayerRef.current = createOSMLayer();

    const newMap = new Map({
      target: currentRef,
      layers: [baseLayerRef.current!],
      view: new View({
        zoom: 6,
        projection: EPSG_3857,
        center: [244598, 5921729], // FIXME : récupérer la zone de l'utilisateur ?
        padding: [50, 50, 50, 50],
      }),
    });

    // Créer l'overlay pour la tooltip
    const newTooltip = new Overlay({
      element: tooltipElement,
      offset: [10, 0],
      positioning: "bottom-left",
    });
    newMap.addOverlay(newTooltip);
    setTooltip(newTooltip);

    // Événements pour la tooltip
    newMap.on("pointermove", (evt) => {
      const feature = newMap.forEachFeatureAtPixel(
        evt.pixel,
        (feature) => feature,
      );

      if (feature) {
        const properties = feature.getProperties();
        const coordinate = evt.coordinate;

        // Calculer le pourcentage
        const value = parseFloat(properties.value) || 0;
        const max = parseFloat(properties.max) || 1;
        const percentage = ((value / max) * 100).toFixed(1);

        // Contenu de la tooltip
        tooltipElement.innerHTML = `
          <div style="
            background: rgba(0, 0, 0, 0.8);
            color: white;
            padding: 8px 12px;
            border-radius: 4px;
            font-size: 12px;
            width: 300px;
          ">
            <div><strong>${properties.libelle ?? "Informations"}</strong></div>
            <div>${data.config.value} : ${value}</div>
            <div>${data.config.max} : ${max}</div>
            <div>Pourcentage: ${percentage}%</div>
          </div>
        `;

        newTooltip.setPosition(coordinate);
        tooltipElement.style.display = "block";
      } else {
        tooltipElement.style.display = "none";
      }
    });

    // L'extent de la map est défini par défaut à la zone de compétence de l'utilisateur ou à l'emprise définie par défaut
    if (user?.zoneIntegrationExtent) {
      const rawExtent = new WKT()
        .readGeometry(user.zoneIntegrationExtent.split(";").pop())
        .getExtent();
      newMap
        ?.getView()
        .fit(transformExtent(rawExtent, projection.name, EPSG_3857), {
          maxZoom: 20,
        });
    } else {
      newMap
        .getView()
        .fit(transformExtent(defaultExtent, extentSRID, EPSG_3857), {
          maxZoom: 20,
        }); // Centre depuis l'étendue fournie par le serveur)
    }

    setMap(newMap);

    // Cleanup function pour détruire la carte quand le composant se démonte
    return () => {
      newMap.setTarget(undefined);
    };
  }, [
    user?.zoneIntegrationExtent,
    projection.name,
    defaultExtent,
    extentSRID,
    data.config,
  ]);

  useEffect(() => {
    if (
      !map ||
      !dataMapped ||
      !Array.isArray(dataMapped) ||
      data.data === undefined
    ) {
      return;
    }

    // On rafraîchit la vue en supprimant les couches hors OSM
    map
      .getLayers()
      .getArray()
      .filter((l: any) => l !== baseLayerRef.current)
      .forEach((l: any) => {
        map.removeLayer(l);
      });

    // JSONParseException
    try {
      // Transformation des données en FeatureCollection pour pouvoir créer une seule couche
      const geojsonObject = {
        type: "FeatureCollection",
        features: dataMapped.map((item) => {
          // eslint-disable-next-line @typescript-eslint/no-unused-vars
          const { geojson: _, ...properties } = item;
          const geometry = JSON.parse(item.geojson);
          return {
            type: "Feature",
            properties: { ...properties, epsg: geometry.crs.properties.name },
            geometry: geometry,
          };
        }),
      };

      const vectorSource = new VectorSource({
        // Garde-fou : conversion individuelle des géométries depuis leur propre EPSG
        features: new GeoJSON().readFeatures(geojsonObject).map((feature) => {
          const geometry = feature.getGeometry();
          if (geometry) {
            geometry.transform(feature.getProperties().epsg, EPSG_3857); // EPSG par défaut de la View
          }
          return feature;
        }),
      });

      const dataLayer = new VectorLayer({
        source: vectorSource,
        style: (feature) => {
          const properties = feature.getProperties();

          // Calculer le pourcentage
          const value = parseFloat(properties.value);
          const max = parseFloat(properties.max);
          const percentage = (value / max) * 100;

          // Obtenir la couleur en fonction du pourcentage
          const color = getColorForPercentage(
            percentage,
            data.config?.limits || [],
          );

          // Créer un style dynamique pour la couche vectorielle
          return new Style({
            stroke: new Stroke({
              color: color, // Couleur du contour
              width: 2,
            }),
            fill: new Fill({
              color: `${color}40`, // Couleur de remplissage avec transparence (ajout de '40' pour 25% d'opacité)
            }),
          });
        },
      });

      // On ajoute la couche dans un groupe (pour la distinguer de la couche OSM)
      map.addLayer(new LayerGroup({ layers: [dataLayer] }));

      // Zoomer et centrer sur les données
      const extent = vectorSource.getExtent(); // Obtient l'étendue des données
      map.getView().fit(extent, {
        padding: [50, 50, 50, 50], // Marge autour des données (en pixels)
        maxZoom: 15, // Zoom maximum pour éviter un zoom trop rapproché
      });
    } catch (e) {
      warningToast("Géométrie invalide");
    }
  }, [map, data.config, dataMapped, warningToast, data.data]);

  return (
    <Container fluid className="h-100">
      <Row className="h-100">
        <Col className="h-100">
          <div ref={mapRef} style={{ width: "100%", height: "100%" }} />
          <div
            ref={tooltipRef}
            style={{
              position: "absolute",
              pointerEvents: "none",
              display: "none",
              zIndex: 1000,
            }}
          />
        </Col>
      </Row>
    </Container>
  );
};

export default MapDashboardComponent;
