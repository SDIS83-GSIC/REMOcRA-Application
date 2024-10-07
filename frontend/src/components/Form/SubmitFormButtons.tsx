import { Button } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import Col from "react-bootstrap/Col";
import { SubmitButtonType } from "../../utils/typeUtils.tsx";

const SubmitFormButtons = ({
  update = false,
  returnLink,
  onClick,
}: SubmitButtonType) => {
  return (
    <Row className={"my-3 d-flex justify-content-center"}>
      <Col sm={"auto"}>
        <Button variant={"secondary"} href={returnLink}>
          Retour
        </Button>
      </Col>
      <Col sm={"auto"}>
        <Button
          type="submit"
          variant={update ? "info" : "primary"}
          onClick={onClick}
        >
          {update ? "Modifier" : "Valider"}
        </Button>
      </Col>
    </Row>
  );
};

export default SubmitFormButtons;
