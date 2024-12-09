import { Button } from "react-bootstrap";
import { IconCreate } from "../Icon/Icon.tsx";

const CreateButton = ({ title, href, onClick }: CreateButtonType) => {
  return (
    <Button
      type="button"
      variant="primary"
      href={href}
      onClick={onClick}
      className="text-nowrap"
    >
      <IconCreate /> {title}
    </Button>
  );
};

type CreateButtonType = {
  title: string;
  href?: string;
  onClick?: (...args: any[]) => void;
};

export default CreateButton;
