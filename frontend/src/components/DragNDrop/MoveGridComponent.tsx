import {
  closestCorners,
  defaultAnnouncements,
  DndContext,
  DragOverlay,
  KeyboardSensor,
  PointerSensor,
  useSensor,
  useSensors,
} from "@dnd-kit/core";
import { arrayMove, sortableKeyboardCoordinates } from "@dnd-kit/sortable";
import { useState } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { IconCreate } from "../Icon/Icon.tsx";
import Colonne from "./Colonne.tsx";
import { Item } from "./SortableItemGrid.tsx";

/**
 * Doit recevoir une liste d'items sous forme de :
 * {
 *  possibilites: ["1", "2", "3"],
 *  colonne1: ["4", "5"]
 *  colonne2: ["3"]
 *  ...
 * }
 * @returns
 */
const MoveGridComponent = ({
  items,
  setItems,
  setPossibilites,
}: {
  items: any;
  setItems: (e: any) => void;
  setPossibilites: (e: any) => void;
}) => {
  const keyPossibilites = "possibilites";
  // Permet de récupérer le colonne associée
  function findColonne(id: string) {
    if (id in items) {
      return id;
    }

    return Object.keys(items).find((key) => items[key].includes(id));
  }

  function handleDragStart(event) {
    const { active } = event;
    const { id } = active;

    setActiveId(id);
  }

  function handleDragOver(event) {
    const { active, over, draggingRect } = event;
    const { id } = active;
    const { id: overId } = over;

    // On récupére la colonne de l'item actif et la colonne de destination
    const activeContainer = findColonne(id);
    const overContainer = findColonne(overId);

    if (
      !activeContainer ||
      !overContainer ||
      activeContainer === overContainer
    ) {
      return;
    }

    setItems((prev) => {
      const activeItems = prev[activeContainer];
      const overItems = prev[overContainer];

      const activeIndex = activeItems.indexOf(id);
      const overIndex = overItems.indexOf(overId);

      let newIndex;
      if (overId in prev) {
        newIndex = overItems.length + 1;
      } else {
        const isBelowLastItem =
          over &&
          overIndex === overItems.length - 1 &&
          draggingRect?.offsetTop > over.rect?.offsetTop + over.rect?.height;

        const modifier = isBelowLastItem ? 1 : 0;

        newIndex = overIndex >= 0 ? overIndex + modifier : overItems.length + 1;
      }

      return {
        ...prev,
        [activeContainer]: [
          ...prev[activeContainer].filter((item) => item !== active.id),
        ],
        [overContainer]: [
          ...prev[overContainer].slice(0, newIndex),
          items[activeContainer][activeIndex],
          ...prev[overContainer].slice(newIndex, prev[overContainer].length),
        ],
      };
    });

    if (activeContainer === keyPossibilites) {
      setPossibilites(
        items[activeContainer].filter((item) => item !== active.id),
      );
    }

    if (overContainer === keyPossibilites) {
      const tabPossibilites = items[overContainer];
      tabPossibilites.push(active.id);
      setPossibilites(tabPossibilites);
    }
  }

  function handleDragEnd(event) {
    const { active, over } = event;
    const { id } = active;
    const { id: overId } = over;

    const activeContainer = findColonne(id);
    const overContainer = findColonne(overId);
    if (
      !activeContainer ||
      !overContainer ||
      activeContainer !== overContainer
    ) {
      return;
    }

    const activeIndex = items[activeContainer].indexOf(active.id);
    const overIndex = items[overContainer].indexOf(overId);

    if (activeIndex !== overIndex) {
      setItems((items) => ({
        ...items,
        [overContainer]: arrayMove(
          items[overContainer],
          activeIndex,
          overIndex,
        ),
      }));
    }

    setActiveId(null);
  }

  const [activeId, setActiveId] = useState();

  const sensors = useSensors(
    useSensor(PointerSensor),
    useSensor(KeyboardSensor, {
      coordinateGetter: sortableKeyboardCoordinates,
    }),
  );

  return (
    <DndContext
      announcements={defaultAnnouncements}
      sensors={sensors}
      collisionDetection={closestCorners}
      onDragStart={handleDragStart}
      onDragOver={handleDragOver}
      onDragEnd={handleDragEnd}
    >
      <h5>2.1 - Blocs à placer</h5>
      <>
        <Row>
          <Col>
            {items.possibilites.length > 0 ? (
              <Colonne
                id={keyPossibilites}
                items={items}
                setItems={setItems}
                keyPossibilites={keyPossibilites}
              />
            ) : (
              <div className="bg-light border p-2 rounded">
                Aucun bloc à placer
              </div>
            )}
          </Col>
        </Row>
      </>
      <Row className="mt-3">
        <h5 className="mt-2">2.2 - Emplacement</h5>
        <Col xs="auto" className="ms-auto">
          <Button
            variant="link"
            onClick={() => {
              const random = Math.random();
              const newColonne = {};
              newColonne["colonne" + random] = [];

              setItems({
                ...items,
                ...newColonne,
              });
            }}
          >
            <IconCreate /> Ajouter une colonne
          </Button>
        </Col>
      </Row>
      <Row>
        {Object.entries(items).map(([key]) => {
          return (
            key !== keyPossibilites && (
              <Col>
                <Colonne
                  id={key}
                  items={items}
                  setItems={setItems}
                  keyPossibilites={keyPossibilites}
                />
              </Col>
            )
          );
        })}

        <DragOverlay>{activeId ? <Item id={activeId} /> : null}</DragOverlay>
      </Row>
    </DndContext>
  );
};

export default MoveGridComponent;
