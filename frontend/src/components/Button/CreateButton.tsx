import { IconCreate } from "../Icon/Icon.tsx";
import LinkButton from "./LinkButton.tsx";

const CreateButton = ({
  title,
  href,
  onClick,
  disabled = false,
}: CreateButtonType) => {
  return (
    <LinkButton
      href={href}
      variant={"primary"}
      onClick={onClick}
      disabled={disabled}
    >
      <IconCreate /> {title}
    </LinkButton>
  );
};

type CreateButtonType = {
  title: string;
  href?: string;
  onClick?: (...args: any[]) => void;
  disabled?: boolean;
};

export default CreateButton;
