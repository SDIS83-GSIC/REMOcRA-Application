import Container from "react-bootstrap/Container";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";

const LienProfilFonctionnaliteList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title={"Liens profils / groupes de fonctionnalités\n"}
        right={
          <CreateButton
            href={URLS.LIEN_PROFIL_FONCTIONNALITE_CREATE}
            title={"Ajouter un lien"}
          />
        }
      />
      <QueryTable
        query={url`/api/lien-profil-fonctionnalite`}
        filterContext={useFilterContext({})}
        columns={[
          {
            accessor: () => <span className={"fst-italic"}>Quand</span>,
          },
          {
            Header: "Profil organisme",
            accessor: "profilOrganismeLibelle",
            sortField: "organisme",
            Filter: <FilterInput name="organisme" type="text" />,
          },
          {
            accessor: () => <span className={"fst-italic"}>et</span>,
          },
          {
            Header: "Profil utilisateur",
            accessor: "profilUtilisateurLibelle",
            sortField: "utilisateur",
            Filter: <FilterInput name="utilisateur" type="text" />,
          },
          {
            accessor: () => <span className={"fst-bold"}>→</span>,
          },
          {
            Header: "Groupe de fonctionnalités",
            accessor: "profilDroitLibelle",
            sortField: "fonctionnalite",
            Filter: <FilterInput name="fonctionnalite" type="text" />,
          },
          ActionColumn({
            Header: "Actions",
            accessor: ({ profilOrganismeId, profilUtilisateurId }) => {
              return {
                profilOrganismeId: profilOrganismeId,
                profilUtilisateurId: profilUtilisateurId,
              };
            },
            buttons: [
              {
                row: (row: any) => {
                  return row;
                },
                route: ({ profilOrganismeId, profilUtilisateurId }) =>
                  URLS.LIEN_PROFIL_FONCTIONNALITE_UPDATE({
                    profilOrganismeId,
                    profilUtilisateurId,
                  }),
                type: TYPE_BUTTON.UPDATE,
              },
            ],
          }),
        ]}
        idName={"lien-profil-droit"}
      />
    </Container>
  );
};

export default LienProfilFonctionnaliteList;
