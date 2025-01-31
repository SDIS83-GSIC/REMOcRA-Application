import { ReactNode } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { URLS } from "../../../routes.tsx";
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
    <Row className="my-3 mx-2 noprint">
      <Col>
        {displayReturnButton && (
          <Row className="d-flex flex-row py-3">
            <Col>
              <Button
                variant={"link"}
                className="text-decoration-none text-sm font-weight-bold"
                onClick={() => {
                  if (location.state?.from?.slice(-1)[0]) {
                    navigate(location.state.from.slice(-1)[0], {
                      state: { from: location.state.from.slice(0, -1) },
                    });
                  } else if (urlRetour) {
                    navigate(urlRetour);
                  } else {
                    navigate(URLS.ACCUEIL);
                  }
                }}
              >
                <IconPreviousPage /> Retour
              </Button>
            </Col>
          </Row>
        )}
        <Row>
          <Col>
            <h1 className="fw-bold">
              {icon} {title}
            </h1>
          </Col>
        </Row>
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
