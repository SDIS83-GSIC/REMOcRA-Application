import { ReactNode } from "react";
import { Container } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { hasDroit } from "../../droits.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import VRAI_FAUX from "../../enums/VraiFauxEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";
import { useAppContext } from "../App/AppProvider.tsx";
import CreateButton from "../Button/CreateButton.tsx";
import PageTitle from "../Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../Filter/FilterInput.tsx";
import SelectFilterFromList from "../Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../Form/SelectEnumOption.tsx";
import {
  ActionColumn,
  BooleanColumn,
  ProtectedColumn,
} from "../Table/columns.tsx";
import QueryTable, { useFilterContext } from "../Table/QueryTable.tsx";
import { ButtonType, TYPE_BUTTON } from "../Table/TableActionColumn.tsx";
import FilterValues from "./FilterNomenclature.tsx";

const ListNomenclature = ({
  pageTitle,
  pageIcon,
  typeNomenclature,
  lienPageAjout,
  hasProtectedValue = true,
  listeFk,
  libelleFk,
  lienPageUpdate,
  addButtonTitle,
}: {
  pageTitle: string;
  pageIcon: ReactNode;
  typeNomenclature: NOMENCLATURE;
  hasProtectedValue?: boolean;
  listeFk?: IdCodeLibelleType[];
  libelleFk?: string;
  lienPageAjout: any;
  lienPageUpdate: any;
  addButtonTitle: string;
}) => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const navigate = useNavigate();
  const location = useLocation();

  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: lienPageUpdate,
      onClick: (nomenclatureId) =>
        navigate(lienPageUpdate(nomenclatureId), {
          state: {
            ...location.state,
            hasProtectedValue: hasProtectedValue,
            listeFk: listeFk,
            libelleFk: libelleFk,
          },
        }),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      disable: (v) => {
        // return hasProtectedValue ? v.original.protected : false;
        return (
          (hasProtectedValue && v.original.protected) ||
          v.original.tablesDependantes?.length > 0
        );
      },
      textDisableFunction: (v) => {
        if (hasProtectedValue && v.original.protected) {
          return "Impossible de supprimer un élément protégé";
        } else {
          return (
            "Impossible de supprimer l'élément car il est utilisé dans les tables suivantes : " +
            v.original.tablesDependantes?.join(", ")
          );
        }
      },
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      path: url`/api/nomenclature/` + typeNomenclature + "/delete/",
    });
  }

  const colonneFk = listeFk
    ? [
        {
          Header: libelleFk,
          accessor: "libelleFk",
          sortField: "libelleFk",
          Filter: (
            <SelectFilterFromList name={"idFk"} listIdCodeLibelle={listeFk} />
          ),
        },
      ]
    : [];

  return (
    <Container>
      <PageTitle
        title={pageTitle}
        icon={pageIcon}
        urlRetour={URLS.MODULE_ADMIN}
        right={
          <CreateButton
            title={addButtonTitle}
            href={lienPageAjout}
            onClick={() =>
              navigate(lienPageAjout, {
                state: {
                  ...location.state,
                  hasProtectedValue: hasProtectedValue,
                  listeFk: listeFk,
                  libelleFk: libelleFk,
                },
              })
            }
          />
        }
      />
      <QueryTable
        filterValuesToVariable={FilterValues}
        query={url`/api/nomenclature/` + typeNomenclature + "/get"}
        columns={[
          {
            Header: "Code",
            accessor: "code",
            sortField: "code",
            Filter: <FilterInput type="text" name="code" />,
          },
          {
            Header: "Libellé",
            accessor: "libelle",
            sortField: "libelle",
            Filter: <FilterInput type="text" name="libelle" />,
          },
          BooleanColumn({
            Header: "Actif",
            accessor: "actif",
            sortField: "actif",
            Filter: <SelectEnumOption options={VRAI_FAUX} name={"actif"} />,
          }),
          ...colonneFk,
          ...(hasProtectedValue
            ? [
                ProtectedColumn({
                  Header: "Protégé",
                  accessor: "protected",
                  sortField: "protected",
                  Filter: (
                    <SelectEnumOption options={VRAI_FAUX} name={"protected"} />
                  ),
                }),
              ]
            : []),
          ActionColumn({
            Header: "Actions",
            accessor: "id",
            buttons: listeButton,
          }),
        ]}
        idName="ListNomenclature"
        filterContext={useFilterContext({
          code: undefined,
          libelle: undefined,
          actif: undefined,
          protected: undefined,
        })}
      />
    </Container>
  );
};

export default ListNomenclature;
