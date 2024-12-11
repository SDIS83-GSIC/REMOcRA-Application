import React from "react";
import {
  DndContext,
  closestCenter,
  useDraggable,
  useDroppable,
} from "@dnd-kit/core";
import {
  arrayMove,
  SortableContext,
  verticalListSortingStrategy,
  useSortable,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { Col, Row } from "react-bootstrap";
import { FormLabel } from "./Form.tsx";

const TransferList = ({
  availableOptions,
  selectedOptions,
  setAvailableOptions,
  setSelectedOptions,
  required = false,
  label,
  tooltipText,
  name,
}: TransferListType) => {
  const handleDragEnd = (event) => {
    const { active, over } = event;

    // Si aucun élément n'est relâché, on ne fait rien
    if (!over) {
      return;
    }

    // Trouve l'élément actif dans l'une des deux listes
    const activeItem =
      availableOptions.find((item) => item.id === active.id) ||
      selectedOptions.find((item) => item.id === active.id);

    if (!activeItem) {
      return;
    } // Sécurité si active.id ne correspond à aucun élément

    // Déplacement entre les listes
    if (
      over.id === "available" &&
      selectedOptions.some((item) => item.id === active.id)
    ) {
      // Déplacer de `selectedOptions` vers `availableOptions`
      setSelectedOptions((prev) =>
        prev.filter((item) => item.id !== active.id),
      );
      setAvailableOptions((prev) => [...prev, activeItem]);
    } else if (
      (over.id === "selected" ||
        selectedOptions.some((item) => item.id === over.id)) &&
      availableOptions.some((item) => item.id === active.id)
    ) {
      // Déplacer de `availableOptions` vers `selectedOptions`
      setAvailableOptions((prev) =>
        prev.filter((item) => item.id !== active.id),
      );
      setSelectedOptions((prev) => [...prev, activeItem]);
    }
    // Réorganisation dans `selectedOptions`
    else if (
      over.id !== "available" &&
      selectedOptions.some((item) => item.id === active.id)
    ) {
      const oldIndex = selectedOptions.findIndex(
        (item) => item.id === active.id,
      );
      const newIndex = selectedOptions.findIndex((item) => item.id === over.id);

      setSelectedOptions((items) => arrayMove(items, oldIndex, newIndex));
    }
  };

  return (
    <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
      <FormLabel
        name={name}
        label={label}
        required={required}
        tooltipText={tooltipText}
      />
      <Row className={"bg-secondary p-3"}>
        {/* Liste des options disponibles */}
        <Col
          className={"border border-2 border-primary-subtle rounded-2 m-2 p-2"}
        >
          <DroppableList
            id="available"
            items={availableOptions}
            title="Options disponibles"
          />
        </Col>
        {/* Liste des options sélectionnées */}
        <Col
          className={"border border-2 rounded-2 border-primary-subtle m-2 p-2"}
        >
          <SortableList
            id="selected"
            items={selectedOptions}
            title="Options sélectionnées"
          />
        </Col>
      </Row>
    </DndContext>
  );
};

const DroppableList = ({ id, items, title }: DroppableSortType) => {
  const { setNodeRef } = useDroppable({ id });

  return (
    <>
      <Col xs={12} className={"text-center m-2 h5"}>
        {title}
      </Col>
      <Col xs={12} ref={setNodeRef} className="list-group">
        {items.map((item) => (
          <DraggableItem key={item} item={item} />
        ))}
      </Col>
    </>
  );
};
const DraggableItem = ({ item }: { item: ItemType }) => {
  const { attributes, listeners, setNodeRef, transform, transition } =
    useDraggable({ id: item.id });

  const style = {
    transform: CSS.Translate.toString(transform),
    transition,
    cursor: "grab",
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className="list-group-item d-flex justify-content-between align-items-center"
    >
      {item.libelle}
    </div>
  );
};

const SortableList = ({ id, items, title }: DroppableSortType) => {
  const { setNodeRef } = useDroppable({ id });

  return (
    <>
      <Col xs={12} className={"text-center m-2 h5"}>
        {title}
      </Col>
      <Col ref={setNodeRef} className="list-group">
        <SortableContext
          items={items.map((item) => item.id)}
          strategy={verticalListSortingStrategy}
        >
          {items.map((item) => (
            <SortableItem key={item.id} item={item} />
          ))}
        </SortableContext>
      </Col>
    </>
  );
};

const SortableItem = ({ item }: { item: ItemType }) => {
  const { attributes, listeners, setNodeRef, transform, transition } =
    useSortable({ id: item.id });

  const style = {
    transform: CSS.Transform.toString(transform),
    transition,
    cursor: "grab",
  };

  return (
    <div
      ref={setNodeRef}
      style={style}
      {...listeners}
      {...attributes}
      className="list-group-item d-flex justify-content-between align-items-center"
    >
      {item.libelle}
    </div>
  );
};

type TransferListType = {
  availableOptions: ItemType[];
  selectedOptions: ItemType[];
  setAvailableOptions: any;
  setSelectedOptions: any;
  required?: boolean;
  label: string;
  tooltipText?: string;
  name: string;
};

type ItemType = {
  id: string;
  libelle: string;
};

type DroppableSortType = {
  id: string;
  items: ItemType[];
  title: string;
};
export default TransferList;
