import { Button } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { SubmitButtonType } from "../../utils/typeUtils.tsx";

const SubmitFormButtons = ({
  update = false,
  returnLink,
  onClick,
  disabledValide = false,
}: SubmitButtonType) => {
  return (
    <Row className={"my-3 d-flex justify-content-center"}>
      {returnLink && (
        <Col sm={"auto"}>
          <Button variant={"secondary"} href={returnLink}>
            Retour
          </Button>
        </Col>
      )}
      <Col sm={"auto"}>
        <Button
          type="submit"
          variant={update ? "info" : "primary"}
          onClick={onClick}
          disabled={disabledValide}
        >
          Enregistrer
        </Button>
      </Col>
    </Row>
  );
};

export default SubmitFormButtons;
