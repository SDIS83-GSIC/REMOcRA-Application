import { useFormikContext } from "formik";
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
  beforeReturn,
}: SubmitButtonType) => {
  const formik = useFormikContext();
  const navigate = useNavigate();
  const location = useLocation();

  //Quand pas de formik/myformik (liste drag et drop), on se base uniquement sur le disableValide
  const isDisabled = formik?.isSubmitting || disabledValide;

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
              beforeReturn && beforeReturn();
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
          disabled={isDisabled}
        >
          {submitTitle}
        </Button>
      </Col>
    </Row>
  );
};

export default SubmitFormButtons;
