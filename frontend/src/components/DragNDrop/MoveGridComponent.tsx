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
import { sortableKeyboardCoordinates } from "@dnd-kit/sortable";
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
    if (!over) {
      return;
    }

    const activeId = active.id;
    const overId = over.id;

    // Trouver les colonnes source et destination
    const activeContainer = findColonne(activeId);
    const overContainer = findColonne(overId);

    if (!activeContainer || !overContainer) {
      return;
    }

    setItems((prev) => {
      const activeItems = prev[activeContainer];
      const overItems = prev[overContainer];

      const activeIndex = activeItems.indexOf(activeId);
      const overIndex = overItems.indexOf(overId);

      // Vérification de sécurité
      if (activeIndex === -1) {
        return prev;
      }

      let newIndex;
      // Ajout en fin de colonne si elle est vide
      if (overId in prev) {
        newIndex = overItems.length;
      } else {
        const isBelowLastItem =
          overIndex === overItems.length - 1 &&
          draggingRect?.offsetTop > over.rect?.offsetTop + over.rect?.height;

        newIndex =
          overIndex >= 0
            ? overIndex + (isBelowLastItem ? 1 : 0)
            : overItems.length;
      }

      // Vérifier si l'élément est déjà dans la colonne de destination et l'enlever avant de le réinsérer
      const filteredOverItems = overItems.filter((item) => item !== activeId);

      // Ajouter l'élément déplacé à la nouvelle position
      const updatedOverItems = [
        ...filteredOverItems.slice(0, newIndex),
        activeId,
        ...filteredOverItems.slice(newIndex),
      ];

      // Supprimer l'élément de la colonne d'origine uniquement si elle est différente sinon utiliser la liste mise à jour
      const updatedActiveItems =
        activeContainer === overContainer
          ? updatedOverItems
          : activeItems.filter((item) => item !== activeId);

      // Mise à jour des possibilités si besoin
      if (activeContainer === keyPossibilites) {
        setPossibilites(updatedActiveItems);
      }
      if (overContainer === keyPossibilites) {
        setPossibilites(updatedOverItems);
      }

      return {
        ...prev,
        [activeContainer]: updatedActiveItems,
        [overContainer]: updatedOverItems,
      };
    });
  }

  function handleDragEnd(event) {
    const { active, over } = event;
    const { id } = active;
    const { id: overId } = over;

    const activeContainer = findColonne(id);
    const overContainer = findColonne(overId);
    if (!activeContainer || !overContainer) {
      return;
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
