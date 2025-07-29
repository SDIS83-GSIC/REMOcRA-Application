import Map from "ol/Map";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import { OSM, TileWMS, WMTS } from "ol/source";

// Configuration des optimisations de performance pour les sources de tuiles
export const TILE_PERFORMANCE_CONFIG = {
  cacheSize: 512, // Cache plus important pour les tuiles
  transition: 0, // Désactive les transitions pour un affichage plus rapide
  interpolate: false, // Désactive l'interpolation pour un rendu plus rapide
};

// Configuration des optimisations pour les couches vectorielles
export const VECTOR_PERFORMANCE_CONFIG = {
  updateWhileAnimating: false, // Évite la mise à jour pendant l'animation
  updateWhileInteracting: false, // Évite la mise à jour pendant l'interaction
  renderBuffer: 100, // Buffer de rendu réduit
};

// Fonction pour optimiser une source de tuiles
export function optimizeTileSource(source: any): any {
  // Applique les optimisations communes aux sources de tuiles
  if (
    source instanceof TileWMS ||
    source instanceof WMTS ||
    source instanceof OSM
  ) {
    // Configuration du cache et des transitions
    source.set("cacheSize", TILE_PERFORMANCE_CONFIG.cacheSize);
    source.set("transition", TILE_PERFORMANCE_CONFIG.transition);
  }

  return source;
}

// Fonction pour optimiser une couche de tuiles
export function optimizeTileLayer(layer: TileLayer<any>): TileLayer<any> {
  // Configuration du préchargement et du rendu
  layer.setPreload(1); // Précharge 1 niveau de zoom
  layer.setUseInterimTilesOnError(false); // Désactive les tuiles intermédiaires en cas d'erreur

  return layer;
}

// Fonction pour optimiser une couche vectorielle
export function optimizeVectorLayer(layer: VectorLayer<any>): VectorLayer<any> {
  // Configuration des optimisations vectorielles via les options
  layer.set(
    "updateWhileAnimating",
    VECTOR_PERFORMANCE_CONFIG.updateWhileAnimating,
  );
  layer.set(
    "updateWhileInteracting",
    VECTOR_PERFORMANCE_CONFIG.updateWhileInteracting,
  );
  layer.set("renderBuffer", VECTOR_PERFORMANCE_CONFIG.renderBuffer);

  return layer;
}

// Fonction pour optimiser la carte globalement
export function optimizeMap(map: Map): Map {
  // Configuration du rendu
  map.setLayerGroup(map.getLayerGroup()); // Force la régénération des couches

  // Optimisation des interactions de zoom
  const interactions = map.getInteractions().getArray();
  interactions.forEach((interaction) => {
    if (interaction.constructor.name === "MouseWheelZoom") {
      interaction.set("useAnchor", true);
      interaction.set("constrainResolution", true);
    }
  });

  return map;
}

// Débounce pour les événements de déplacement de carte
export function debounce<T extends (...args: any[]) => void>(
  func: T,
  wait: number,
): T {
  let timeout: NodeJS.Timeout;
  return ((...args: any[]) => {
    clearTimeout(timeout);
    timeout = setTimeout(() => func(...args), wait);
  }) as T;
}

// Throttle pour les événements de zoom
export function throttle<T extends (...args: any[]) => void>(
  func: T,
  limit: number,
): T {
  let inThrottle: boolean;
  return ((...args: any[]) => {
    if (!inThrottle) {
      func(...args);
      inThrottle = true;
      setTimeout(() => (inThrottle = false), limit);
    }
  }) as T;
}
