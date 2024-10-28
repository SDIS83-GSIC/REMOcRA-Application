import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import CreateButton from "../../../components/Form/CreateButton.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
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
import TYPE_CIVILITE from "../../../enums/CiviliteEnum.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValues from "./FilterContact.tsx";

const ListContact = () => {
  const { appartenanceId, appartenance } = useParams();
  const { user }: { user: UtilisateurEntity } = useAppContext();

  // TODO vérifier si c'est un gestionnaire pour l'affichage de la colonne site

  const listeButton: ButtonType[] = [];
  if (
    hasDroit(user, TYPE_DROIT.GEST_SITE_A) ||
    hasDroit(user, TYPE_DROIT.ADMIN_DROITS)
  ) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (contactId) =>
        URLS.UPDATE_CONTACT(appartenanceId, contactId, appartenance),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      path: url`/api/contact/` + appartenanceId + `/delete/`,
    });
  }
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Liste des contacts"}
          right={
            (hasDroit(user, TYPE_DROIT.GEST_SITE_A) ||
              hasDroit(user, TYPE_DROIT.ADMIN_DROITS)) && (
              <CreateButton
                href={URLS.ADD_CONTACT(appartenanceId, appartenance)}
                title={"Ajouter un contact"}
              />
            )
          }
        />
        <QueryTable
          query={url`/api/contact/` + appartenanceId}
          columns={[
            BooleanColumn({
              Header: "Actif",
              accessor: "contactActif",
              sortField: "contactActif",
              Filter: (
                <SelectEnumOption options={VRAI_FAUX} name={"contactActif"} />
              ),
            }),
            {
              Header: "Civilité",
              accessor: "contactCivilite",
              sortField: "contactCivilite",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && TYPE_CIVILITE[value.value]}
                  </div>
                );
              },
              Filter: (
                <SelectEnumOption
                  options={TYPE_CIVILITE}
                  name={"contactCivilite"}
                />
              ),
            },
            {
              Header: "Nom",
              accessor: "contactNom",
              sortField: "contactNom",
              Filter: <FilterInput type="text" name="contactNom" />,
            },
            {
              Header: "Prénom",
              accessor: "contactPrenom",
              sortField: "contactPrenom",
              Filter: <FilterInput type="text" name="contactPrenom" />,
            },
            {
              Header: "Fonction",
              accessor: "fonctionContactLibelle",
              sortField: "fonctionContactLibelle",
              Filter: <FilterInput type="text" name="fonctionContactLibelle" />,
            },
            appartenance === "gestionnaire"
              ? {
                  Header: "Site",
                  accessor: "siteLibelle",
                  sortField: "siteLibelle",
                  Filter: <FilterInput type="text" name="siteLibelle" />,
                }
              : {},
            {
              Header: "Téléphone",
              accessor: "contactTelephone",
              sortField: "contactTelephone",
              Filter: <FilterInput type="text" name="contactTelephone" />,
            },
            {
              Header: "Email",
              accessor: "contactEmail",
              sortField: "contactEmail",
              Filter: <FilterInput type="text" name="contactEmail" />,
            },
            ActionColumn({
              Header: "Actions",
              accessor: "contactId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableContact"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({})}
        />
      </Container>
    </>
  );
};

export default ListContact;
