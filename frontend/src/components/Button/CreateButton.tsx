import { IconCreate } from "../Icon/Icon.tsx";
import LinkButton from "./LinkButton.tsx";

const CreateButton = ({ title, href, onClick }: CreateButtonType) => {
  return (
    <LinkButton href={href} variant={"primary"} onClick={onClick}>
      <IconCreate /> {title}
    </LinkButton>
  );
};

type CreateButtonType = {
  title: string;
  href?: string;
  onClick?: (...args: any[]) => void;
};

export default CreateButton;
