import { Button, Container } from "react-bootstrap";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconPei } from "../../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import url from "../../../module/fetch.tsx";
import EditColumn, {
  BooleanColumn,
} from "../../../components/Table/columns.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import filterValuesOrganisme from "./FilterOrganisme.tsx";

const ListOrganisme = () => {
  return (
    <>
      <PageTitle title="Liste des organismes" icon={<IconPei />} />
      <Container>
        <Button
          type="button"
          variant="primary"
          href={URLS.ADD_ORGANISME}
          className="mb-1"
        >
          Ajouter un organisme
        </Button>
        <QueryTable
          query={url`/api/organisme/get`}
          filterValuesToVariable={filterValuesOrganisme}
          filterContext={useFilterContext({ blob: "blob" })}
          idName={"ListOrganisme"}
          columns={[
            {
              Header: "Code",
              accessor: "organismeCode",
              sortField: "organismeCode",
              Filter: <FilterInput type="text" name="organismeCode" />,
            },
            {
              Header: "Libellé",
              accessor: "organismeLibelle",
              sortField: "organismeLibelle",
              Filter: <FilterInput type="text" name="organismeLibelle" />,
            },
            {
              Header: "Email principal",
              accessor: "organismeEmailContact",
              sortField: "organismeEmailContact",
              Filter: <FilterInput type="text" name="organismeEmailContact" />,
            },
            {
              Header: "Type Organisme",
              accessor: "typeOrganismeLibelle",
              sortField: "typeOrganismeLibelle",
              Filter: <FilterInput type="text" name="typeOrganismeLibelle" />,
            },
            {
              Header: "Profil",
              accessor: "profilOrganismeLibelle",
              sortField: "profilOrganismeLibelle",
              Filter: <FilterInput type="text" name="profilOrganismeLibelle" />,
            },
            {
              Header: "Zone de compétence",
              accessor: "zoneIntegrationLibelle",
              sortField: "zoneIntegrationLibelle",
              Filter: <FilterInput type="text" name="zoneIntegrationLibelle" />,
            },
            {
              Header: "Organisme parent",
              accessor: "parentLibelle",
              sortField: "parentLibelle",
              Filter: <FilterInput type="text" name="parentLibelle" />,
            },
            BooleanColumn({
              Header: "Actif",
              accessor: "organismeActif",
              sortField: "organismeActif",
              Filter: (
                <SelectEnumOption name="organismeActif" options={VRAI_FAUX} />
              ),
            }),
            EditColumn({
              to: (data) => URLS.UPDATE_ORGANISME(data),
              accessor: "organismeId",
              title: false,
              canEdit: true, // TODO mettre les droits
            }),
          ]}
        />
      </Container>
    </>
  );
};
export default ListOrganisme;
