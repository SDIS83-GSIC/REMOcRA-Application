import Container from "react-bootstrap/Container";
import url from "../../../module/fetch.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import { URLS } from "../../../routes.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";

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
          },
          {
            accessor: () => <span className={"fst-italic"}>et</span>,
          },
          {
            Header: "Profil utilisateur",
            accessor: "profilUtilisateurLibelle",
            sortField: "utilisateur",
          },
          {
            accessor: () => <span className={"fst-bold"}>→</span>,
          },
          {
            Header: "Groupe de fonctionnalités",
            accessor: "profilDroitLibelle",
            sortField: "fonctionnalite",
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
                row: (row) => {
                  return row;
                },
                href: ({ profilOrganismeId, profilUtilisateurId }) =>
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
