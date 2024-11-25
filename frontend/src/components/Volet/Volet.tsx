import { ReactNode } from "react";
import { Offcanvas } from "react-bootstrap";

const Volet = ({
  show,
  handleClose,
  className,
  backdrop = false,
  title,
  children,
}: VoletType) => {
  return (
    <Offcanvas
      show={show}
      onHide={handleClose}
      placement="end"
      className={className}
      scroll={true}
      backdrop={backdrop}
      autoFocus={true}
    >
      <Offcanvas.Header closeButton>
        {title && <Offcanvas.Title>{title}</Offcanvas.Title>}
      </Offcanvas.Header>
      <Offcanvas.Body>{children}</Offcanvas.Body>
    </Offcanvas>
  );
};

type VoletType = {
  show: boolean;
  handleClose: () => void;
  className?: string;
  children: ReactNode;
  backdrop?: boolean;
  title?: string;
};

export default Volet;
