import { useState } from "react";

function useModal(initialVisible = false) {
  const [visible, setVisible] = useState(initialVisible);
  const [value, setValue] = useState(null);

  function handleShow(value = null) {
    setVisible(true);
    setValue(value);
  }

  function handleClose() {
    setVisible(false);
    setValue(null);
  }

  return { visible, value, handleShow, handleClose };
}

export default useModal;
