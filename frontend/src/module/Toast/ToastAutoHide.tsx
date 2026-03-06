import Toast from "react-bootstrap/Toast";
import type { Variant } from "react-bootstrap/types";

const ToastAutohide = ({
  header,
  content,
  variant,
  delay,
  id,
  onClose,
}: ToastAutohideType) => {
  const isPersistent = delay === null || delay === undefined;

  return (
    <Toast
      bg={variant.toLowerCase()}
      onClose={() => onClose && id && onClose(id)}
      show={true}
      delay={isPersistent ? undefined : delay}
      autohide={!isPersistent}
      className={"toast-fixed"}
    >
      <Toast.Header closeLabel={"Fermer"}>
        <span className={"fw-bold me-auto"}>{header}</span>
      </Toast.Header>
      <Toast.Body>{content}</Toast.Body>
    </Toast>
  );
};

type ToastAutohideType = {
  header?: string;
  content: React.ReactNode;
  delay?: number | null;
  variant: Variant;
  id?: number;
  onClose?: (id: number) => void;
};

export default ToastAutohide;
