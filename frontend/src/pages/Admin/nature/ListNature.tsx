import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectNomenclaturesFilter from "../../../components/Filter/SelectNomenclaturesFilter.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import {
  ActionColumn,
  BooleanColumn,
  ProtectedColumn,
} from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import {
  ButtonType,
  TYPE_BUTTON,
} from "../../../components/Table/TableActionColumn.tsx";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import { NOMENCLATURE } from "../../../enums/NomenclaturesEnum.tsx";
import TypePeiEnum from "../../../enums/TypePeiEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import filterValuesNature from "./FilterNature.tsx";

const ListNature = () => {
  const { user } = useAppContext();

  const diametreState = useGet(url`/api/nomenclatures/diametre`);

  const colonne = [
    {
      Header: "Libellé",
      accessor: "natureLibelle",
      sortField: "natureLibelle",
      Filter: <FilterInput type="text" name="natureLibelle" />,
    },
    {
      Header: "Type",
      accessor: "natureTypePei",
      sortField: "natureTypePei",
      Filter: <SelectEnumOption options={TypePeiEnum} name={"natureTypePei"} />,
    },
    {
      Header: "Code",
      accessor: "natureCode",
      sortField: "natureCode",
      Filter: <FilterInput type="text" name="natureCode" />,
    },
    {
      Header: "Diamètres associés",
      accessor: ({ diametreIds }) => {
        return (
          diametreIds?.map(
            (e) =>
              diametreState?.data &&
              Object.values(diametreState.data)?.find((r) => r.diametreId === e)
                ?.diametreLibelle,
          ) ?? []
        ).join(", ");
      },
      Filter: (
        <SelectNomenclaturesFilter
          name={"diametreId"}
          nomenclature={NOMENCLATURE.DIAMETRE}
        />
      ),
    },
    BooleanColumn({
      Header: "Actif",
      accessor: "natureActif",
      sortField: "natureActif",
      Filter: <SelectEnumOption name="natureActif" options={VRAI_FAUX} />,
    }),
    ProtectedColumn({
      Header: "Protégé",
      accessor: "natureProtected",
      sortField: "natureProtected",
      Filter: <SelectEnumOption name="natureProtected" options={VRAI_FAUX} />,
    }),
  ];

  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.ADMIN_DROITS)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.UPDATE,
      route: (data) => URLS.UPDATE_NATURE(data),
    });
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/nature/delete/`,
    });
  }

  colonne.push(
    ActionColumn({
      Header: "Actions",
      accessor: "natureId",
      buttons: listeButton,
    }),
  );
  return (
    <>
      <Container>
        <PageTitle
          title="Liste des natures"
          icon={<IconPei />}
          right={
            <CreateButton title={"Ajouter une nature"} href={URLS.ADD_NATURE} />
          }
        />
        <QueryTable
          query={url`/api/nature/get`}
          filterValuesToVariable={filterValuesNature}
          filterContext={useFilterContext({
            natureActif: undefined,
            natureCode: undefined,
            natureLibelle: undefined,
            diametreIds: undefined,
            natureTypePei: undefined,
            natureProtected: undefined,
          })}
          idName={"ListNature"}
          columns={colonne}
        />
      </Container>
    </>
  );
};
export default ListNature;
