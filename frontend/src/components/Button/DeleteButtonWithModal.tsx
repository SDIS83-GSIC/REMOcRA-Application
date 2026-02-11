import { Button } from "react-bootstrap";
import { IconDelete } from "../Icon/Icon.tsx";
import DeleteModal from "../Modal/DeleteModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";

const DeleteButtonWithModal = ({
  path,
  reload,
  title = true,
  disabled = false,
  variant = "danger",
  className = "text-white",
  header = null,
  content = null,
}: {
  path: string;
  reload: () => void;
  title: boolean;
  disabled: boolean;
  variant?: string;
  className?: string;
  header?: React.ReactNode;
  content?: React.ReactNode;
}) => {
  const { visible, show, close, ref } = useModal();
  return (
    <>
      <Button
        variant={variant}
        disabled={disabled}
        onClick={show}
        className={className}
      >
        <IconDelete />
        {title && <>&nbsp;Supprimer</>}
      </Button>
      <DeleteModal
        visible={visible}
        closeModal={close}
        query={path}
        ref={ref}
        onDelete={() => (reload ? reload() : window.location.reload())}
        header={header}
        content={content}
      />
    </>
  );
};

export default DeleteButtonWithModal;
