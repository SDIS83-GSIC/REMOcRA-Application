import classNames from "classnames";
import { Container, Row, Button } from "react-bootstrap";
import ListTournee from "../../pages/Tournee/ListTournee.tsx";

type QueryTableTourneeType = {
  useFilterContext: any;
  filterId: string | null;
  setShowTable?: (value: ((prevState: boolean) => boolean) | boolean) => void;
  setFilterId?: (value: ((prevState: null) => null) | null) => void;
};

const QueryTableTournee = ({
  filterId = null,
  setShowTable,
  setFilterId,
}: QueryTableTourneeType) => {
  return (
    <Container>
      <Row xs={"auto"}>
        <Button
          onClick={() => {
            if (setShowTable) {
              setShowTable(true);
            }
            if (setFilterId) {
              setFilterId(null);
            }
          }}
          className={classNames("mx-5 my-3 btn-secondary", {
            "d-none": filterId == null,
          })}
        >
          Retour à la liste précédente
        </Button>
      </Row>

      {filterId != null && (
        <>
          <ListTournee peiId={filterId} />
        </>
      )}
    </Container>
  );
};
export default QueryTableTournee;
