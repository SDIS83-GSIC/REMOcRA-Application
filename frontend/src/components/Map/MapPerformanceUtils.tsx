import Feature from "ol/Feature";
import { Geometry } from "ol/geom";
import ImageTile from "ol/ImageTile";
import TileLayer from "ol/layer/Tile";
import VectorLayer from "ol/layer/Vector";
import OLMap from "ol/Map";
import { OSM, TileWMS, WMTS } from "ol/source";

// Configuration des optimisations de performance pour les sources de tuiles
export const TILE_PERFORMANCE_CONFIG = {
  cacheSize: 1024,
  transition: 0, // Désactive les transitions pour un affichage plus rapide
  interpolate: false, // Désactive l'interpolation pour un rendu plus rapide
};

// Configuration des optimisations pour les couches vectorielles
export const VECTOR_PERFORMANCE_CONFIG = {
  updateWhileAnimating: false, // Évite la mise à jour pendant l'animation
  updateWhileInteracting: false, // Évite la mise à jour pendant l'interaction
  renderBuffer: 150,
};

// Fonction pour optimiser une source de tuiles
export function optimizeTileSource(
  source: TileWMS | WMTS | OSM,
): TileWMS | WMTS | OSM {
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
export function optimizeTileLayer(
  layer: TileLayer<TileWMS | WMTS | OSM>,
): TileLayer<TileWMS | WMTS | OSM> {
  // Configuration du préchargement et du rendu
  layer.setPreload(1); // Précharge 1 niveau de zoom
  layer.setUseInterimTilesOnError(false); // Désactive les tuiles intermédiaires en cas d'erreur

  return layer;
}

// Fonction pour optimiser une couche vectorielle
export function optimizeVectorLayer(
  layer: VectorLayer<Feature<Geometry>>,
): VectorLayer<Feature<Geometry>> {
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
export function optimizeMap(map: OLMap): OLMap {
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

// Patch canvas context pour optimiser les opérations de readback (getImageData)
// Résout le warning: "Canvas2D: Multiple readback operations using getImageData are faster with the willReadFrequently attribute set to true"
export function patchCanvasContextForReadback(): void {
  const originalGetContext = HTMLCanvasElement.prototype.getContext;

  HTMLCanvasElement.prototype.getContext = function (
    contextType: string,
    contextAttributes?:
      | CanvasRenderingContext2DSettings
      | WebGLContextAttributes
      | ImageBitmapRenderingContextSettings,
  ) {
    // Pour les contextes 2D (utilisés par OpenLayers pour le rendu), ajouter l'option willReadFrequently
    if (contextType === "2d") {
      const settings = (contextAttributes ||
        {}) as CanvasRenderingContext2DSettings;
      settings.willReadFrequently = true;
      contextAttributes = settings;
    }
    return originalGetContext.call(this, contextType, contextAttributes);
  } as typeof HTMLCanvasElement.prototype.getContext;
}

// Convertit la valeur crossOrigin HTML en options fetch équivalentes
function crossOriginToFetchOptions(crossOrigin?: string | null): RequestInit {
  if (crossOrigin === "use-credentials") {
    return { mode: "cors", credentials: "include" };
  }
  if (crossOrigin === "anonymous") {
    return { mode: "cors", credentials: "omit" };
  }
  return { credentials: "same-origin" };
}

// Crée une tileLoadFunction avec annulation des requêtes en cours via AbortController.
// Chaque appel retourne une closure indépendante (un contrôleur par source).
export function createAbortableTileLoadFunction(
  crossOrigin?: string | null,
): (tile: ImageTile, src: string) => void {
  const controllers = new Map<string, AbortController>();
  const fetchOptions = crossOriginToFetchOptions(crossOrigin);

  return (tile: ImageTile, src: string) => {
    // Annuler une éventuelle requête en cours pour la même URL (re-demande de la même tuile)
    controllers.get(src)?.abort();

    const controller = new AbortController();
    controllers.set(src, controller);

    // OL appelle dispose() sur la tuile quand elle quitte le viewport — on abort alors la requête
    const onDispose = () => {
      controller.abort();
      controllers.delete(src);
      tile.removeEventListener("dispose", onDispose);
    };
    tile.addEventListener("dispose", onDispose);

    fetch(src, { ...fetchOptions, signal: controller.signal })
      .then((res) => res.blob())
      .then((blob) => {
        const imgEl = tile.getImage() as HTMLImageElement;
        const objectUrl = URL.createObjectURL(blob);
        imgEl.onload = () => URL.revokeObjectURL(objectUrl);
        imgEl.src = objectUrl;
        controllers.delete(src);
      })
      .catch((e) => {
        controllers.delete(src);
        if (e.name !== "AbortError") {
          (tile.getImage() as HTMLImageElement).dispatchEvent(
            new Event(
              "Erreur lors de la récupération de la tuile : " + e.message,
            ),
          );
        }
      });
  };
}

// Crée une imageLoadFunction pour ImageWMS qui annule la requête précédente
// dès qu'une nouvelle vue est demandée (déplacement / zoom).
// Chaque appel retourne une closure indépendante (un contrôleur par source).
export function createAbortableImageLoadFunction(): (
  image: ImageTile,
  src: string,
) => void {
  let controller: AbortController | null = null;
  let currentObjectUrl: string | null = null;

  return (image: ImageTile, src: string) => {
    // Annuler la requête précédente si elle est encore en cours
    controller?.abort();
    controller = new AbortController();
    // Libérer l'URL blob précédente si elle n'a pas encore été révoquée
    if (currentObjectUrl) {
      URL.revokeObjectURL(currentObjectUrl);
      currentObjectUrl = null;
    }

    fetch(src, { credentials: "same-origin", signal: controller.signal })
      .then((res) => res.blob())
      .then((blob) => {
        const imgEl = image.getImage() as HTMLImageElement;
        const objectUrl = URL.createObjectURL(blob);
        currentObjectUrl = objectUrl;
        imgEl.onload = () => {
          URL.revokeObjectURL(objectUrl);
          currentObjectUrl = null;
        };
        imgEl.src = objectUrl;
        controller = null;
      })
      .catch((e) => {
        controller = null;
        if (e.name !== "AbortError") {
          (image.getImage() as HTMLImageElement).dispatchEvent(
            new Event(
              "Erreur lors de la récupération de la tuile : " + e.message,
            ),
          );
        }
      });
  };
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
