import { Alert, Container } from "react-bootstrap";
import CreateButton from "../../../components/Button/CreateButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconInfo, IconMapComponent } from "../../../components/Icon/Icon.tsx";
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
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import filterValuesToVariable from "./FilterGroupeCouche.tsx";

const ListGroupeCouche = () => {
  const listeButton: ButtonType[] = [];

  listeButton.push({
    row: (row) => {
      return row;
    },
    type: TYPE_BUTTON.UPDATE,
    route: (data) => URLS.UPDATE_GROUPE_COUCHE(data),
  });

  listeButton.push({
    disable: (v) => {
      return v.original.nombreCouche > 0 || v.original.groupeCoucheProtected;
    },
    textDisableFunction: (v) => {
      if (v.original.groupeCoucheProtected) {
        return "Impossible de supprimer un élément protégé";
      } else {
        return "Impossible de supprimer le groupe de couche car des couches y sont associées";
      }
    },
    row: (row) => {
      return row;
    },
    type: TYPE_BUTTON.DELETE,
    pathname: url`/api/admin/groupe-couche/`,
  });

  return (
    <Container>
      <PageTitle
        title="Groupes de couches"
        icon={<IconMapComponent />}
        right={
          <CreateButton
            title={"Ajouter un groupe de couche"}
            href={URLS.ADD_GROUPE_COUCHE}
          />
        }
      />
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
          ActionColumn({
            Header: "Actions",
            accessor: "groupeCoucheId",
            buttons: listeButton,
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
