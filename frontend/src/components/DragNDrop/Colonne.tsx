import { useDroppable } from "@dnd-kit/core";
import {
  SortableContext,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { Button, Col, Row } from "react-bootstrap";
import { IconClose } from "../Icon/Icon.tsx";
import SortableItem from "./SortableItemGrid.tsx";

export default function Colonne({
  id,
  items,
  setItems,
  keyPossibilites,
}: {
  id: string;
  items: any;
  setItems: (e: any) => void;
  keyPossibilites: string;
}) {
  const { setNodeRef } = useDroppable({
    id,
  });

  return (
    items[id] && (
      <Row className="m-2">
        <SortableContext
          id={id}
          items={items[id] ?? []}
          strategy={verticalListSortingStrategy}
        >
          <Row ref={setNodeRef} className="bg-light border p-2 rounded">
            {id !== keyPossibilites && (
              <Col xs="auto" className="ms-auto">
                <Button
                  variant="link"
                  className="text-danger "
                  onClick={() => {
                    const possibilites = items.possibilites;
                    items[id]?.forEach((element) => {
                      possibilites.push(element);
                    });
                    const itemsTemp = {
                      ...items,
                      possibilites: possibilites,
                    };
                    delete itemsTemp[id];
                    setItems({
                      ...itemsTemp,
                    });
                  }}
                >
                  <IconClose />
                </Button>
              </Col>
            )}
            {id === keyPossibilites
              ? items[id]?.map((id: string, index: number) => (
                  <Col key={index} xs={"auto"}>
                    <SortableItem key={id} id={id} />
                  </Col>
                ))
              : items[id]?.map((id) => <SortableItem key={id} id={id} />)}
          </Row>
        </SortableContext>
      </Row>
    )
  );
}
