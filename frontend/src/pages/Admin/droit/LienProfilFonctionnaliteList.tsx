import Container from "react-bootstrap/Container";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import { IconInfo, IconList } from "../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { TYPE_BUTTON } from "../../../components/Table/TableActionColumn.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";

// Définition du type pour une ligne de la table
type LienProfilFonctionnaliteRow = {
  original: {
    profilOrganismeId: string;
    profilUtilisateurId: string;
    groupeFonctionnalitesId: string;
    profilOrganismeLibelle?: string;
    profilUtilisateurLibelle?: string;
    groupeFonctionnalitesLibelle?: string;
  };
};

const LienProfilFonctionnaliteList = () => {
  return (
    <Container>
      <PageTitle
        icon={<IconList />}
        title={
          <>
            Liens profils / groupes de fonctionnalités
            <TooltipCustom
              tooltipText={
                <>
                  Un lien profil / groupe de fonctionnalités permet de définir
                  des combinaisons possibles pour affecter des droits (groupe de
                  fonctionnalités) à un utilisateur (profil utilisateur) qui est
                  forcément rattaché à un organisme (profil organisme).
                  L&apos;édition d&apos;un utilisateur permettra, en fonction du
                  profil utilisateur et de l&apos;organisme sélectionnés, de lui
                  affecter les groupes de fonctionnalités disponibles pour cette
                  combinaison.
                </>
              }
              tooltipId={"tooltip-lien-profil-fonctionnalite"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
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
            accessor: "groupeFonctionnalitesLibelle",
            sortField: "fonctionnalite",
            Filter: <FilterInput name="fonctionnalite" type="text" />,
          },
          ActionColumn({
            Header: "Actions",
            accessor: (row: LienProfilFonctionnaliteRow) => row,
            buttons: [
              {
                row: (row: LienProfilFonctionnaliteRow) => row,
                route: ({ profilOrganismeId, profilUtilisateurId }) =>
                  URLS.LIEN_PROFIL_FONCTIONNALITE_UPDATE({
                    profilOrganismeId,
                    profilUtilisateurId,
                  }),
                type: TYPE_BUTTON.UPDATE,
              },
              {
                row: (row: LienProfilFonctionnaliteRow) => row,
                type: TYPE_BUTTON.DELETE,
                pathname: (row: LienProfilFonctionnaliteRow) =>
                  `/api/lien-profil-fonctionnalite/delete/${row.original.profilOrganismeId}/${row.original.profilUtilisateurId}/${row.original.groupeFonctionnalitesId}`,
                textEnable:
                  "Attention : supprimer ce lien retirera l’accès au groupe de fonctionnalités pour tous les utilisateurs associés à ce profil et cet organisme. Cela peut entraîner la perte de droits d’accès à l’application.",
                content: (_: LienProfilFonctionnaliteRow) => (
                  <div className="fs-6">
                    <div className="fw-bold mb-3">
                      Êtes-vous sûr de vouloir supprimer ce lien ?
                    </div>
                    <div className="mb-3">
                      Cette action retirera l’accès au groupe de fonctionnalités
                      pour tous les utilisateurs associés à ce profil et cet
                      organisme. Cela peut entraîner la perte de droits d’accès
                      à l’application.
                    </div>
                    <div className="fw-bold">
                      Cette action est irréversible.
                    </div>
                  </div>
                ),
              },
            ],
          }),
        ]}
        idName={"lien-groupe-fonctionnalites"}
      />
    </Container>
  );
};

export default LienProfilFonctionnaliteList;
