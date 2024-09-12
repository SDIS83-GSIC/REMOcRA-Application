import { useEffect, useRef, useState } from "react";

const useModal = () => {
  const [visible, setVisible] = useState(false);
  const [value, setValue] = useState(null);

  const ref = useRef<HTMLDialogElement | null>(null);

  useEffect(() => {
    if (!ref.current) {
      return;
    }
    const modal = ref.current;
    visible && modal.showModal();
    !visible && modal.close();
  }, [visible]);

  function show(value = null) {
    setVisible(true);
    const modal = ref.current;
    modal?.showModal();
    setValue(value);
  }

  function close() {
    const modal = ref.current;
    modal?.close();
    setVisible(false);
    setValue(null);
  }

  return { visible, value, show, close, ref };
};

export default useModal;
