import { DndContext } from "@dnd-kit/core";
import { arrayMove, SortableContext, useSortable } from "@dnd-kit/sortable";
import { FC } from "react";
import { CSS } from "@dnd-kit/utilities";
import Table from "react-bootstrap/Table";
import { Button } from "react-bootstrap";
import { IconClose, IconDragNDrop } from "../Icon/Icon.tsx";
import { PeiInfoEntity } from "../../Entities/PeiEntity.tsx";

type SortableTourneePeiType = {
  id: string;
  item: string;
  onRemove: (id: string) => any;
};

const SortableRowTourneePei: FC<SortableTourneePeiType> = ({
  id, // La propriété id doit impérativement s'appeler id
  item,
  onRemove,
}) => {
  const { setNodeRef, listeners, transform, transition } = useSortable({ id });

  const styles = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <tr ref={setNodeRef} style={styles}>
      <td>
        <span {...listeners} role="button" className="pe-2">
          <IconDragNDrop />
        </span>
        {item.peiNumeroComplet}
      </td>
      <td>{item.natureLibelle}</td>
      <td>{item.adresse}</td>
      <td>{item.communeLibelle}</td>
      <td>
        <Button
          variant={"link"}
          className={"text-danger text-decoration-none"}
          onClick={() => {
            onRemove(id);
          }}
        >
          <IconClose />
        </Button>
      </td>
    </tr>
  );
};

type SortableTableTourneePeiType = {
  data: PeiInfoEntity[];
  setData: React.Dispatch<React.SetStateAction<PeiInfoEntity[]>>;
};

const SortableTableTourneePei = ({
  data,
  setData,
}: SortableTableTourneePeiType) => {
  function handleRemove(id: string) {
    setData((data) => data.filter((e) => e.id !== id));
  }

  function dragEndEvent(e: DndContext) {
    const { over, active } = e;
    setData((data) => {
      return arrayMove(
        data,
        data.findIndex((item) => item.id === active.id),
        data.findIndex((item) => item.id === over?.id),
      );
    });
  }

  return (
    <DndContext onDragEnd={dragEndEvent}>
      <Table bordered striped>
        <thead>
          <tr>
            <th>Point d&apos;eau</th>
            <th>Nature</th>
            <th>Adresse</th>
            <th>Commune</th>
            <th>Action</th>
          </tr>
        </thead>
        <tbody>
          <SortableContext items={data}>
            {data.map((v) => (
              <SortableRowTourneePei
                key={v.id}
                id={v.id}
                item={v}
                onRemove={handleRemove}
              />
            ))}
          </SortableContext>
        </tbody>
      </Table>
    </DndContext>
  );
};

export default SortableTableTourneePei;
