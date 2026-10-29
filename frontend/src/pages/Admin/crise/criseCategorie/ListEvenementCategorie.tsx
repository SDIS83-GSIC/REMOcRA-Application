import { Container } from "react-bootstrap";
import { useAppContext } from "../../../../components/App/AppProvider.tsx";
import CreateButton from "../../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../../components/Filter/FilterInput.tsx";
import { IconCrise, IconList } from "../../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
} from "../../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../../droits.tsx";
import TYPE_DROIT from "../../../../enums/DroitEnum.tsx";
import url from "../../../../module/fetch.tsx";
import { URLS } from "../../../../routes.tsx";
import filterValuesToVariable from "./filterEvenementCategorie.tsx";

type EvenementCategorieRowType = {
  value: string;
  original: {
    evenementCategorieId: string;
    evenementCategorieCode: string;
    evenementCategorieLibelle: string;
    evenementCategorieActif: boolean;
    evenementCategorieEstUtilisee: boolean;
  };
};

const ListEvenementCategorie = () => {
  const { user } = useAppContext();
  const listeButton: ButtonType[] = [
    {
      row: (row: EvenementCategorieRowType) => {
        return row;
      },
      textEnable: "Accéder aux sous catégories d'évènement",
      route: () => URLS.LIST_EVENEMENT_SOUS_CATEGORIE,
      type: TYPE_BUTTON.LINK,
      icon: <IconList />,
      search: (obj: EvenementCategorieRowType) => {
        return new URLSearchParams({
          filterBy: JSON.stringify({ evenementCategorieId: obj.value }),
        }).toString();
      },
    },
  ];

  if (hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE)) {
    listeButton.push(
      {
        row: (row: EvenementCategorieRowType) => {
          return row;
        },
        textEnable: "Modifier",
        route: (evenementCategorieId: string) =>
          URLS.UPDATE_EVENEMENT_CATEGORIE(evenementCategorieId),
        type: TYPE_BUTTON.UPDATE,
      },
      {
        disable: (v: EvenementCategorieRowType) => {
          return v.original.evenementCategorieEstUtilisee;
        },
        textDisableFunction: () =>
          "Impossible de supprimer l'élément car il est utilisé",
        row: (row: EvenementCategorieRowType) => {
          return row;
        },
        type: TYPE_BUTTON.DELETE,
        pathname: url`/api/evenement-categorie/delete/`,
      },
    );
  }

  return (
    <Container>
      <PageTitle
        icon={<IconCrise />}
        title={"Catégorie d'évènement"}
        right={
          hasDroit(user, TYPE_DROIT.ADMIN_NOMENCLATURE) && (
            <CreateButton
              href={URLS.ADD_EVENEMENT_CATEGORIE}
              title={"Ajouter une catégorie d'évènement"}
            />
          )
        }
      />
      <QueryTable
        query={url`/api/evenement-categorie/`}
        columns={[
          {
            Header: "Code",
            accessor: "evenementCategorieCode",
            sortField: "evenementCategorieCode",
            Filter: <FilterInput type="text" name="evenementCategorieCode" />,
          },
          {
            Header: "Libellé",
            accessor: "evenementCategorieLibelle",
            sortField: "evenementCategorieLibelle",
            Filter: (
              <FilterInput type="text" name="evenementCategorieLibelle" />
            ),
          },
          BooleanColumn({
            Header: "Actif",
            accessor: "evenementCategorieActif",
          }),
          ActionColumn({
            Header: "Actions",
            accessor: "evenementCategorieId",
            buttons: listeButton,
          }),
        ]}
        idName={"evenementCategorieId"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          evenementCategorieCode: undefined,
          evenementCategorieLibelle: undefined,
        })}
      />
    </Container>
  );
};

export default ListEvenementCategorie;
