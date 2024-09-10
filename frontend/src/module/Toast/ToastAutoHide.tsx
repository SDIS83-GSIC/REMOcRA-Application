import React, { ReactNode, useState } from "react";
import Toast from "react-bootstrap/Toast";
import type { Variant } from "react-bootstrap/types";

const ToastAutohide = ({
  header,
  content,
  variant,
  delay,
}: ToastAutohideType) => {
  const [show, setShow] = useState(true);
  return (
    <Toast
      bg={variant.toLowerCase()}
      onClose={() => setShow(false)}
      show={show}
      delay={delay}
      autohide
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
  content: ReactNode;
  delay?: number;
  variant: Variant;
};

export default ToastAutohide;
