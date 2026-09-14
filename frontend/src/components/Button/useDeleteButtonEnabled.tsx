import { useCallback, useState } from "react";
import useModal from "../Modal/ModalUtils.tsx";

const useDeleteButtonEnabled = () => {
  const { visible, ref, show, close } = useModal();
  const [isDeleteEnabled, setIsDeleteEnabled] = useState(true);

  const openDeleteModal = useCallback(() => {
    setIsDeleteEnabled(false);
    show();
  }, [show]);

  const closeDeleteModalAndReset = useCallback(() => {
    close();
    setIsDeleteEnabled(true);
  }, [close]);

  const closeDeleteModalSuccess = useCallback(() => {
    close();
  }, [close]);

  return {
    visible,
    ref,
    isDeleteEnabled,
    openDeleteModal,
    closeDeleteModalCancel: closeDeleteModalAndReset,
    closeDeleteModalError: closeDeleteModalAndReset,
    closeDeleteModalSuccess,
  };
};

export default useDeleteButtonEnabled;
