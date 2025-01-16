import Modal from "react-bootstrap/Modal";
import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import { usePost, usePut } from "../Fetch/useFetch.tsx";
import ToastAutohide from "../../module/Toast/ToastAutoHide.tsx";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";

type FunctionType = {
  id;
  query;
  onConfirm;
  successToast;
  closeModal;
  errorToast;
};
const usePostState = ({
  id,
  query,
  onConfirm,
  successToast,
  closeModal,
  errorToast,
}: FunctionType) => {
  return usePost(id ? `${query}/${id}` : `${query}`, {
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
};

const usePutState = ({
  id,
  query,
  onConfirm,
  successToast,
  closeModal,
  errorToast,
}) => {
  return usePut(
    id ? `${query}/${id}` : `${query}`,
    {
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
    },
    false,
  );
};

const ConfirmModalBody = ({
  query,
  id,
  closeModal,
  onConfirm,
  content,
  href,
  isPost,
}: ConfirmModalBodyType) => {
  const { success: successToast, error: errorToast } = useToastContext();
  const usePost = usePostState({
    id: id,
    query: query,
    closeModal: closeModal,
    onConfirm: onConfirm,
    errorToast: errorToast,
    successToast: successToast,
  });

  const usePut = usePutState({
    id: id,
    query: query,
    closeModal: closeModal,
    onConfirm: onConfirm,
    errorToast: errorToast,
    successToast: successToast,
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
                isPost ? await usePost.run() : await usePut.run();
              } catch (e: any) {
                ToastAutohide({
                  content: `Erreur lors de l'exécution de l'action : ${e.message}`,
                  variant: "danger",
                });
              }
            } else {
              if (onConfirm) {
                // Si onConfirm est fourni, exécutez-le directement
                onConfirm(id);
              }

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
  ref,
  visible,
  closeModal,
  header = "Confirmation",
  query,
  id,
  onConfirm,
  content = "Confirmer ?",
  href = undefined,
  isPost = true,
}: ConfirmModalBodyType & { visible: boolean }) => {
  return (
    <Modal show={visible} onHide={closeModal} ref={ref}>
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
        isPost={isPost}
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
  content?: ReactNode;
  href?: string | undefined;
  ref?: any;
  isPost?: boolean;
};

export default ConfirmModal;
