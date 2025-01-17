import { Map, View } from "ol";
import { GeoJSON } from "ol/format";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import OSM from "ol/source/OSM";
import VectorSource from "ol/source/Vector";
import { useEffect, useRef } from "react";
import { Container, Row, Col } from "react-bootstrap";
import { Fill, Stroke, Style } from "ol/style";
import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";

const MapDashboardComponent = (data: any) => {
  const mapRef = useRef<HTMLDivElement>(null);

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

  useEffect(() => {
    // Créer une couche de base (OSM)
    const baseLayer = new TileLayer({
      source: new OSM(),
    });

    // Vérifiez que `data` est un tableau avant de parcourir
    const vectorLayers: any =
      Array.isArray(dataMapped) && dataMapped.length > 0
        ? dataMapped
            .map((item: any) => {
              try {
                const geojsonObject = JSON.parse(item.geojson);
                const vectorSource = new VectorSource({
                  features: new GeoJSON().readFeatures(geojsonObject, {
                    dataProjection:
                      geojsonObject?.crs?.properties?.name || "EPSG:4326",
                    featureProjection: "EPSG:3857",
                  }),
                });

                // Calculer le pourcentage
                const value = parseFloat(item.value);
                const max = parseFloat(item.max);
                const percentage = (value / max) * 100;

                // Obtenir la couleur en fonction du pourcentage
                const color = getColorForPercentage(
                  percentage,
                  data.config?.limits || [],
                );

                // Créer un style dynamique pour la couche vectorielle
                const style = new Style({
                  stroke: new Stroke({
                    color: color, // Couleur du contour
                    width: 2,
                  }),
                  fill: new Fill({
                    color: `${color}40`, // Couleur de remplissage avec transparence (ajout de '40' pour 25% d'opacité)
                  }),
                });

                return new VectorLayer({
                  source: vectorSource,
                  style: style,
                });
              } catch (error) {
                return null;
              }
            })
            .filter(Boolean) // Supprime les couches nulles
        : [];

    // Créer la carte
    const map = new Map({
      target: mapRef.current!,
      layers: [baseLayer, ...vectorLayers],
      view: new View({
        zoom: 1,
      }),
    });

    // Zoomer et centrer sur les données
    if (vectorLayers.length > 0) {
      const vectorSource = vectorLayers[0].getSource(); // Prend la première couche vectorielle
      if (vectorSource) {
        const extent = vectorSource.getExtent(); // Obtient l'étendue des données
        map.getView().fit(extent, {
          padding: [50, 50, 50, 50], // Marge autour des données (en pixels)
          maxZoom: 15, // Zoom maximum pour éviter un zoom trop rapproché
        });
      }
    }

    return () => {
      map.setTarget(null);
    };
  }, [data, dataMapped]);

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
