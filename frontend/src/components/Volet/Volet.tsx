import { ReactNode } from "react";
import { Offcanvas } from "react-bootstrap";

const Volet = ({ show, handleClose, className, children }: VoletType) => {
  return (
    <Offcanvas
      show={show}
      onHide={handleClose}
      placement="end"
      className={className}
      scroll={true}
      backdrop={false}
      autoFocus={false}
    >
      <Offcanvas.Header closeButton />
      <Offcanvas.Body>{children}</Offcanvas.Body>
    </Offcanvas>
  );
};

type VoletType = {
  show: boolean;
  handleClose: () => void;
  className?: string;
  children: ReactNode;
};

export default Volet;
