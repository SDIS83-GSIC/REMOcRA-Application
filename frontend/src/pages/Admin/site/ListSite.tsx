import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import { BooleanColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import FilterValues from "./FilterSite.tsx";

const ListSite = () => {
  const { data } = useGet(url`/api/gestionnaire/get`);

  return (
    <>
      <Container>
        <PageTitle icon={<IconList />} title={"Liste des sites"} />
        <QueryTable
          query={url`/api/site`}
          columns={[
            {
              Header: "Code",
              accessor: "siteCode",
              sortField: "siteCode",
              Filter: <FilterInput type="text" name="siteCode" />,
            },
            {
              Header: "Libellé",
              accessor: "siteLibelle",
              sortField: "siteLibelle",
              Filter: <FilterInput type="text" name="siteLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "siteActif",
              sortField: "siteActif",
              Filter: (
                <SelectEnumOption options={VRAI_FAUX} name={"siteActif"} />
              ),
            }),

            {
              Header: "Gestionnaire",
              accessor: "gestionnaireLibelle",
              sortField: "gestionnaireLibelle",
              Filter: (
                <SelectFilterFromList
                  name={"siteGestionnaireId"}
                  listIdCodeLibelle={data}
                />
              ),
            },
          ]}
          idName={"tableSite"}
          filterValuesToVariable={FilterValues}
          filterContext={useFilterContext({
            siteCode: undefined,
            siteLibelle: undefined,
            siteActif: undefined,
            siteGestionnaireId: undefined,
          })}
        />
      </Container>
    </>
  );
};

export default ListSite;
