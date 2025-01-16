import React, { ReactNode } from "react";
import Row from "react-bootstrap/Row";

const SectionTitle = ({ children }: { children: ReactNode }) => {
  return (
    <Row className="my-3">
      <h3>{children}</h3>
    </Row>
  );
};

export default SectionTitle;
