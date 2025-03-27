import { ReactNode } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { navigateGoBack } from "../../../utils/fonctionsUtils.tsx";
import { IconPreviousPage } from "../../Icon/Icon.tsx";

type TitlePageModel = {
  title: string;
  icon: ReactNode;
  right?: ReactNode;
  displayReturnButton?: boolean;
  urlRetour?: string;
};

const PageTitle = ({
  title,
  icon,
  right,
  displayReturnButton = true,
  urlRetour,
}: TitlePageModel) => {
  const navigate = useNavigate();
  const location = useLocation();
  return (
    <Container className="ps-0">
      <Row className="mb-4 mt-3 mx-0 ps-0 noprint">
        {displayReturnButton && (
          <Col xs={"auto"} className={"ps-0"}>
            <Button
              variant={"link"}
              className="text-decoration-none text-sm font-weight-bold ps-0"
              onClick={() => {
                navigateGoBack(location, navigate, urlRetour);
              }}
            >
              <IconPreviousPage /> Retour
            </Button>
          </Col>
        )}
        <Col xs={"auto"}>
          <h1 className="fw-bold">
            {icon} {title}
          </h1>
        </Col>
        {right ? (
          <Col className="ms-auto" sm={"auto"}>
            {right}
          </Col>
        ) : (
          ""
        )}
      </Row>
    </Container>
  );
};

export default PageTitle;
