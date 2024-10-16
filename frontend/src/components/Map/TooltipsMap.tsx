import { Feature, Map, Overlay } from "ol";
import { useEffect, useRef, useState } from "react";
import { Button, Col, Popover, Row } from "react-bootstrap";
import { IconClose } from "../Icon/Icon.tsx";

/**
 * Permet d'afficher une tooltip sur la carte lorsque l'utilisateur clique sur un point
 * @param map : la carte
 * @returns la tooltip
 */
const TooltipMap = ({ map }: { map: Map }) => {
  const [featureSelect, setFeatureSelect] = useState<Feature | null>(null);
  const ref = useRef(null);
  const [overlay, setOverlay] = useState<Overlay | undefined>(
    new Overlay({
      positioning: "bottom-center",
      stopEvent: false,
    }),
  );

  useEffect(() => {
    if (map && ref.current != null) {
      map.on("singleclick", (event) => {
        const pixel = map.getEventPixel(event.originalEvent);

        map.forEachFeatureAtPixel(pixel, function (feature) {
          const coordinate = event.coordinate;
          setFeatureSelect(feature);

          const over = new Overlay({
            element: ref.current,
            positioning: "bottom-center",
            position: coordinate,
            stopEvent: false,
          });

          map.addOverlay(over);
          setOverlay(over);
        });
      });
    }
  }, [map]);

  return (
    <div ref={ref}>
      {featureSelect && (
        <Popover
          id="popover"
          placement="bottom"
          arrowProps={{
            style: {
              display: "none",
            },
          }}
        >
          <Popover.Header>
            <Row>
              <Col>Information PEI</Col>
              <Col className="ms-auto" xs={"auto"}>
                <Button
                  variant="link"
                  onClick={() => overlay?.setPosition(undefined)}
                >
                  <IconClose />
                </Button>
              </Col>
            </Row>
          </Popover.Header>
          <Popover.Body>
            {Object.entries(featureSelect.getProperties()).map(
              ([key, value]) =>
                key !== "geometry" && (
                  <div key={key}>
                    <span className="fw-bold">{key}</span> : {value}{" "}
                  </div>
                ),
            )}
          </Popover.Body>
        </Popover>
      )}
    </div>
  );
};

export default TooltipMap;
