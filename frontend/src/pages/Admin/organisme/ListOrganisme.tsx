import { Button, Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconAddContact,
  IconGererContact,
  IconPei,
} from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "../../../Entities/UtilisateurEntity.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import filterValuesOrganisme from "./FilterOrganisme.tsx";

const ListOrganisme = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.ADMIN_DROITS)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (organismeId) => URLS.UPDATE_ORGANISME(organismeId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (organismeId) => URLS.ADD_CONTACT(organismeId, "organisme"),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconAddContact />,
      textEnable: "Ajouter un contact",
      classEnable: "warning",
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (organismeId) => URLS.LIST_CONTACT(organismeId, "organisme"),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconGererContact />,
      textEnable: "Afficher les contacts",
      textDisable: "Aucun contact pour cet organisme",
      disable: (row) => !row.original.hasContact,
      classEnable: "warning",
    });
  }
  return (
    <>
      <PageTitle title="Liste des organismes" icon={<IconPei />} />
      <Container>
        <Button
          type="button"
          variant="primary"
          href={URLS.ADD_ORGANISME}
          className="mb-1"
        >
          Ajouter un organisme
        </Button>
        <QueryTable
          query={url`/api/organisme/get`}
          filterValuesToVariable={filterValuesOrganisme}
          filterContext={useFilterContext({ blob: "blob" })}
          idName={"ListOrganisme"}
          columns={[
            {
              Header: "Code",
              accessor: "organismeCode",
              sortField: "organismeCode",
              Filter: <FilterInput type="text" name="organismeCode" />,
            },
            {
              Header: "Libellé",
              accessor: "organismeLibelle",
              sortField: "organismeLibelle",
              Filter: <FilterInput type="text" name="organismeLibelle" />,
            },
            {
              Header: "Email principal",
              accessor: "organismeEmailContact",
              sortField: "organismeEmailContact",
              Filter: <FilterInput type="text" name="organismeEmailContact" />,
            },
            {
              Header: "Type Organisme",
              accessor: "typeOrganismeLibelle",
              sortField: "typeOrganismeLibelle",
              Filter: <FilterInput type="text" name="typeOrganismeLibelle" />,
            },
            {
              Header: "Profil",
              accessor: "profilOrganismeLibelle",
              sortField: "profilOrganismeLibelle",
              Filter: <FilterInput type="text" name="profilOrganismeLibelle" />,
            },
            {
              Header: "Zone de compétence",
              accessor: "zoneIntegrationLibelle",
              sortField: "zoneIntegrationLibelle",
              Filter: <FilterInput type="text" name="zoneIntegrationLibelle" />,
            },
            {
              Header: "Organisme parent",
              accessor: "parentLibelle",
              sortField: "parentLibelle",
              Filter: <FilterInput type="text" name="parentLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "organismeActif",
              sortField: "organismeActif",
              Filter: (
                <SelectEnumOption name="organismeActif" options={VRAI_FAUX} />
              ),
            }),
            ActionColumn({
              Header: "Actions",
              accessor: "organismeId",
              buttons: listeButton,
            }),
          ]}
        />
      </Container>
    </>
  );
};
export default ListOrganisme;
