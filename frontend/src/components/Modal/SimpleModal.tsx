import { ReactNode } from "react";
import { Button, Modal } from "react-bootstrap";

const SimpleModalBody = ({ closeModal, content }: SimpleModalBodyType) => {
  return (
    <>
      <Modal.Body>{content}</Modal.Body>
      <Modal.Footer>
        <Button variant="primary" onClick={closeModal}>
          Fermer
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
}: SimpleModaleType) => {
  return (
    <Modal show={visible} onHide={closeModal} ref={ref} size="xl">
      <Modal.Header closeButton>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <SimpleModalBody closeModal={closeModal} content={content} />
    </Modal>
  );
};

type SimpleModaleType = SimpleModalBodyType & {
  header: ReactNode;
  ref: any;
  visible: boolean;
};

type SimpleModalBodyType = {
  closeModal: () => void;
  content: ReactNode;
};

export default SimpleModal;
