import { ReactNode } from "react";
import { Col, Row } from "react-bootstrap";

type TitlePageModel = {
  title: string;
  icon: ReactNode;
  right?: ReactNode;
};

const PageTitle = ({ title, icon, right }: TitlePageModel) => {
  return (
    <Row className="my-3 mx-2 noprint">
      <Col>
        <h1 className="fw-bold">
          {icon} {title}
        </h1>
      </Col>
      {right ? (
        <Col className="my-auto" sm={"auto"}>
          {right}
        </Col>
      ) : (
        ""
      )}
    </Row>
  );
};

export default PageTitle;
