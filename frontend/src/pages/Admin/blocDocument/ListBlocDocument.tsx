import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import CreateButton from "../../../components/Form/CreateButton.tsx";
import { IconExport, IconList } from "../../../components/Icon/Icon.tsx";
import { ActionColumn } from "../../../components/Table/columns.tsx";
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
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import FilterValues from "./FilterBlocDocument.tsx";

const ListBlocDocument = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const thematiqueState = useGet(url`/api/thematique/`);
  const profilDroitState = useGet(url`/api/profil-droit`);
  const listeButton: ButtonType[] = [];

  if (hasDroit(user, TYPE_DROIT.DOCUMENTS_A)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (blocDocumentId) => URLS.UPDATE_BLOC_DOCUMENT(blocDocumentId),
      type: TYPE_BUTTON.UPDATE,
    });

    listeButton.push({
      row: (row) => {
        return row;
      },
      type: TYPE_BUTTON.DELETE,
      path: url`/api/bloc-document/delete/`,
    });
  }

  if (hasDroit(user, TYPE_DROIT.DOCUMENTS_R)) {
    listeButton.push({
      row: (row) => {
        return row;
      },
      href: (blocDocumentId) =>
        url`/api/bloc-document/telecharger/` + blocDocumentId,
      type: TYPE_BUTTON.CUSTOM,
      icon: <IconExport />,
      textEnable: "Télécharger le document",
      classEnable: "warning",
    });
  }
  return (
    <>
      <Container>
        <PageTitle
          icon={<IconList />}
          title={"Liste des blocs documents"}
          right={
            hasDroit(user, TYPE_DROIT.DOCUMENTS_A) && (
              <CreateButton
                href={URLS.ADD_BLOC_DOCUMENT}
                title={"Ajouter un bloc document"}
              />
            )
          }
        />
        <QueryTable
          query={url`/api/bloc-document/`}
          columns={[
            {
              Header: "Libellé",
              accessor: "blocDocumentLibelle",
              sortField: "blocDocumentLibelle",
              Filter: <FilterInput type="text" name="blocDocumentLibelle" />,
            },
            {
              Header: "Thématiques",
              accessor: "listeThematique",
              Filter: (
                <MultiSelectFilterFromList
                  name={"listThematiqueId"}
                  listIdCodeLibelle={thematiqueState.data}
                />
              ),
            },
            {
              Header: "Profils droits",
              accessor: "listeProfilDroit",
              Filter: (
                <MultiSelectFilterFromList
                  name={"listProfilDroitId"}
                  listIdCodeLibelle={profilDroitState.data}
                />
              ),
            },
            {
              Header: "Mise à jour le",
              accessor: "blocDocumentDateMaj",
              sortField: "blocDocumentDateMaj",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && formatDateTime(value.value)}
                  </div>
                );
              },
            },
            ActionColumn({
              Header: "Actions",
              accessor: "blocDocumentId",
              buttons: listeButton,
            }),
          ]}
          idName={"tableBlocDocument"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({})}
        />
      </Container>
    </>
  );
};

export default ListBlocDocument;
