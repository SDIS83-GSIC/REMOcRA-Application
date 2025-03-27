import { Button } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import { useLocation, useNavigate } from "react-router-dom";
import { navigateGoBack } from "../../utils/fonctionsUtils.tsx";
import { SubmitButtonType } from "../../utils/typeUtils.tsx";

const SubmitFormButtons = ({
  returnLink,
  onSecondaryActionClick,
  secondaryActionTitle,
  onClick,
  disabledValide = false,
  submitTitle = "Enregistrer",
}: SubmitButtonType) => {
  const navigate = useNavigate();
  const location = useLocation();

  return (
    <Row className={"my-3 d-flex justify-content-center"}>
      {onSecondaryActionClick && (
        <Col sm={"auto"}>
          <Button variant={"secondary"} onClick={onSecondaryActionClick}>
            {secondaryActionTitle}
          </Button>
        </Col>
      )}
      {returnLink && (
        <Col sm={"auto"}>
          <Button
            variant={"light"}
            className="btn-light"
            onClick={() => {
              navigateGoBack(location, navigate);
            }}
          >
            Retour
          </Button>
        </Col>
      )}
      <Col sm={"auto"}>
        <Button
          type="submit"
          variant={"primary"}
          onClick={onClick}
          disabled={disabledValide}
        >
          {submitTitle}
        </Button>
      </Col>
    </Row>
  );
};

export default SubmitFormButtons;
