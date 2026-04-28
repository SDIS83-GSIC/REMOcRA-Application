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
    canDelete: boolean;
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
        columns={[
          {
            accessor: () => <span className={"fst-italic"}>Quand</span>,
          },
          {
            Header: "Profil organisme",
            accessor: "profilOrganismeLibelle",
            sortField: "organisme",
            Filter: <FilterInput name="profilOrganismeLibelle" type="text" />,
          },
          {
            accessor: () => <span className={"fst-italic"}>et</span>,
          },
          {
            Header: "Profil utilisateur",
            accessor: "profilUtilisateurLibelle",
            sortField: "utilisateur",
            Filter: <FilterInput name="profilUtilisateurLibelle" type="text" />,
          },
          {
            accessor: () => <span className={"fst-bold"}>→</span>,
          },
          {
            Header: "Groupe de fonctionnalités",
            accessor: "groupeFonctionnalitesLibelle",
            sortField: "fonctionnalite",
            Filter: (
              <FilterInput name="groupeFonctionnalitesLibelle" type="text" />
            ),
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
                pathname: (row: object) => {
                  const typedRow = row as LienProfilFonctionnaliteRow;
                  return `/api/lien-profil-fonctionnalite/delete/${typedRow.original.profilOrganismeId}/${typedRow.original.profilUtilisateurId}/${typedRow.original.groupeFonctionnalitesId}`;
                },
                disable: (row: LienProfilFonctionnaliteRow) =>
                  !row.original.canDelete,
                textDisable:
                  "Impossible de supprimer ce lien car il est utilisé par au moins un utilisateur",
              },
            ],
          }),
        ]}
        idName={"lien-groupe-fonctionnalites"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          profilOrganismeLibelle: undefined,
          profilUtilisateurLibelle: undefined,
          groupeFonctionnalitesLibelle: undefined,
        })}
      />
    </Container>
  );
};

export default LienProfilFonctionnaliteList;

type FilterType = {
  groupeFonctionnalitesLibelle?: string;
  profilOrganismeLibelle?: string;
  profilUtilisateurLibelle?: string;
};

const filterValuesToVariable = ({
  groupeFonctionnalitesLibelle,
  profilOrganismeLibelle,
  profilUtilisateurLibelle,
}: FilterType) => {
  const filter: FilterType = {};

  filterProperty(
    filter,
    groupeFonctionnalitesLibelle,
    "groupeFonctionnalitesLibelle",
  );
  filterProperty(filter, profilOrganismeLibelle, "profilOrganismeLibelle");
  filterProperty(filter, profilUtilisateurLibelle, "profilUtilisateurLibelle");

  return filter;
};

function filterProperty(
  filter: FilterType,
  value: string | undefined,
  name: string,
) {
  if (value?.trim().length > 0) {
    filter[name] = value;
  }
}
