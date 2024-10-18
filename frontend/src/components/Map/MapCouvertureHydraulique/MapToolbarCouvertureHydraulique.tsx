import { Feature } from "ol";
import { shiftKeyOnly } from "ol/events/condition";
import { WKT } from "ol/format";
import { Circle, Geometry, MultiLineString, Point } from "ol/geom";
import { DragBox, Draw, Select } from "ol/interaction";
import Map from "ol/Map";
import { Fill, Stroke, Style, Text } from "ol/style";
import CircleStyle from "ol/style/Circle";
import { forwardRef, useState } from "react";
import { Button, ToggleButton } from "react-bootstrap";
import url, { getFetchOptions } from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import TraceeCouvertureForm from "../../../pages/CouvertureHydraulique/Etude/TraceeCouvertureForm.tsx";
import CreatePeiProjet from "../../../pages/CouvertureHydraulique/PeiProjet/CreatePeiProjet.tsx";
import { doFetch } from "../../Fetch/useFetch.tsx";
import Volet from "../../Volet/Volet.tsx";
import ToolbarButton from "../ToolbarButton.tsx";
import { TooltipMapEditPeiProjet } from "../TooltipsMap.tsx";

const MapToolbarCouvertureHydraulique = forwardRef(
  ({
    map,
    dataPeiLayer,
    dataPeiProjetLayer,
    workingLayer,
    etudeId,
    reseauImporte,
    disabledEditPeiProjet,
  }: {
    map: Map;
    dataPeiLayer: any;
    dataPeiProjetLayer: any;
    workingLayer: any;
    etudeId: string;
    reseauImporte: boolean;
    disabledEditPeiProjet: boolean;
  }) => {
    const { success: successToast, error: errorToast } = useToastContext();

    const [showCreatePeiProjet, setShowPeiProjet] = useState(false);
    const handleClosePeiProjet = () => setShowPeiProjet(false);
    const [pointPeiProjet, setPointPeiProjet] = useState<Point | null>(null);

    const [showTraceeCouverture, setShowTraceeCouverture] = useState(false);
    const handleCloseTraceeCouverture = () => setShowTraceeCouverture(false);
    const [listePeiId, setListePeiId] = useState<string[]>([]);
    const [listePeiProjetId, setListePeiProjetId] = useState<string[]>([]);

    const [activeTool, setActiveTool] = useState<string>();

    const measureStyle = new Style({
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

    function toggleSelect(active = false) {
      const selectCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof Select)[0];
      const dragBoxCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof DragBox)[0];
      if (active) {
        if (!selectCtrl) {
          const select = new Select({});
          const dragBox = new DragBox({
            style: new Style({
              stroke: new Stroke({
                color: [0, 0, 255, 1],
              }),
            }),
            minArea: 25,
          });
          dragBox.on("boxend", function (e) {
            if (!shiftKeyOnly(e.mapBrowserEvent)) {
              select.getFeatures().clear();
            }
            const boxExtent = dragBox.getGeometry().getExtent();
            const boxFeatures = dataPeiLayer
              .getSource()
              .getFeaturesInExtent(boxExtent);

            select.getFeatures().extend(boxFeatures);
            const boxFeaturesPeiProjet = dataPeiProjetLayer
              .getSource()
              .getFeaturesInExtent(boxExtent);

            select.getFeatures().extend(boxFeaturesPeiProjet);
          });

          map.addInteraction(select);
          map.addInteraction(dragBox);
        }
      } else {
        if (selectCtrl) {
          map.removeInteraction(selectCtrl);
        }
        if (dragBoxCtrl) {
          map.removeInteraction(dragBoxCtrl);
        }
      }
    }

    /**
     * Permet de dessiner un point pour la création des PEI en projet
     */
    function toggleCreatePeiProjet(active = false) {
      const createCtrl2 = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof Draw)[0];
      if (active) {
        if (!createCtrl2) {
          const draw = new Draw({
            source: workingLayer.getSource(),
            type: "Point",
            style: (feature) => {
              const geometryType = feature.getGeometry().getType();
              if (geometryType === "Point") {
                return measureStyle;
              }
            },
          });
          draw.on("drawstart", async () => {
            // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
            workingLayer.getSource().clear();
          });
          draw.on("drawend", async (event) => {
            const geometry = event.feature.getGeometry();
            setPointPeiProjet(geometry);
            setShowPeiProjet(true);
          });
          map.addInteraction(draw);
        }
      }
    }

    /**
     * Permet de dessiner un point pour la création des PEI en projet
     */
    function togglePeiPlusProche(active = false) {
      const createCtrl = map
        ?.getInteractions()
        .getArray()
        .filter((c) => c instanceof Draw)[0];
      if (active) {
        if (!createCtrl) {
          const draw = new Draw({
            source: workingLayer.getSource(),
            type: "Point",
            style: (feature) => {
              const geometryType = feature.getGeometry().getType();
              if (geometryType === "Point") {
                return measureStyle;
              }
            },
          });
          draw.on("drawstart", async () => {
            // Avant de redessiner un point, on supprime les autres points, le but est d'avoir juste un seul point à la fois.
            workingLayer.getSource().clear();
          });
          draw.on("drawend", async (event) => {
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
          map.addInteraction(draw);
        }
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

    /**
     * Permet de dessiner un point pour la création des PEI en projet
     */
    async function calculCouverture() {
      // On va chercher les identifiants des PEI sélectionnées
      const listePeiId: string[] = [];
      const listePeiProjetId: string[] = [];
      map
        .getInteractions()
        .getArray()
        .find((e) => e instanceof Select)
        ?.getFeatures()
        .forEach((e) => {
          const point = e.getProperties();
          if (point.typePointCarte === "PEI_PROJET") {
            listePeiProjetId.push(point.pointId);
          }
          if (point.typePointCarte === "PEI") {
            listePeiId.push(point.pointId);
          }
        });

      if (listePeiId.length === 0 && listePeiProjetId.length === 0) {
        return;
      }

      setListePeiId(listePeiId);
      setListePeiProjetId(listePeiProjetId);

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

    const tools = {
      create: {
        action: toggleCreatePeiProjet,
      },
      select: {
        action: toggleSelect,
      },
      peiPlusProche: {
        action: togglePeiPlusProche,
      },
    };

    function toggleTool(toolId) {
      let newTool = null;
      if (activeTool === toolId) {
        setActiveTool(null);
      } else {
        setActiveTool(toolId);
        newTool = toolId;
      }
      for (const property in tools) {
        tools[property].action(property === newTool);
      }
    }

    return (
      <>
        {/**Pour la couverture hydraulique */}
        <ToggleButton
          name={"tool"}
          onClick={() => toggleTool("select")}
          id={"select"}
          value={"select"}
          type={"radio"}
          variant={"outline-primary"}
          checked={activeTool === "select"}
        >
          Sélectionner
        </ToggleButton>
        <ToolbarButton
          toolName={"create"}
          toolLabel={"Créer un PEI en projet"}
          toggleTool={toggleTool}
          activeTool={activeTool}
          disabled={disabledEditPeiProjet}
        />
        <ToolbarButton
          toolName={"peiPlusProche"}
          toolLabel={"Trouver le PEI le plus proche"}
          toggleTool={toggleTool}
          activeTool={activeTool}
          disabled={disabledEditPeiProjet}
        />
        <Button
          variant="outline-primary"
          onClick={() => calculCouverture()}
          disabled={disabledEditPeiProjet}
        >
          Lancer une simulation
        </Button>
        <Button
          variant="outline-primary"
          onClick={() => clearCouverture()}
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
        />
      </>
    );
  },
);

MapToolbarCouvertureHydraulique.displayName = "MapToolbarCouvertureHydraulique";

export default MapToolbarCouvertureHydraulique;
