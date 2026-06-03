import { ReactNode } from "react";
import { Modal } from "react-bootstrap";
import { SimpleModalBody } from "./SimpleModal.tsx";

const SimpleConfirmModal = ({
  ref,
  visible,
  closeModal,
  onSubmit,
  header,
  content,
  confirmButtonText,
}: {
  header: ReactNode;
  ref: any;
  confirmButtonText?: string;
  visible: boolean;
  closeModal?: () => void;
  onSubmit: () => void;
  content: ReactNode;
}) => {
  return (
    <Modal show={visible} onHide={closeModal} ref={ref} size="xl">
      <Modal.Header closeButton>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <SimpleModalBody
        closeModal={onSubmit}
        content={content}
        confirmButtonText={confirmButtonText}
      />
    </Modal>
  );
};

export default SimpleConfirmModal;
