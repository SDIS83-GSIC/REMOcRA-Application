import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import {
  IconAddContact,
  IconCreateApi,
  IconGererContact,
  IconPei,
  IconRegenereApi,
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
import filterValuesOrganisme from "./FilterOrganisme.tsx";

const ListOrganisme = () => {
  const { user } = useAppContext();

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.ADMIN_DROITS)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (organismeId) => URLS.UPDATE_ORGANISME(organismeId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (organismeId) => URLS.ADD_CONTACT(organismeId, "organisme"),
      type: TYPE_BUTTON.LINK,
      icon: <IconAddContact />,
      textEnable: "Ajouter un contact",
      classEnable: "warning",
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (organismeId) => URLS.LIST_CONTACT(organismeId, "organisme"),
      type: TYPE_BUTTON.LINK,
      icon: <IconGererContact />,
      textEnable: "Afficher les contacts",
      textDisable: "Aucun contact pour cet organisme",
      disable: (row) => !row.original.hasContact,
      classEnable: "warning",
    });
  }

  if (hasDroit(user, TYPE_DROIT.ADMIN_API)) {
    // TODO alimenter le contenu des modales pour expliquer comment on notifiera un organisme
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      hide: (row) => row.organismeKeycloakId != null,
      textEnable: "Créer un accès API",
      pathname: url`/api/organisme/create-client-keycloak/`,
      icon: <IconCreateApi />,
      classEnable: "warning",
      confirmModal: {
        header: "Créer un accès API ?",
        content:
          "Vous allez créer un accès API pour l'organisme sélectionné. Il recevra un mail avec le mot de passe qu'il lui est associé.\nVoulez-vous continuer ? ",
      },
    });
    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.CONFIRM,
      hide: (row) => row.organismeKeycloakId == null,
      textEnable: "Regénérer un accès API",
      pathname: url`/api/organisme/regenerer-client-secret/`,
      icon: <IconRegenereApi />,
      classEnable: "warning",
      confirmModal: {
        header: "Régénérer le mot de passe de l'API ?",
        content:
          "Vous allez regénérer le mot de passe de l'API pour l'organisme sélectionné. Il recevra un mail avec le nouevau mot de passe qu'il lui est associé.\nVoulez-vous continuer ? ",
      },
    });
  }
  return (
    <>
      <Container>
        <PageTitle
          title="Organismes"
          icon={<IconPei />}
          right={
            <CreateButton
              title={"Ajouter un organisme"}
              href={URLS.ADD_ORGANISME}
            />
          }
        />
      </Container>
      <Container fluid className={"px-5"}>
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
