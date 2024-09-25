import Modal from "react-bootstrap/Modal";
import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import { usePost } from "../Fetch/useFetch.tsx";
import ToastAutohide from "../../module/Toast/ToastAutoHide.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";

const ConfirmModalBody = ({
  query,
  id,
  closeModal,
  onConfirm,
  content,
  href,
}: ConfirmModalBodyType) => {
  const { success: successToast, error: errorToast } = useToastContext();
  const action = usePost(id ? `${query}/${id}` : `${query}`, {
    onResolve: (res: any) => {
      onConfirm && onConfirm(res);
      successToast({ message: "L'action a bien été exécutée" });
      closeModal();
    },
    onReject: async (error: any) => {
      errorToast({
        message: `Erreur lors de l'exécution de l'action : ${await error.text()}`,
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
            // Si on a un href, on n'est pas en post
            if (href === undefined) {
              try {
                await action.run();
              } catch (e: any) {
                ToastAutohide({
                  content: `Erreur lors de l'exécution de l'action : ${e.message}`,
                  variant: "danger",
                });
              }
            } else {
              closeModal();
            }
          }}
          href={href}
        >
          Valider
        </Button>
      </Modal.Footer>
    </>
  );
};

const ConfirmModal = ({
  visible,
  closeModal,
  header = "Confirmation",
  query,
  id,
  onConfirm,
  content = "Confirmer ?",
  href = undefined,
}: ConfirmModalBodyType & { visible: boolean }) => {
  return (
    <Modal show={visible} onHide={closeModal}>
      <Modal.Header>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <ConfirmModalBody
        query={query}
        id={id}
        closeModal={closeModal}
        onConfirm={onConfirm}
        content={content}
        href={href}
      />
    </Modal>
  );
};

type ConfirmModalBodyType = {
  visible: boolean;
  header?: ReactNode;
  closeModal: () => void;
  id?: string;
  query: string;
  onConfirm?: (value: any) => void;
  content: ReactNode;
  href?: string | undefined;
};

export default ConfirmModal;
