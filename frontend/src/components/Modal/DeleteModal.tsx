import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import Modal from "react-bootstrap/Modal";
import { useDelete } from "../Fetch/useFetch.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import ToastAutohide from "../../module/Toast/ToastAutoHide.tsx";

const DeleteModalBody = ({
  query,
  id,
  closeModal,
  onDelete,
  content,
}: DeleteModalBodyType) => {
  const { success: successToast, error: errorToast } = useToastContext();
  const del = useDelete(id ? `${query}/${id}` : `${query}`, {
    onResolve: (res: any) => {
      onDelete && onDelete(res);
      successToast("L'élément a bien été supprimé");
      closeModal();
    },
    onReject: async (error: any) => {
      errorToast(
        `Erreur lors de l'exécution de l'action : ${await error.text()}`,
      );
      closeModal();
    },
  });

  return (
    <>
      <Modal.Body>{content}</Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={closeModal}>
          Annuler
        </Button>
        <Button
          variant="primary"
          onClick={async () => {
            try {
              await del.run();
            } catch (e: any) {
              ToastAutohide({
                content: `Erreur lors de la suppression de l'élément : ${e.message}`,
                variant: "danger",
              });
            }
          }}
        >
          Valider
        </Button>
      </Modal.Footer>
    </>
  );
};

const DeleteModal = ({
  ref,
  visible,
  closeModal,
  header = "Suppression d'un élément",
  query,
  id,
  onDelete,
  content = "Voulez-vous supprimer cet élément ?",
}: DeleteModalBodyType & { visible: boolean }) => {
  return (
    <Modal show={visible} onHide={closeModal} ref={ref}>
      <Modal.Header>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <DeleteModalBody
        query={query}
        id={id}
        closeModal={closeModal}
        onDelete={onDelete}
        content={content}
      />
    </Modal>
  );
};

type DeleteModalBodyType = {
  header?: ReactNode;
  closeModal: () => void;
  query: string;
  id?: string;
  content?: ReactNode;
  ref?: any;
  onDelete: (values: any) => void;
};

export default DeleteModal;
