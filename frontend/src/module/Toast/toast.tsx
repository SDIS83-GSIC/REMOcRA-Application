import React, { ReactNode, useState } from "react";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import Toast from "react-bootstrap/Toast";
import type { Variant } from "react-bootstrap/types";

const ToastAutohide = ({
  header,
  content,
  variant,
  delay = 3000,
}: ToastAutohideType) => {
  const [show, setShow] = useState(true);
  return (
    <Row className={"position-absolute top-0 end-0"}>
      <Col xs={6}>
        <Toast
          bg={variant}
          onClose={() => setShow(false)}
          show={show}
          delay={delay}
          autohide
        >
          {header && <Toast.Header>{header}</Toast.Header>}
          <Toast.Body>{content}</Toast.Body>
        </Toast>
      </Col>
    </Row>
  );
};

type ToastAutohideType = {
  header?: ReactNode;
  content: ReactNode;
  delay?: number;
  variant?: Variant;
};

export default ToastAutohide;
