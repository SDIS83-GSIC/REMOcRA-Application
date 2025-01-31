import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconAddContact,
  IconGererContact,
  IconList,
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
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValues from "./FilterGestionnaire.tsx";

const ListGestionnaire = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.GEST_SITE_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (gestionnaireId) => URLS.UPDATE_GESTIONNAIRE(gestionnaireId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      textDisable: "Ce gestionnaire a des contacts.",
      disable: (row) => row.original.hasContact,
      type: TYPE_BUTTON.DELETE,
      path: url`/api/gestionnaire/delete/`,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (gestionnaireId) =>
        URLS.ADD_CONTACT(gestionnaireId, "gestionnaire"),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconAddContact />,
      textEnable: "Ajouter un contact",
      classEnable: "warning",
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (gestionnaireId) =>
        URLS.LIST_CONTACT(gestionnaireId, "gestionnaire"),
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconGererContact />,
      textEnable: "Afficher les contacts",
      textDisable: "Aucun contact pour ce gestionnaire",
      disable: (row) => !row.original.hasContact,
      classEnable: "warning",
    });
  }
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Liste des gestionnaires"}
          right={
            hasDroit(user, TYPE_DROIT.GEST_SITE_A) && (
              <CreateButton
                href={URLS.ADD_GESTIONNAIRE}
                title={"Ajouter un gestionnaire"}
              />
            )
          }
        />
        <QueryTable
          query={url`/api/gestionnaire`}
          columns={[
            {
              Header: "Code",
              accessor: "gestionnaireCode",
              sortField: "gestionnaireCode",
              Filter: <FilterInput type="text" name="gestionnaireCode" />,
            },
            {
              Header: "Libellé",
              accessor: "gestionnaireLibelle",
              sortField: "gestionnaireLibelle",
              Filter: <FilterInput type="text" name="gestionnaireLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "gestionnaireActif",
              sortField: "gestionnaireActif",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"gestionnaireActif"}
                />
              ),
            }),
            ActionColumn({
              Header: "Actions",
              accessor: "gestionnaireId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableGestionnaire"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({
            gestionnaireCode: undefined,
            gestionnaireLibelle: undefined,
            gestionnaireActif: undefined,
          })}
        />
      </Container>
    </>
  );
};

export default ListGestionnaire;
