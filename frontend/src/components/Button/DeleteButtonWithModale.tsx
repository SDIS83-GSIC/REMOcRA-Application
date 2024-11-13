import { Button } from "react-bootstrap";
import { IconDelete } from "../Icon/Icon.tsx";
import DeleteModal from "../Modal/DeleteModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";

const DeleteButtonWithModale = ({
  path,
  reload,
  title = true,
  disabled = false,
}: {
  path: string;
  reload: () => void;
  title: boolean;
  disabled: boolean;
}) => {
  const { visible, show, close, ref } = useModal();
  return (
    <>
      <Button
        variant={"danger"}
        disabled={disabled}
        onClick={show}
        className={"text-white"}
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
      />
    </>
  );
};

export default DeleteButtonWithModale;
