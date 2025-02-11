import { IconCreate } from "../Icon/Icon.tsx";
import LinkButton from "./LinkButton.tsx";

const CreateButton = ({
  title,
  href,
  onClick,
  disabled = false,
  state,
}: CreateButtonType) => {
  return (
    <LinkButton
      pathname={href}
      variant={"primary"}
      onClick={onClick}
      disabled={disabled}
      state={state}
    >
      <IconCreate /> {title}
    </LinkButton>
  );
};

type CreateButtonType = {
  title: string;
  href?: string;
  onClick?: (...args: any[]) => void;
  state?: object;
  disabled?: boolean;
};

export default CreateButton;
