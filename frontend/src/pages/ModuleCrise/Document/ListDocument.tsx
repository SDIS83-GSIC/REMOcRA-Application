import { Button, Container } from "react-bootstrap";
import { IconExport } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "../Crise/FilterCrise.tsx";
import { shortenString } from "../../../utils/fonctionsUtils.tsx";

const ListDocument = ({ criseId }: { criseId: string }) => {
  return (
    <Container>
      <QueryTable
        query={url`/api/crise/documents/getAllFromCrise/${criseId}`}
        columns={[
          {
            Header: "Origine",
            accessor: "type",
            sortField: "type",
          },
          {
            Header: "Libellé",
            accessor: "documentNomFichier",
            sortField: "documentNomFichier",
            Cell: (value) => {
              return <div>{shortenString(value.value, 20)}</div>;
            },
          },
          {
            Header: "Mise à jour le",
            accessor: "documentDate",
            sortField: "documentDate",
            Cell: (value) => {
              return (
                <div>{value?.value != null && formatDateTime(value.value)}</div>
              );
            },
          },

          {
            Header: "Actions",
            accessor: "documentId",
            Cell: (value) => {
              return (
                <div>
                  <Button
                    style={{ backgroundColor: "transparent", border: "none" }}
                    className={"text-warning"}
                    href={url`/api/documents/telecharger/` + value.value}
                  >
                    <IconExport />
                  </Button>
                </div>
              );
            },
          },
        ]}
        idName={"tableModuleDocument"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          documentDate: undefined,
          documentNomFichier: undefined,
          type: undefined,
        })}
      />
    </Container>
  );
};

export default ListDocument;
