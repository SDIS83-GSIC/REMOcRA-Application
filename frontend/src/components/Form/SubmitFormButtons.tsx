import { Button } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { SubmitButtonType } from "../../utils/typeUtils.tsx";

const SubmitFormButtons = ({
  returnLink,
  onSecondaryActionClick,
  secondaryActionTitle = "Retour",
  onClick,
  disabledValide = false,
  submitTitle = "Enregistrer",
}: SubmitButtonType) => {
  return (
    <Row className={"my-3 d-flex justify-content-center"}>
      {(returnLink || onSecondaryActionClick) && (
        <Col sm={"auto"}>
          <Button
            variant={"secondary"}
            href={returnLink}
            onClick={onSecondaryActionClick}
          >
            {secondaryActionTitle}
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
