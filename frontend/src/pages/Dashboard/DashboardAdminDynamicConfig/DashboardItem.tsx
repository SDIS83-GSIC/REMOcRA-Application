import { useDraggable } from "@dnd-kit/core";
import { Card } from "react-bootstrap";
import { COMPONENTS } from "../Constants.tsx";

type DashboardItemProps = {
  component: any;
  isSelected: boolean;
  heightRow: number;
};

const DashboardItem = (props: DashboardItemProps) => {
  const { attributes, listeners, setNodeRef, transform } = useDraggable({
    id: props.component.id,
  });

  const style = {
    transform: `translate3d(${transform?.x || 0}px, ${transform?.y || 0}px, 0)`,
    position: "absolute" as const,
    top: `${props.component.configPosition.y * props.heightRow}px`,
    left: `${props.component.configPosition.x * 25}%`,
    width: `${(props.component.configPosition.largeur ?? 1) * 25}%`,
    height: `calc(${(props.component.configPosition.hauteur ?? 1) * props.heightRow}px - 2rem)`,
    zIndex: props.isSelected ? 10 : 1,
  };

  // Set le composant associé à la clé
  const Component = COMPONENTS[props.component.key as keyof typeof COMPONENTS];
  return (
    <div
      ref={setNodeRef}
      {...attributes}
      {...listeners}
      style={style}
      className={`dashboard-item ${props.isSelected ? "active" : ""}`}
    >
      <Card
        bg="secondary"
        className="p-2"
        style={{
          border: props.isSelected ? "2px solid blue" : "1px solid grey",
          boxShadow: props.isSelected
            ? "0 0 10px rgba(0, 0, 255, 0.5)"
            : "none",
        }}
      >
        <Card.Body className="p-0">
          <div style={{ height: `calc(${style.height} - 2rem)` }}>
            <Component
              data={props.component.data}
              config={props.component.config}
            />
          </div>
        </Card.Body>
        <Card.Footer className="text-muted">
          {props.component.title}
        </Card.Footer>
      </Card>
    </div>
  );
};

export default DashboardItem;
