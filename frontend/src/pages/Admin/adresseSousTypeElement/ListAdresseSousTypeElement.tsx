import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import FilterInput from "../../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../../components/Form/SelectEnumOption.tsx";
import { IconAdresse } from "../../../components/Icon/Icon.tsx";
import { BooleanColumn } from "../../../components/Table/columns.tsx";
import QueryTable, {
  columnType,
  useFilterContext,
} from "../../../components/Table/QueryTable.tsx";
import { referenceTypeGeometrie } from "../../../enums/Adresse/SousTypeTypeGeometrie.tsx";
import VRAI_FAUX from "../../../enums/VraiFauxEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";
import CreateButton from "../../../components/Button/CreateButton.tsx";

const ListAdresseSousTypeElement = () => {
  const listeTypeElement: IdCodeLibelleType[] = useGet(
    url`/api/adresse-sous-type-element/ref`,
  ).data;

  const column: Array<columnType> = [
    {
      Header: "Code",
      accessor: "adresseSousTypeElementCode",
      sortField: "adresseSousTypeElementCode",
      Filter: <FilterInput type="text" name="adresseSousTypeElementCode" />,
    },
    {
      Header: "Libellé",
      accessor: "adresseSousTypeElementLibelle",
      sortField: "adresseSousTypeElementLibelle",
      Filter: <FilterInput type="text" name="adresseSousTypeElementLibelle" />,
    },
    BooleanColumn({
      Header: "Actif",
      accessor: "adresseSousTypeElementActif",
      sortField: "adresseSousTypeElementActif",
      Filter: (
        <SelectEnumOption
          options={VRAI_FAUX}
          name={"adresseSousTypeElementActif"}
        />
      ),
    }),
    {
      Header: "Type élément",
      accessor: "adresseTypeElementLibelle",
      sortField: "adresseTypeElementLibelle",
      Filter: (
        <SelectFilterFromList
          name={"adresseTypeElementId"}
          listIdCodeLibelle={listeTypeElement}
        />
      ),
    },
    {
      Header: "Type géométrie",
      accessor: "adresseSousTypeElementTypeGeometrie",
      sortField: "adresseSousTypeElementTypeGeometrie",
      Cell: (value) => {
        return (
          <div>
            {referenceTypeGeometrie.find((e) => e.code === value.value).libelle}
          </div>
        );
      },
      Filter: (
        <SelectFilterFromList
          name={"adresseSousTypeElementTypeGeometrie"}
          listIdCodeLibelle={referenceTypeGeometrie}
        />
      ),
    },
  ];

  return (
    <Container>
      <PageTitle
        title="Adresse - Sous type élément"
        icon={<IconAdresse />}
        urlRetour={URLS.MODULE_ADMIN}
        right={
          <CreateButton
            title="Ajouter un sous type d'élément"
            href={URLS.ADD_ADRESSE_SOUS_TYPE_ELEMENT}
          />
        }
      />
      <QueryTable
        columns={column}
        query={url`/api/adresse-sous-type-element/get`}
        idName="ListNomenclature"
        filterValuesToVariable={filterValuesToVariable}
        filterContext={useFilterContext({
          adresseSousTypeElementCode: undefined,
          adresseSousTypeElementLibelle: undefined,
          adresseSousTypeElementActif: undefined,
          adresseTypeElementId: undefined,
          adresseSousTypeElementTypeGeometrie: undefined,
        })}
      />
    </Container>
  );
};

export default ListAdresseSousTypeElement;

type filterSousTypeElement = {
  adresseSousTypeElementCode?: string;
  adresseSousTypeElementLibelle?: string;
  adresseSousTypeElementActif?: string;
  adresseTypeElementId?: string;
  adresseSousTypeElementTypeGeometrie?: string;
};

const filterValuesToVariable = ({
  adresseSousTypeElementCode,
  adresseSousTypeElementLibelle,
  adresseSousTypeElementActif,
  adresseTypeElementId,
  adresseSousTypeElementTypeGeometrie,
}: filterSousTypeElement) => {
  const filter: filterSousTypeElement = {};

  if (
    adresseSousTypeElementCode != null &&
    adresseSousTypeElementCode.trim() !== ""
  ) {
    filter.adresseSousTypeElementCode = adresseSousTypeElementCode;
  }
  if (
    adresseSousTypeElementLibelle != null &&
    adresseSousTypeElementLibelle.trim() !== ""
  ) {
    filter.adresseSousTypeElementLibelle = adresseSousTypeElementLibelle;
  }
  if (
    adresseSousTypeElementActif != null &&
    adresseSousTypeElementActif.trim() !== ""
  ) {
    filter.adresseSousTypeElementActif = adresseSousTypeElementActif;
  }
  if (adresseTypeElementId != null && adresseTypeElementId.trim() !== "") {
    filter.adresseTypeElementId = adresseTypeElementId;
  }
  if (
    adresseSousTypeElementTypeGeometrie != null &&
    adresseSousTypeElementTypeGeometrie.trim() !== ""
  ) {
    filter.adresseSousTypeElementTypeGeometrie =
      adresseSousTypeElementTypeGeometrie;
  }

  return filter;
};
