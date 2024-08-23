import { FC } from "react";
import { CSS } from "@dnd-kit/utilities";
import { useSortable } from "@dnd-kit/sortable";
import { Row } from "react-bootstrap";

type SortableTourneePeiType = {
  id: string;
  peiNumeroComplet: string;
};
const SortableTourneePei: FC<SortableTourneePeiType> = ({
  id,
  peiNumeroComplet,
}) => {
  const { setNodeRef, listeners, transform, transition } = useSortable({ id });

  const styles = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  return (
    <div ref={setNodeRef} {...listeners} style={styles}>
      <Row className="border border-3 mt-1">
        Numéro complet : {peiNumeroComplet}
      </Row>
    </div>
  );
};

export default SortableTourneePei;
