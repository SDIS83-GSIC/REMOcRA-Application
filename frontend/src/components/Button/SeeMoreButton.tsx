import { Button } from "react-bootstrap";
import React, { ReactNode, useState } from "react";
import useModal from "../Modal/ModalUtils.tsx";
import SimpleModal from "../Modal/SimpleModal.tsx";

const SeeMoreButton = ({ headerModal = "", children }: TypeSeeMoreButton) => {
  const { ref } = useModal();

  const [show, setShow] = useState(false);
  const handleShow = () => setShow(true);
  const handleClose = () => setShow(false);

  return (
    <>
      <Button variant="info" onClick={handleShow} size={"sm"} className={"p-1"}>
        Plus d&apos;info
      </Button>
      <SimpleModal
        ref={ref}
        visible={show}
        closeModal={handleClose}
        header={headerModal}
        content={children}
      />
    </>
  );
};

type TypeSeeMoreButton = { headerModal?: ReactNode; children: ReactNode };
export default SeeMoreButton;
