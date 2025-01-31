import { Button } from "react-bootstrap";
import { IconDelete } from "../Icon/Icon.tsx";

const DeleteButton = ({
  className = "text-danger",
  disabled,
  onClick,
  title,
}: DeleteButtonType) => {
  return (
    <Button
      variant={"link"}
      className={className}
      disabled={disabled}
      onClick={onClick}
    >
      <IconDelete /> {title}
    </Button>
  );
};
type DeleteButtonType = {
  className?: string;
  disabled?: boolean;
  onClick: (...args: any[]) => void;
  title?: string;
};

export default DeleteButton;
