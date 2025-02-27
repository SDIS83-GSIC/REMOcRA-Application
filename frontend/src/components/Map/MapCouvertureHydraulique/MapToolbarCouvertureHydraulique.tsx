import { Feature } from "ol";
import { shiftKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { Circle, Geometry, MultiLineString, Point } from "ol/geom";
import { DragBox, Draw, Select } from "ol/interaction";
import Map from "ol/Map";
import { Fill, Stroke, Style, Text } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { useMemo, useState } from "react";
import { Button } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import TraceeCouvertureForm from "../../../pages/CouvertureHydraulique/Etude/TraceeCouvertureForm.tsx";
import CreatePeiProjet from "../../../pages/CouvertureHydraulique/PeiProjet/CreatePeiProjet.tsx";
import { doFetch } from "../../Fetch/useFetch.tsx";
import {
  IconCreate,
  IconMoveObjet,
  IconPeiPlusProche,
  IconSelect,
} from "../../Icon/Icon.tsx";
import Volet from "../../Volet/Volet.tsx";
import toggleDeplacerPoint from "../MapUtils.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { TooltipMapEditPeiProjet } from "../TooltipsMap.tsx";

const drawStyle = new Style({
  fill: new Fill({
    color: "rgba(255, 255, 255, 0.2)",
  }),
  stroke: new Stroke({
    color: "rgba(0, 0, 0, 0.5)",
    lineDash: [10, 10],
    width: 2,
  }),
  image: new CircleStyle({
    radius: 5,
    stroke: new Stroke({
      color: "rgba(0, 0, 0, 0.7)",
    }),
    fill: new Fill({
      color: "rgba(255, 255, 255, 0.2)",
    }),
  }),
});

export const useToolbarCouvertureHydrauliqueContext = ({
  map,
  workingLayer,
  dataPeiLayer,
  dataPeiProjetLayer,
  etudeId,
  reseauImporte,
}) => {
  const { success: successToast, error: errorToast } = useToastContext();
  const [showCreatePeiProjet, setShowPeiProjet] = useState(false);
  const handleClosePeiProjet = () => setShowPeiProjet(false);
  const [pointPeiProjet, setPointPeiProjet] = useState<Point | null>(null);
  const [showTraceeCouverture, setShowTraceeCouverture] = useState(false);
  const handleCloseTraceeCouverture = () => setShowTraceeCouverture(false);
  const [listePeiId] = useState<string[]>([]);
  const [listePeiProjetId] = useState<string[]>([]);

  /**
   * Permet de dessiner un point pour la création des PEI en projet
   */
  async function calculCouverture() {
    if (listePeiId.length === 0 && listePeiProjetId.length === 0) {
      return;
    }

    // Si l'étude a un réseau importé alors on demande quel réseau utiliser.
    if (reseauImporte) {
      setShowTraceeCouverture(true);
    } else {
      (
        await fetch(
          url`/api/couverture-hydraulique/calcul/` + etudeId,
          getFetchOptions({
            method: "PUT",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
              listePeiId: listePeiId,
              listePeiProjetId: listePeiProjetId,
              useReseauImporte: false,
              useReseauImporteWithReseauCourant: false,
            }),
          }),
        )
      )
        .text()
        .then(() => {
          successToast("Couverture hydraulique tracée");
        })
        .catch((reason: string) => {
          errorToast(reason);
        });
    }
  }

  /**
   * Permet de dessiner un point pour la création des PEI en projet
   */
  async function clearCouverture() {
    (
      await fetch(
        url`/api/couverture-hydraulique/calcul/clear/` + etudeId,
        getFetchOptions({
          method: "DELETE",
          headers: { "Content-Type": "application/json" },
        }),
      )
    )
      .text()
      .then(() => {
        successToast("Couverture hydraulique effacée");
      })
      .catch((reason: string) => {
        errorToast(reason);
      });
  }

  const tools = useMemo(() => {
    if (!map) {
      return {};
    }

    const createPeiProjetCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return drawStyle;
        }
      },
    });
    createPeiProjetCtrl.on("drawstart", async () => {
      // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
      workingLayer.getSource().clear();
    });
    createPeiProjetCtrl.on("drawend", async (event) => {
      const geometry = event.feature.getGeometry();
      setPointPeiProjet(geometry);
      setShowPeiProjet(true);
    });

    /**
     * Permet de dessiner un point pour la création des PEI en projet
     */
    function toggleCreatePeiProjet(active = false) {
      const idx = map
        ?.getInteractions()
        .getArray()
        .indexOf(createPeiProjetCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(createPeiProjetCtrl);
        }
      } else {
        map.removeInteraction(createPeiProjetCtrl);
      }
    }

    const peiPlusProcheCtrl = new Draw({
      source: workingLayer.getSource(),
      type: "Point",
      style: (feature) => {
        const geometryType = feature.getGeometry().getType();
        if (geometryType === "Point") {
          return drawStyle;
        }
      },
    });
    peiPlusProcheCtrl.on("drawstart", async () => {
      // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
      workingLayer.getSource().clear();
    });
    peiPlusProcheCtrl.on("drawend", async (event) => {
      const geometry = event.feature.getGeometry();
      const result = await doFetch(
        url`/api/couverture-hydraulique/calcul/pei-plus-proche`,
        getFetchOptions({
          method: "POST",
          headers: { "Content-Type": "application/json" },
          body: JSON.stringify({
            longitude: geometry.getFlatCoordinates()[0],
            latitude: geometry.getFlatCoordinates()[1],
            srid: map.getView().getProjection().getCode().split(":")[1],
          }),
        }),
      );
      if (result?.chemin != null) {
        const wktChemin = result.chemin;
        const wktPei = result.peiGeometry;
        const distance = result.dist;

        // Affichage des features
        cheminPlusCourtFeaturePei(workingLayer, wktPei, distance);
        cheminPlusCourtFeatureChemin(workingLayer, wktChemin);
        cheminPlusCourtFeatureClic(workingLayer, geometry);
      } else {
        errorToast("Aucun PEI n'a été trouvé.");
      }
    });

    /**
     * Permet de dessiner un point pour la création des PEI en projet
     */
    function togglePeiPlusProche(active = false) {
      const idx = map?.getInteractions().getArray().indexOf(peiPlusProcheCtrl);
      if (active) {
        if (idx === -1) {
          map.addInteraction(peiPlusProcheCtrl);
        }
      } else {
        map.removeInteraction(peiPlusProcheCtrl);
      }
    }

    /**
     * Permet de dessiner un rond rouge sur le PEI le plus proche
     */
    function cheminPlusCourtFeaturePei(
      workingLayer: any,
      wktPei: Geometry,
      distance: number,
    ) {
      if (wktPei == null) {
        return;
      }
      const featurePei = new WKT().readFeature(wktPei.split(";")[1]);

      const circle = new Feature(
        new Circle(featurePei.get("geometry").flatCoordinates),
      );

      circle.setStyle(
        new Style({
          geometry: new Point(featurePei.get("geometry").flatCoordinates),
          image: new CircleStyle({
            radius: 12,
            stroke: new Stroke({
              color: "#f01f18",
              width: 3,
            }),
          }),
          text: new Text({
            font: "16px Calibri,sans-serif",
            overflow: true,
            fill: new Fill({
              color: "white",
            }),
            text: Math.round(distance) + " m",
            offsetY: -20,
            stroke: new Stroke({ color: "black", width: 2 }),
          }),
        }),
      );

      workingLayer.getSource().addFeature(circle);
    }

    /**
     * Permet de dessiner le chemin entre le clic et le PEI le plus proche
     */
    function cheminPlusCourtFeatureChemin(
      workingLayer: any,
      wktChemin: Geometry,
    ) {
      const featureChemin = new WKT().readFeature(wktChemin);
      const path = new Feature({
        geometry: new MultiLineString(
          featureChemin.getGeometry().getCoordinates(),
        ),
        name: "chemin",
      });

      path.setStyle(
        new Style({
          stroke: new Stroke({
            color: "#f01f18",
            width: 3,
          }),
        }),
      );

      workingLayer.getSource().addFeature(path);
    }

    /**
     * Permet de dessiner un rond à l'endroit du clic de l'utilisateur
     */
    function cheminPlusCourtFeatureClic(
      workingLayer: any,
      coordsClic: Geometry,
    ) {
      const pointClic = new Feature(coordsClic);
      pointClic.setStyle(
        new Style({
          geometry: coordsClic,
          image: new CircleStyle({
            radius: 6,
            stroke: new Stroke({
              color: "black",
              width: 2,
            }),
            fill: new Fill({
              color: "white",
            }),
          }),
        }),
      );

      workingLayer.getSource().addFeature(pointClic);
    }

    const selectCtrl = new Select({
      style: new Style({
        image: new CircleStyle({
          radius: 16,
          stroke: new Stroke({
            color: "rgba(255, 0, 0, 0.7)",
            width: 4,
          }),
        }),
      }),
      hitTolerance: 4,
    });
    const dragBoxCtrl = new DragBox({
      style: new Style({
        stroke: new Stroke({
          color: [0, 0, 255, 1],
        }),
      }),
      minArea: 25,
    });
    dragBoxCtrl.on("boxend", function (e) {
      if (!shiftKeyOnly(e.mapBrowserEvent)) {
        selectCtrl.getFeatures().clear();
      }
      const boxExtent = dragBoxCtrl.getGeometry().getExtent();
      const boxFeatures = dataPeiLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeatures);
      const boxFeaturesPeiProjet = dataPeiProjetLayer
        .getSource()
        .getFeaturesInExtent(boxExtent);

      selectCtrl.getFeatures().extend(boxFeaturesPeiProjet);

      listePeiId.splice(0, listePeiId.length);
      listePeiProjetId.splice(0, listePeiProjetId.length);

      selectCtrl.getFeatures().forEach((e) => {
        const point = e.getProperties();
        if (point.typePointCarte === "PEI") {
          listePeiId.push(point.pointId);
        }
        if (point.typePointCarte === "PEI_PROJET") {
          listePeiProjetId.push(point.pointId);
        }
      });
    });

    function toggleSelect(active = false) {
      const idx1 = map?.getInteractions().getArray().indexOf(selectCtrl);
      const idx2 = map?.getInteractions().getArray().indexOf(dragBoxCtrl);
      if (active) {
        if (idx1 === -1 && idx2 === -1) {
          map.addInteraction(selectCtrl);
          map.addInteraction(dragBoxCtrl);
        }
      } else {
        listePeiId.splice(0, listePeiId.length);
        listePeiProjetId.splice(0, listePeiProjetId.length);
        map.removeInteraction(selectCtrl);
        map.removeInteraction(dragBoxCtrl);
      }
    }

    const selectProjetCtrl = new Select();

    function toggleDeplacerPeiProjet(active = false) {
      toggleDeplacerPoint(
        active,
        selectProjetCtrl,
        map,
        `/api/couverture-hydraulique/pei-projet/move/`,
        dataPeiProjetLayer,
        successToast,
        errorToast,
        (feature) => feature.getProperties().typePointCarte === "PEI_PROJET",
      );
    }

    const tools = {
      "create-pei-projet": {
        action: toggleCreatePeiProjet,
      },
      "select-etude": {
        action: toggleSelect,
      },
      "pei-plus-proche": {
        action: togglePeiPlusProche,
      },
      "deplacer-pei-projet": {
        action: toggleDeplacerPeiProjet,
      },
    };

    return tools;
  }, [map]);

  return {
    tools,
    calculCouverture,
    clearCouverture,
    handleClosePeiProjet,
    showCreatePeiProjet,
    pointPeiProjet,
    handleCloseTraceeCouverture,
    showTraceeCouverture,
    listePeiId,
    listePeiProjetId,
  };
};

