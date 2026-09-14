import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import Modal from "react-bootstrap/Modal";
import ToastAutohide from "../../module/Toast/ToastAutoHide.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import Loading from "../Elements/Loading/Loading.tsx";
import { useDelete } from "../Fetch/useFetch.tsx";

const DeleteModalBody = ({
  query,
  id,
  onCancel,
  onSuccess,
  onError,
  onDelete,
  content,
  successLibelle,
}: DeleteModalBodyType) => {
  const { success: successToast, error: errorToast } = useToastContext();

  const del = useDelete(id ? `${query}/${id}` : `${query}`, {
    onResolve: (res: any) => {
      onDelete && onDelete(res);
      successToast(successLibelle ?? "L'élément a bien été supprimé");
      onSuccess();
    },
    onReject: async (error: any) => {
      errorToast(
        `Erreur lors de l'exécution de l'action : ${await error.text()}`,
      );
      onError();
    },
  });
  const isSubmitting = del.isPending || del.isLoading;

  return (
    <>
      <Modal.Body>
        {content}
        {isSubmitting && (
          <div className="d-flex flex-column align-items-center mt-3 gap-2">
            <Loading className="py-2" />
            <div>Suppression en cours…</div>
          </div>
        )}
      </Modal.Body>
      <Modal.Footer>
        <Button variant="secondary" onClick={onCancel} disabled={isSubmitting}>
          Annuler
        </Button>
        <Button
          variant="primary"
          disabled={isSubmitting}
          onClick={async () => {
            if (isSubmitting) {
              return;
            }
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
  onCancel,
  onSuccess,
  onError,
  header = "Suppression d'un élément",
  query,
  id,
  onDelete,
  content = "Voulez-vous supprimer cet élément ?",
  successLibelle,
}: DeleteModalBodyType & { visible: boolean }) => {
  return (
    <Modal show={visible} onHide={onCancel} ref={ref}>
      <Modal.Header>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <DeleteModalBody
        query={query}
        id={id}
        onCancel={onCancel}
        onSuccess={onSuccess}
        onError={onError}
        onDelete={onDelete}
        content={content}
        successLibelle={successLibelle}
      />
    </Modal>
  );
};

type DeleteModalBodyType = {
  header?: ReactNode;
  onCancel: () => void;
  onSuccess: () => void;
  onError: () => void;
  query: string;
  id?: string;
  content?: ReactNode;
  ref?: any;
  onDelete: (values: any) => void;
  successLibelle?: string;
};

export default DeleteModal;
