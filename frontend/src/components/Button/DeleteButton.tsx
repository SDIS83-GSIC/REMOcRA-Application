import { Button } from "react-bootstrap";
import classNames from "classnames";
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
      className={classNames(className, "text-decoration-none")}
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
