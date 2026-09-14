import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
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
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValues from "./FilterGestionnaire.tsx";

const ListGestionnaire = () => {
  const { user } = useAppContext();

  const listeButton: ButtonType[] = [];

  if (
    hasDroit(user, TYPE_DROIT.GEST_SITE_R) ||
    hasDroit(user, TYPE_DROIT.GEST_CONTACT_A)
  ) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (gestionnaireId) =>
        URLS.LIST_CONTACT(gestionnaireId, "gestionnaire"),
      type: TYPE_BUTTON.LINK,
      icon: <IconGererContact />,
      textEnable: "Afficher les contacts",
      textDisable: "Aucun contact pour ce gestionnaire",
      disable: (row) =>
        !row.original.listContact || row.original.listContact.length === 0,
      classEnable: "warning",
    });
  }

  if (hasDroit(user, TYPE_DROIT.GEST_SITE_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (gestionnaireId) => URLS.UPDATE_GESTIONNAIRE(gestionnaireId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      pathname: url`/api/gestionnaire/delete/`,
      header: (row) =>
        "Suppression du gestionnaire " + row.original.gestionnaireLibelle,
      content: (row) => <DeleteContent row={row} />,
    });

    if (hasDroit(user, TYPE_DROIT.GEST_CONTACT_A)) {
      listeButton.push({
        row: (row) => {
          return row;
        },
        route: (gestionnaireId) =>
          URLS.ADD_CONTACT(gestionnaireId, "gestionnaire"),
        type: TYPE_BUTTON.LINK,
        icon: <IconAddContact />,
        textEnable: "Ajouter un contact",
        classEnable: "warning",
      });
    }
  }
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Gestionnaires"}
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

const DeleteContent = ({ row }: { row: any }) => {
  const peis: string[] = row.original.listPei ?? [];
  const contacts: string[] = row.original.listContact ?? [];

  return (
    <>
      {peis.length === 0 ? (
        <p>
          Êtes-vous sûr de vouloir supprimer le gestionnaire{" "}
          {row.original.gestionnaireLibelle} ?
        </p>
      ) : peis.length === 1 ? (
        <p>
          Ce gestionnaire est lié au PEI {peis[0]}.
          <br />
          Le lien sera supprimé mais le PEI sera conservé.
        </p>
      ) : (
        <p>
          Ce gestionnaire est lié aux PEI suivants.
          <br />
          Les liens seront supprimés mais les PEI seront conservés :
          <ul>
            {peis.map((pei, index) => (
              <li key={index}>{pei}</li>
            ))}
          </ul>
        </p>
      )}
      {contacts.length === 1 ? (
        <p>
          Ce gestionnaire est lié au contact {contacts[0]}.
          <br />
          La suppression du gestionnaire entraînera la suppression du contact et
          ses courriers.
        </p>
      ) : contacts.length > 1 ? (
        <p>
          Ce gestionnaire est lié aux contacts suivants.
          <br />
          La suppression du gestionnaire entraînera la suppression de ces
          contacts, et leurs courriers :
          <ul>
            {contacts.map((contact, index) => (
              <li key={index}>{contact}</li>
            ))}
          </ul>
        </p>
      ) : null}
    </>
  );
};