const MapToolbarCouvertureHydraulique = ({
  map,
  dataPeiProjetLayer,
  etudeId,
  disabledEditPeiProjet,
  calculCouverture,
  clearCouverture,
  handleClosePeiProjet,
  showCreatePeiProjet,
  pointPeiProjet,
  handleCloseTraceeCouverture,
  showTraceeCouverture,
  listePeiId,
  listePeiProjetId,
  toggleTool: toggleToolCallback,
  activeTool,
}: {
  map?: Map;
  dataPeiLayer: any;
  dataPeiProjetLayer: any;
  workingLayer: any;
  etudeId: string;
  disabledEditPeiProjet: boolean;
  calculCouverture: () => void;
  clearCouverture: () => void;
  handleClosePeiProjet: () => void;
  showCreatePeiProjet: () => void;
  pointPeiProjet: string[];
  handleCloseTraceeCouverture: () => void;
  showTraceeCouverture: () => void;
  listePeiId: string[];
  listePeiProjetId: string[];
  toggleTool: (toolId: string) => void;
  activeTool: string;
}) => {
  return (
    <>
      {/**Pour la couverture hydraulique */}
      <ToolbarButton
        toolName={"select-etude"}
        toolIcon={<IconSelect />}
        toolLabelTooltip={"Sélectionner"}
        toggleTool={toggleToolCallback}
        activeTool={activeTool}
      />
      <ToolbarButton
        toolName={"create-pei-projet"}
        toolIcon={<IconCreate />}
        toolLabelTooltip={"Créer un PEI en projet"}
        toggleTool={toggleToolCallback}
        activeTool={activeTool}
        disabled={disabledEditPeiProjet}
      />
      <ToolbarButton
        toolName={"deplacer-pei-projet"}
        toolIcon={<IconMoveObjet />}
        toolLabelTooltip={"Déplacer un PEI en projet"}
        toggleTool={toggleToolCallback}
        activeTool={activeTool}
        disabled={disabledEditPeiProjet}
      />
      <ToolbarButton
        toolName={"pei-plus-proche"}
        toolIcon={<IconPeiPlusProche />}
        toolLabelTooltip={"Trouver le PEI le plus proche"}
        toggleTool={toggleToolCallback}
        activeTool={activeTool}
        disabled={disabledEditPeiProjet}
      />
      <Button
        className="me-1"
        variant="outline-primary"
        onClick={calculCouverture}
        disabled={disabledEditPeiProjet}
      >
        Lancer une simulation
      </Button>
      <Button
        variant="outline-primary"
        onClick={clearCouverture}
        disabled={disabledEditPeiProjet}
      >
        Effacer la couverture tracée
      </Button>
      <Volet
        handleClose={handleClosePeiProjet}
        show={showCreatePeiProjet}
        className="w-auto"
      >
        <CreatePeiProjet
          coordonneeX={pointPeiProjet?.getFlatCoordinates()[0]}
          coordonneeY={pointPeiProjet?.getFlatCoordinates()[1]}
          srid={map.getView().getProjection().getCode().split(":").pop()}
          etudeId={etudeId}
          onSubmit={() => {
            dataPeiProjetLayer.getSource().refresh();
            handleClosePeiProjet();
          }}
        />
      </Volet>
      <Volet
        handleClose={handleCloseTraceeCouverture}
        show={showTraceeCouverture}
        className="w-auto"
      >
        <TraceeCouvertureForm
          etudeId={etudeId}
          listePeiId={listePeiId}
          listePeiProjetId={listePeiProjetId}
          closeVolet={handleCloseTraceeCouverture}
        />
      </Volet>
      <TooltipMapEditPeiProjet
        etudeId={etudeId}
        map={map}
        disabledEditPeiProjet={disabledEditPeiProjet}
        dataPeiProjetLayer={dataPeiProjetLayer}
        disabled={activeTool === "deplacer-pei-projet"}
      />
    </>
  );
};

MapToolbarCouvertureHydraulique.displayName = "MapToolbarCouvertureHydraulique";

export default MapToolbarCouvertureHydraulique;
