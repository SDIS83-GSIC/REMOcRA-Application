import { Alert, Container } from "react-bootstrap";
import { IconInfo, IconMapComponent } from "../../../components/Icon/Icon.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import url from "../../../module/fetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { BooleanColumn } from "../../../components/Table/columns.tsx";
import filterValuesToVariable from "./FilterGroupeCouche.tsx";

const ListGroupeCouche = () => {
  return (
    <Container>
      <PageTitle title="Groupes de couches" icon={<IconMapComponent />} />
      <Alert
        variant="info"
        className="mt-2 mb-2 text-muted d-flex align-items-center"
      >
        <span>
          <IconInfo /> Les couches cartographiques sont accessibles et
          paramétrables au sein d&apos;un groupe, leur gestion se fait donc au
          travers de leur groupe d&apos;appartenance.
        </span>
      </Alert>
      <QueryTable
        query={url`/api/admin/groupe-couche`}
        columns={[
          {
            Header: "Code",
            accessor: "groupeCoucheCode",
            sortField: "groupeCoucheCode",
            Filter: <FilterInput type="text" name="groupeCoucheCode" />,
          },
          {
            Header: "Libellé",
            accessor: "groupeCoucheLibelle",
            sortField: "groupeCoucheLibelle",
            Filter: <FilterInput type="text" name="groupeCoucheLibelle" />,
          },
          {
            Header: "Nombre de couches associées",
            accessor: "nombreCouche",
            sortField: "nombreCouche",
          },
          BooleanColumn({
            Header: "Protégé",
            accessor: "groupeCoucheProtected",
            sortField: "groupeCoucheProtected",
            Filter: (
              <SelectEnumOption
                options={VRAI_FAUX}
                name={"groupeCoucheProtected"}
              />
            ),
          }),
        ]}
        idName={"tableGroupeCouche"}
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          groupeCoucheLibelle: undefined,
          groupeCoucheProtected: undefined,
        })}
      />
    </Container>
  );
};

export default ListGroupeCouche;
