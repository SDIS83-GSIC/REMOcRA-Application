import { useSortable } from "@dnd-kit/sortable";

export function Item({ id }: { id: string }) {
  return <div className="bg-white border p-2 rounded text-center">{id}</div>;
}

export default function SortableItem({ id }: { id: string }) {
  const { attributes, listeners, setNodeRef } = useSortable({ id: id });

  return (
    <div ref={setNodeRef} className={"m-2"} {...attributes} {...listeners}>
      <Item id={id} />
    </div>
  );
}
