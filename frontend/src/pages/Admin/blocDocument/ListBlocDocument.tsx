import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import MultiSelectFilterFromList from "../../../components/Filter/MultiSelectFilterFromList.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import formatDateTime from "../../../utils/formatDateUtils.tsx";
import FilterValues from "./FilterBlocDocument.tsx";

const ListBlocDocument = () => {
  const thematiqueState = useGet(url`/api/thematique/`);
  const profilDroitState = useGet(url`/api/profil-droit`);
  return (
    <>
      <Container>
        <PageTitle icon={<IconList />} title={"Liste des blocs documents"} />
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
