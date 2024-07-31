import Form from "react-bootstrap/Form";
import { Container } from "react-bootstrap";
import QueryTable, {
  columnType,
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import url from "../../module/fetch.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import { filterValuesToVariable } from "./FilterTournee.tsx";

const GestionTournee = () => {
  const column: Array<columnType> = [
    {
      Header: "Nom",
      accessor: "tourneeLibelle",
      sortField: "tourneeLibelle",
      Filter: <FilterInput type="text" name="tourneeLibelle" />,
    },
    {
      Header: "Nombre de PEI",
      accessor: "tourneeNbPei",
      sortField: "tourneeNbPei",
    },
    {
      Header: "Organisme",
      accessor: "organismeLibelle",
      sortField: "organismeLibelle",
      Filter: <FilterInput type="text" name="tourneeOrganismeLibelle" />,
    },
    {
      Header: "Etat",
      accessor: "tourneeEtat",
      sortField: "tourneeEtat",
    },
    {
      Header: "Réservation",
      accessor: "tourneeUtilisateurReservationLibelle",
      sortField: "tourneeUtilisateurReservationLibelle",
      Filter: (
        <FilterInput type="text" name="tourneeUtilisateurReservationLibelle" />
      ),
    },
    {
      Header: "Actif",
      accessor: "tourneeActif",
      Cell: (value) => {
        return (
          <Form.Check type="checkbox" disabled checked={value.value === true} />
        );
      },
      sortField: "tourneeActif",
    },
  ];

  return (
    <Container>
      <h1>Gestion des tournées</h1>
      <QueryTable
        query={url`/api/tournee`}
        columns={column}
        idName={"TourneeTable"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          tourneeLibelle: undefined,
          tourneeOrganismeLibelle: undefined,
          tourneeUtilisateurReservationLibelle: undefined,
        })}
      />
    </Container>
  );
};

export default GestionTournee;
