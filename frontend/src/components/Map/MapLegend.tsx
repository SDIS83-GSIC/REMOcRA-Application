import { forwardRef, useImperativeHandle, useState } from "react";
import { Card, Form, ListGroup } from "react-bootstrap";
import FormRange from "react-bootstrap/FormRange";

const MapLegend = forwardRef(
  (
    {
      layers,
      addOrRemoveLayer,
    }: { layers: any[]; addOrRemoveLayer: (layer: any) => void },
    ref,
  ) => {
    const [activeLayers, setActiveLayers] = useState([]);

    useImperativeHandle(ref, () => ({
      addActiveLayer(layerId) {
        setActiveLayers([...activeLayers, layerId]);
      },
      removeActiveLayer(layerId) {
        setActiveLayers(activeLayers.filter((l) => l !== layerId));
      },
    }));

    return (
      <>
        {layers
          ?.filter((g) => g.layers.length > 0)
          .map((group: any, groupIdx: number) => (
            <Card key={groupIdx}>
              <Card.Header>{group.libelle}</Card.Header>
              <ListGroup className="list-group-flush">
                {group.layers.map((layer: any, layerIdx: number) => (
                  <ListGroup.Item key={layerIdx}>
                    <Form.Group controlId={layer.openlayer.ol_uid}>
                      <Form.Switch
                        checked={activeLayers.some(
                          (l) => layer.openlayer.ol_uid === l,
                        )}
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
                    </Form.Group>
                    <FormRange
                      disabled={
                        !activeLayers.some((l) => layer.openlayer.ol_uid === l)
                      }
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
                  </ListGroup.Item>
                ))}
              </ListGroup>
            </Card>
          ))}
      </>
    );
  },
);

MapLegend.displayName = "MapLegend";

export default MapLegend;
