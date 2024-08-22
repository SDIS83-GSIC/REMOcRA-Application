import Form from "react-bootstrap/Form";
import { Button, Container } from "react-bootstrap";
import QueryTable, {
  columnType,
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import url from "../../module/fetch.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import { URLS } from "../../routes.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconTournee } from "../../components/Icon/Icon.tsx";
import EditColumn from "../../components/Table/columns.tsx";
import { filterValuesToVariable } from "./FilterTournee.tsx";

const ListTournee = () => {
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

  column.push(
    EditColumn({
      to: (data) => URLS.UPDATE_TOURNEE(data.tourneeId),
      accessor: ({ tourneeId, tourneeUtilisateurReservationLibelle }) => {
        return { tourneeId, tourneeUtilisateurReservationLibelle };
      },
      title: false,
      canEditFunction(data) {
        // TODO Ajouter la gestion des droits
        return data.tourneeUtilisateurReservationLibelle != null ? false : true;
      },
    }),
  );

  return (
    <Container>
      <PageTitle
        icon={<IconTournee />}
        title={"Liste des tournées"}
        right={
          <Button variant="primary" href={URLS.CREATE_TOURNEE}>
            Créer une tournée
          </Button>
        }
      />
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

export default ListTournee;
