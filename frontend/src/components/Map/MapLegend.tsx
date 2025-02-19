import { getUid } from "ol";
import { forwardRef, ReactNode, useImperativeHandle, useState } from "react";
import { Badge, Collapse, Form, Image, ListGroup } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import FormRange from "react-bootstrap/FormRange";
import Row from "react-bootstrap/Row";
import AccordionCustom, { useAccordionState } from "../Accordion/Accordion.tsx";
import { IconMinus, IconPlus } from "../Icon/Icon.tsx";
import "./legend.css";

const MapLegend = forwardRef(
  (
    {
      layers,
      addOrRemoveLayer,
    }: { layers: any[]; addOrRemoveLayer: (layer: any) => void },
    ref,
  ) => {
    const [activeLayers, setActiveLayers] = useState([]);
    const [open, setOpen] = useState({});
    const { handleShowClose, activesKeys } = useAccordionState([false]); // Tous les volets fermés par défaut

    useImperativeHandle(ref, () => ({
      addActiveLayer(layerId) {
        setActiveLayers([...activeLayers, layerId]);
      },
      removeActiveLayer(layerId) {
        setActiveLayers(activeLayers.filter((l) => l !== layerId));
      },
    }));

    function isChecked(layer) {
      return activeLayers.some((l) => getUid(layer.openlayer) === l);
    }

    const listeVoletsAccordion: { header: string; content: ReactNode }[] = [];

    layers
      ?.filter((g) => g.layers.length > 0)
      .map((group) =>
        listeVoletsAccordion.push({
          header: group.libelle,
          content: (
            <ListGroup className="list-group-flush">
              {group.layers
                .sort((a, b) => {
                  return b.ordre - a.ordre;
                })
                .map((layer: any, layerIdx: number) => (
                  <ListGroup.Item
                    key={layerIdx}
                    className={isChecked(layer) ? "" : "noprint"}
                  >
                    <Row>
                      <Col className={"icone-legende"}>
                        {layer.icone && (
                          <Image src={layer.icone} rounded fluid />
                        )}
                      </Col>
                      <Col>
                        <Form.Group controlId={getUid(layer.openlayer)}>
                          <Row>
                            <Col>
                              <Form.Switch
                                checked={isChecked(layer)}
                                onClick={() => {
                                  addOrRemoveLayer(layer);
                                }}
                                label={
                                  <>
                                    {layer.libelle}
                                    {layer.legende && (
                                      <img src={layer.icone} height={24} />
                                    )}
                                  </>
                                }
                              />
                            </Col>
                            {layer.legende && (
                              <Col xs={"auto"}>
                                <Badge
                                  pill
                                  onClick={() =>
                                    setOpen({
                                      ...open,
                                      ...{
                                        [getUid(layer.openlayer)]:
                                          !open[getUid(layer.openlayer)],
                                      },
                                    })
                                  }
                                >
                                  {open[getUid(layer.openlayer)] ? (
                                    <IconMinus />
                                  ) : (
                                    <IconPlus />
                                  )}
                                </Badge>
                              </Col>
                            )}
                          </Row>
                        </Form.Group>
                        <FormRange
                          className={"noprint"}
                          disabled={!isChecked(layer)}
                          min={0}
                          max={1}
                          step={0.1}
                          defaultValue={layer.openlayer.getOpacity()}
                          onChange={(evt) => {
                            layer.openlayer.setOpacity(
                              parseFloat(evt.target.value),
                            );
                          }}
                        />
                      </Col>
                    </Row>
                    {layer.legende && (
                      <Row>
                        <Col>
                          <Collapse in={open[getUid(layer.openlayer)]}>
                            <Image src={layer.legende} rounded fluid />
                          </Collapse>
                        </Col>
                      </Row>
                    )}
                  </ListGroup.Item>
                ))}
            </ListGroup>
          ),
        }),
      );

    return (
      <AccordionCustom
        activesKeys={activesKeys}
        handleShowClose={handleShowClose}
        list={listeVoletsAccordion}
      />
    );
  },
);

MapLegend.displayName = "MapLegend";

export default MapLegend;
