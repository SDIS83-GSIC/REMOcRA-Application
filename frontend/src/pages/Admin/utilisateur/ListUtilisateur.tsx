import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconGererContact } from "../../../components/Icon/Icon.tsx";
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
import FilterValues from "./FilterUtilisateur.tsx";

const ListUtilisateur = () => {
  const { user } = useAppContext();
  const { data: organismeList } = useGet(url`/api/organisme/get-all`);
  const { data: profilDroitList } = useGet(url`/api/profil-droit`);
  const { data: profilUtilisateurList } = useGet(url`/api/profil-utilisateur`);

  const listeButton: ButtonType[] = [];
  if (hasDroit(user, TYPE_DROIT.ADMIN_UTILISATEURS_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      route: (utilisateurId) => URLS.UPDATE_UTILISATEUR(utilisateurId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      disable: (v) => {
        return v.value === user.utilisateurId;
      },
      textDisable: "Impossible de supprimer votre propre compte.",
      pathname: url`/api/utilisateur/delete/`,
      textEnable:
        "Attention, si l'utilisateur vient d'un annuaire, le compte sera de nouveau présent lors de la synchronisation. Si vous souhaitez le supprimer, supprimez-le de l'annuaire, la synchronisation se chargera ensuite de la suppression dans REMOcRA",
    });
  }

  return (
    <>
      <Container fluid className={"px-5"}>
        <PageTitle
          icon={<IconGererContact />}
          title={"Utilisateurs"}
          right={
            hasDroit(user, TYPE_DROIT.ADMIN_UTILISATEURS_A) && (
              <CreateButton
                href={URLS.ADD_UTILISATEUR}
                title={"Ajouter un utilisateur"}
              />
            )
          }
        />
        <QueryTable
          query={url`/api/utilisateur`}
          columns={[
            {
              Header: "Email",
              accessor: "utilisateurEmail",
              sortField: "utilisateurEmail",
              Filter: <FilterInput type="text" name="utilisateurEmail" />,
            },
            {
              Header: "Identifiant",
              accessor: "utilisateurUsername",
              sortField: "utilisateurUsername",
              Filter: <FilterInput type="text" name="utilisateurUsername" />,
            },
            {
              Header: "Nom",
              accessor: "utilisateurNom",
              sortField: "utilisateurNom",
              Filter: <FilterInput type="text" name="utilisateurNom" />,
            },
            {
              Header: "Prénom",
              accessor: "utilisateurPrenom",
              sortField: "utilisateurPrenom",
              Filter: <FilterInput type="text" name="utilisateurPrenom" />,
            },
            {
              Header: "Téléphone",
              accessor: "utilisateurTelephone",
              sortField: "utilisateurTelephone",
              Filter: <FilterInput type="text" name="utilisateurTelephone" />,
            },
            BooleanColumn({
              Header: "Peut être notifié",
              accessor: "utilisateurCanBeNotified",
              sortField: "utilisateurCanBeNotified",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"utilisateurCanBeNotified"}
                />
              ),
            }),
            {
              Header: "Organisme",
              accessor: "organismeLibelle",
              sortField: "organismeLibelle",
              Filter: (
                <SelectFilterFromList
                  name={"utilisateurOrganismeId"}
                  listIdCodeLibelle={organismeList}
                />
              ),
            },
            {
              Header: "Profil utilisateur",
              accessor: "profilUtilisateurLibelle",
              sortField: "profilUtilisateurLibelle",
              Filter: (
                <SelectFilterFromList
                  name={"utilisateurProfilUtilisateurId"}
                  listIdCodeLibelle={profilUtilisateurList}
                />
              ),
            },
            {
              Header: "Profil droit",
              accessor: "profilDroitLibelle",
              sortField: "profilDroitLibelle",
              Filter: (
                <SelectFilterFromList
                  name={"profilDroitId"}
                  listIdCodeLibelle={profilDroitList}
                />
              ),
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "utilisateurActif",
              sortField: "utilisateurActif",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"utilisateurActif"}
                />
              ),
            }),
            ActionColumn({
              Header: "Actions",
              accessor: "utilisateurId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableUtilisateurId"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({})}
        />
      </Container>
    </>
  );
};

export default ListUtilisateur;
