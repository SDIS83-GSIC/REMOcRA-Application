import { ReactNode } from "react";
import { Button, Modal } from "react-bootstrap";

export const SimpleModalBody = ({
  closeModal,
  content,
  confirmButtonText,
}: SimpleModalBodyType) => {
  return (
    <>
      <Modal.Body>{content}</Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={closeModal}>
          {confirmButtonText ?? "Fermer"}
        </Button>
      </Modal.Footer>
    </>
  );
};

const SimpleModal = ({
  ref,
  visible,
  closeModal,
  header,
  content,
}: SimpleModalType) => {
  return (
    <Modal show={visible} onHide={closeModal} ref={ref} size="xl">
      <Modal.Header closeButton>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <SimpleModalBody closeModal={closeModal} content={content} />
    </Modal>
  );
};

type SimpleModalType = SimpleModalBodyType & {
  header: ReactNode;
  ref: any;
  visible: boolean;
};

type SimpleModalBodyType = {
  closeModal: () => void;
  content: ReactNode;
  confirmButtonText?: string;
};

export default SimpleModal;
