import { closestCenter, DndContext, useDroppable } from "@dnd-kit/core";
import {
  arrayMove,
  SortableContext,
  useSortable,
  verticalListSortingStrategy,
} from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
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
  const handleDragEnd = (event: any) => {
    const { active, over } = event;

    // Si aucun élément n'est relâché, on ne fait rien
    if (!over) {
      return;
    }

    // Trouve l'élément actif dans l'une des deux listes
    const activeItem =
      availableOptions?.find((item) => item.id === active.id) ||
      selectedOptions?.find((item) => item.id === active.id);

    if (!activeItem) {
      return;
    } // Sécurité si active.id ne correspond à aucun élément

    // Déplacement entre les listes
    if (
      over.id === "available" ||
      (availableOptions?.some((item) => item.id === over.id) &&
        selectedOptions?.some((item) => item.id === active.id))
    ) {
      // Déplacer de `selectedOptions` vers `availableOptions`
      setSelectedOptions((prev: ItemType[]) =>
        prev.filter((item: ItemType) => item.id !== active.id),
      );
      setAvailableOptions((prev: ItemType[]) => [...prev, activeItem]);
    } else if (
      (over.id === "selected" ||
        selectedOptions?.some((item) => item.id === over.id)) &&
      availableOptions?.some((item) => item.id === active.id)
    ) {
      // Déplacer de `availableOptions` vers `selectedOptions`
      setAvailableOptions((prev: ItemType[]) =>
        prev.filter((item: ItemType) => item.id !== active.id),
      );
      setSelectedOptions((prev: ItemType[]) => [...prev, activeItem]);
    }
    // Réorganisation dans `selectedOptions`
    else if (
      over.id !== "available" &&
      selectedOptions?.some((item) => item.id === active.id)
    ) {
      const oldIndex = selectedOptions.findIndex(
        (item) => item.id === active.id,
      );
      const newIndex = selectedOptions.findIndex((item) => item.id === over.id);

      setSelectedOptions((items: ItemType[]) =>
        arrayMove(items, oldIndex, newIndex),
      );
    }
  };

  return (
    availableOptions &&
    selectedOptions && (
      <DndContext collisionDetection={closestCenter} onDragEnd={handleDragEnd}>
        <FormLabel
          name={name}
          label={label}
          required={required}
          tooltipText={tooltipText}
        />
        <Row className={"bg-secondary p-3"}>
          <SortableContext
            items={availableOptions
              .concat(selectedOptions)
              .map((item) => item.id)}
            strategy={verticalListSortingStrategy}
          >
            {/* Liste des options disponibles */}
            <Col
              className={
                "border border-2 border-primary-subtle rounded-2 m-2 p-2"
              }
            >
              <DroppableList
                id="available"
                items={availableOptions}
                title="Options disponibles"
              />
            </Col>
            {/* Liste des options sélectionnées */}
            <Col
              className={
                "border border-2 rounded-2 border-primary-subtle m-2 p-2"
              }
            >
              <DroppableList
                id="selected"
                items={selectedOptions}
                title="Options sélectionnées"
              />
            </Col>
          </SortableContext>
        </Row>
      </DndContext>
    )
  );
};

const DroppableList = ({ id, items, title }: DroppableSortType) => {
  const { setNodeRef } = useDroppable({ id });

  return (
    items && (
      <>
        <Col xs={12} className={"text-center m-2 h5"}>
          {title}
        </Col>
        <Col xs={12} ref={setNodeRef} className="list-group">
          {items.map((item: ItemType) => (
            <DraggableItem key={item.id} item={item} />
          ))}
        </Col>
      </>
    )
  );
};
const DraggableItem = ({ item }: { item: ItemType }) => {
  // Gestion du tri et du drag
  const { attributes, listeners, setNodeRef, transform, transition } =
    useSortable({ id: item.id });

  // Gestion du drop (pour accepter des éléments)
  const { setNodeRef: setDroppableRef } = useDroppable({
    id: item.id,
  });

  // Fusion des refs
  const setRefs = (node: HTMLElement | null) => {
    setNodeRef(node);
    setDroppableRef(node);
  };

  const style = {
    transform: CSS.Translate.toString(transform),
    transition,
    cursor: "grab",
  };

  return (
    <div
      ref={setRefs}
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
  availableOptions: ItemType[] | null | undefined;
  selectedOptions: ItemType[] | null | undefined;
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

/**
 * Hook personnalisé pour gérer une liste transférable entre une liste disponible et une liste sélectionnée.
 * Doit être utilisé dans un contexte Formik absolument.
 *
 * @param listeDisponible Liste des options disponibles (doit être préfiltrée par rapport à la liste sélectionnée).
 * @param listeSelectionne Les options déjà sélectionnées (paramètre en base par exemple).
 * @param nameFormik Le nom du champ Formik lié à la liste sélectionnée.
 */

export const useTransferList = ({
  listeDisponible,
  listeSelectionne,
  nameFormik,
}: {
  listeDisponible: ItemType[] | undefined | null;
  listeSelectionne: ItemType[] | undefined | null;
  nameFormik: string;
}) => {
  const { setFieldValue } = useFormikContext();
  const [availableOptions, setAvailableOptions] = useState<
    ItemType[] | undefined | null
  >(listeDisponible);
  const [selectedOptions, setSelectedOptions] = useState<
    ItemType[] | undefined | null
  >(listeSelectionne);

  useEffect(() => {
    if (
      listeSelectionne != null &&
      selectedOptions?.length !== listeSelectionne?.length
    ) {
      setSelectedOptions(listeSelectionne ? listeSelectionne : []);
    }
  }, [listeSelectionne, selectedOptions, setSelectedOptions]);

  useEffect(() => {
    setFieldValue(nameFormik, selectedOptions);

    const filteredOptions = availableOptions?.filter(
      (option) =>
        !selectedOptions?.some((selected) => selected.id === option.id),
    );
    if (availableOptions?.length !== filteredOptions?.length) {
      setAvailableOptions(filteredOptions);
    }
  }, [selectedOptions, availableOptions, nameFormik, setFieldValue]);

  return {
    availableOptions,
    selectedOptions,
    setAvailableOptions,
    setSelectedOptions,
  };
};
