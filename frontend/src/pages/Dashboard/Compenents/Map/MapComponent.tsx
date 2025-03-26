import { Map, View } from "ol";
import { GeoJSON } from "ol/format";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import LayerGroup from "ol/layer/Group";
import OSM from "ol/source/OSM";
import VectorSource from "ol/source/Vector";
import { useEffect, useMemo, useRef } from "react";
import { Container, Row, Col } from "react-bootstrap";
import { Fill, Stroke, Style } from "ol/style";
import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";
import { useToastContext } from "../../../../module/Toast/ToastProvider.tsx";
import EPSG_3857 from "../../../../utils/constantsUtils.tsx";

const OSM_LAYER = new TileLayer({
  source: new OSM(),
});

const MapDashboardComponent = (data: any) => {
  const mapRef = useRef<HTMLDivElement>(null);
  const { warning: warningToast } = useToastContext();
  const dataMapped = setSimpleValueMapped(
    data.data ? data.data : [],
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

  // Hack Lint dependency array useMemo
  const currentRef = mapRef.current;
  const map = useMemo(() => {
    if (!currentRef) {
      return;
    }
    const map = new Map({
      target: currentRef,
      layers: [OSM_LAYER],
      view: new View({
        zoom: 6,
        projection: EPSG_3857,
        center: [244598, 5921729], // FIXME : récupérer la zone de l'utilisateur ?
        padding: [50, 50, 50, 50],
      }),
    });
    return map;
  }, [currentRef]);

  useEffect(() => {
    if (
      !map ||
      !dataMapped ||
      !Array.isArray(dataMapped) ||
      dataMapped.some((item) => !item.geojson)
    ) {
      return;
    }

    // On rafraîchit la vue en supprimant les couches hors OSM
    map
      .getLayers()
      .getArray()
      .forEach((l) => {
        if (l instanceof LayerGroup) {
          map.removeLayer(l);
        }
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
          feature
            .getGeometry()
            .transform(feature.getProperties().epsg, "EPSG:3857"); // EPSG par défaut de la View
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
  }, [map, data.config, dataMapped, warningToast]);

  return (
    <Container fluid className="h-100">
      <Row className="h-100">
        <Col className="h-100">
          <div ref={mapRef} style={{ width: "100%", height: "100%" }} />
        </Col>
      </Row>
    </Container>
  );
};

export default MapDashboardComponent;
