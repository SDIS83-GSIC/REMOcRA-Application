import { IconCreate } from "../Icon/Icon.tsx";
import LinkButton from "./LinkButton.tsx";

/**
 * CreateButton ne doit être utilisé que dans le cadre d'une redirection vers une page de création
 * Il permet de gérer le bouton retour
 */
const CreateButton = ({
  title,
  href,
  onClick,
  disabled = false,
  state,
  classnames,
}: CreateButtonType) => {
  return (
    <LinkButton
      pathname={href}
      variant={"primary"}
      onClick={onClick}
      disabled={disabled}
      classname={classnames}
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
  classnames?: string;
};

export default CreateButton;
