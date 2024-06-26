import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import Modal from "react-bootstrap/Modal";
import { useDelete } from "../Fetch/useFetch.tsx";
import ToastAutohide from "../../module/Toast/toast.tsx";

const DeleteModalBody = ({
  query,
  id,
  closeModal,
  onDelete,
  content,
}: DeleteModalBodyType) => {
  const del = useDelete(id ? `${query}/${id}` : `${query}`, {
    onResolve: (res: any) => {
      onDelete && onDelete(res);
      ToastAutohide({
        content: "L'élément a bien été supprimé",
        variant: "success",
      });
      closeModal();
    },
    onReject: async (error: any) => {
      ToastAutohide({
        content: `Erreur lors de l'exécution de l'action : ${await error.text()}`,
        variant: "danger",
      });
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
  visible,
  closeModal,
  header = "Suppression d'un élément",
  query,
  id,
  onDelete,
  content = "Voulez-vous supprimer cet élément ?",
}: DeleteModalBodyType & { visible: boolean }) => {
  return (
    <Modal show={visible} onHide={closeModal}>
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
  content: ReactNode;
  onDelete: (values: any) => void;
};

export default DeleteModal;
