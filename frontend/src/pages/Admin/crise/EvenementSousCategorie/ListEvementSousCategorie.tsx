import { Container } from "react-bootstrap";
import { useAppContext } from "../../../../components/App/AppProvider.tsx";
import CreateButton from "../../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../../components/Filter/FilterInput.tsx";
import { IconWarningCrise } from "../../../../components/Icon/Icon.tsx";
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
import SelectEnumOption from "../../../../components/Form/SelectEnumOption.tsx";
import VRAI_FAUX from "../../../../enums/VraiFauxEnum.tsx";
import SelectFilterFromUrl from "../../../../components/Filter/SelectFilterFromUrl.tsx";
import SOUS_TYPE_TYPE_GEOMETRIE from "../../../../enums/Signalement/SousTypeTypeGeometrie.tsx";
import filterValuesToVariable from "./filterEvenementSousCategorie.tsx";

const EvenementSousCategorieList = () => {
  const { user } = useAppContext();
  const listeButton: ButtonType[] = [];
  const categorieCode = "MESSAGE";

  if (hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI)) {
    listeButton.push(
      {
        row: (row: any) => {
          return row;
        },
        textEnable: "Modifier",
        route: (sousTypeId) => URLS.UPDATE_EVENEMENT_SOUS_CATEGORIE(sousTypeId),
        type: TYPE_BUTTON.UPDATE,
      },

      {
        disable: (v) => {
          return (
            v.original.evenementsDependants ||
            v.original.typeCriseCategorieCode === categorieCode
          );
        },
        textDisableFunction: () =>
          "Impossible de supprimer l'élément car il est utilisé",
        row: (row: any) => {
          return row;
        },
        type: TYPE_BUTTON.DELETE,
        pathname: url`/api/evenement-sous-categorie/delete/`,
      },
    );
  }

  return (
    <Container>
      <PageTitle
        icon={<IconWarningCrise />}
        title={"Liste des sous catégories d'évènements"}
        right={
          hasDroit(user, TYPE_DROIT.CRISE_C) && (
            <CreateButton
              href={URLS.CREATE_EVENEMENT_SOUS_CATEGORIE}
              title={"Ajouter une sous catégorie"}
            />
          )
        }
      />
      <QueryTable
        query={url`/api/crise/evenement/all-sous-categories`}
        columns={[
          {
            Header: "Code",
            accessor: "evenementSousCategorieCode",
            sortField: "evenementSousCategorieCode",
            Filter: (
              <FilterInput type="text" name="evenementSousCategorieCode" />
            ),
          },
          {
            Header: "Libelle",
            accessor: "evenementSousCategorieLibelle",
            sortField: "evenementSousCategorieLibelle",
            Filter: (
              <FilterInput type="text" name="evenementSousCategorieLibelle" />
            ),
          },
          {
            Header: "Type de Géométrie",
            accessor: "evenementSousCategorieTypeGeometrie",
            sortField: "evenementSousCategorieTypeGeometrie",
            Filter: (
              <SelectEnumOption
                options={SOUS_TYPE_TYPE_GEOMETRIE}
                name={"evenementSousCategorieTypeGeometrie"}
              />
            ),
          },
          BooleanColumn({
            Header: "Actif",
            accessor: "evenementSousCategorieActif",
            sortField: "evenementSousCategorieActif",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"evenementSousCategorieActif"}
              />
            ),
          }),
          {
            Header: "catégorie d'évènement",
            accessor: "evenementCategorieLibelle",
            Filter: (
              <SelectFilterFromUrl
                url={url`/api/crise/get-evenement-category-libelle`}
                name="evenementCategorieId"
              />
            ),
          },
          ActionColumn({
            Header: "Actions",
            accessor: "evenementSousCategorieId",
            buttons: listeButton,
          }),
        ]}
        idName={"evenementSousCategorieId"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          evenementSousCategorieCode: undefined,
          evenementSousCategorieTypeGeometrie: undefined,
          evenementSousCategorieLibelle: undefined,
          evenementSousCategorieActif: undefined,
          evenementCategorieId: undefined,
        })}
      />
    </Container>
  );
};

export default EvenementSousCategorieList;
