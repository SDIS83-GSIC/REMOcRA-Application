import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconList } from "../../../components/Icon/Icon.tsx";
import { BooleanColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import FilterValues from "./FilterGestionnaire.tsx";

const ListGestionnaire = () => {
  return (
    <>
      <Container>
        <PageTitle icon={<IconList />} title={"Liste des gestionnaires"} />
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
