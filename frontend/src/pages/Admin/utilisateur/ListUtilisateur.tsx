import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconGererContact } from "../../../components/Icon/Icon.tsx";
import { BooleanColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import FilterValues from "./FilterUtilisateur.tsx";

const ListUtilisateur = () => {
  const { data: organismeList } = useGet(url`/api/organisme/get-all`);
  const { data: profilDroitList } = useGet(url`/api/profil-droit`);
  const { data: profilUtilisateurList } = useGet(url`/api/profil-utilisateur`);

  return (
    <>
      <Container fluid className={"px-5"}>
        <PageTitle
          icon={<IconGererContact />}
          title={"Liste des utilisateurs"}
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
              Header: "Peut être notifier ?",
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
