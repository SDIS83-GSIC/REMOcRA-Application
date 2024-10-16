import { IconDelete } from "../Icon/Icon.tsx";
import CustomLinkButton from "./CustomLinkButton.tsx";

const DeleteButton = ({
  className = "text-danger",
  disabled,
  onClick,
  title,
}: DeleteButtonType) => {
  return (
    <CustomLinkButton
      variant={"link"}
      className={className}
      disabled={disabled}
      onClick={onClick}
    >
      <IconDelete /> {title}
    </CustomLinkButton>
  );
};
type DeleteButtonType = {
  className?: string;
  disabled?: boolean;
  onClick: (...args: any[]) => void;
  title?: string;
};

export default DeleteButton;
