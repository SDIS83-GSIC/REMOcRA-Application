import { Button, Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import EditColumn, {
  BooleanColumn,
  DeleteColumn,
  ProtectedColumn,
} from "../../../components/Table/columns.tsx";
import url from "../../../module/fetch.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { URLS } from "../../../routes.tsx";
import FilterValuesDiametre from "./FilterDiametre.tsx";

const ListDiametre = () => {
  return (
    <>
      <PageTitle title="Liste des diamètres" icon={<IconPei />} />
      <Container>
        <Button
          type="button"
          variant="primary"
          href={URLS.ADD_DIAMETRE}
          className="mb-1"
        >
          Ajouter un diamètre
        </Button>
        <QueryTable
          filterValuesToVariable={FilterValuesDiametre}
          query={url`/api/diametre/get`}
          columns={[
            {
              Header: "Code",
              accessor: "diametreCode",
              sortField: "diametreCode",
              Filter: <FilterInput type="text" name="diametreCode" />,
            },
            {
              Header: "Libellé",
              accessor: "diametreLibelle",
              sortField: "diametreLibelle",
              Filter: <FilterInput type="text" name="diametreLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "diametreActif",
              sortField: "diametreActif",
              Filter: (
                <SelectEnumOption options={VRAI_FAUX} name={"diametreActif"} />
              ),
            }),
            ProtectedColumn({
              Header: "Protégé",
              accessor: "diametreProtected",
              sortField: "diametreProtected",
              Filter: (
                <SelectEnumOption
                  options={VRAI_FAUX}
                  name={"diametreProtected"}
                />
              ),
            }),
            EditColumn({
              to: (data) => URLS.UPDATE_DIAMETRE(data),
              accessor: "diametreId",
              title: false,
              canEditFunction() {
                // TODO Ajouter la gestion des droits
                return true;
              },
              disable: (v) => {
                return v.original.diametreProtected;
              },
            }),
            DeleteColumn({
              path: url`/api/diametre/delete/`,
              title: false,
              canSupress: true,
              accessor: "diametreId",
              disable: (v) => {
                return v.original.diametreProtected;
              },
            }),
          ]}
          idName="ListDiametre"
          filterContext={useFilterContext({
            diametreCode: undefined,
            diametreLibelle: undefined,
            diametreActif: undefined,
          })}
        />
      </Container>
    </>
  );
};

export default ListDiametre;
