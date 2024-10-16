import classNames from "classnames";
import { Button, Container } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import QueryTable, { columnType } from "../../components/Table/QueryTable.tsx";
import FILTER_PAGE from "../../enums/FilterPageEnum.tsx";
import ListPei from "./ListePeiTable.tsx";

type QueryTableWithListingPeiType = {
  column: columnType[];
  query: string;
  idName: string;
  filterValuesToVariable: any;
  useFilterContext: any;
  filterPage: FILTER_PAGE;
  filterId: string;
  showTable: boolean;
  //j'ai besoin des méthodes du state pour savoir ce qui doit être mis a jour
  setShowTable?: (value: ((prevState: boolean) => boolean) | boolean) => void;
  setFilterId?: (value: ((prevState: null) => null) | null) => void;
};

const QueryTableWithListingPei = ({
  //pour le queryTable
  column,
  query,
  idName,
  filterValuesToVariable,
  useFilterContext,
  //pour le composant ListePei
  filterPage,
  showTable,
  filterId = null,
  setShowTable,
  setFilterId,
}: QueryTableWithListingPeiType) => {
  return (
    <Container fluid className={"px-5"}>
      <QueryTable
        displayNone={!showTable}
        query={query}
        columns={column}
        idName={idName}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext}
      />

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
            "d-none": showTable,
          })}
        >
          Retour
        </Button>
      </Row>

      {filterId != null && (
        <ListPei
          displayNone={showTable}
          filterPage={filterPage}
          filterId={filterId}
        />
      )}
    </Container>
  );
};
export default QueryTableWithListingPei;
