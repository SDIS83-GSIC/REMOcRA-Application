import { Container } from "react-bootstrap";
import EtudeStatutEnum from "../../Entities/EtudeEntity.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import FilterInput from "../../components/Filter/FilterInput.tsx";
import SelectFilterFromList from "../../components/Filter/SelectFilterFromList.tsx";
import SelectEnumOption from "../../components/Form/SelectEnumOption.tsx";
import { IconEtude } from "../../components/Icon/Icon.tsx";
import QueryTable, {
  useFilterContext,
} from "../../components/Table/QueryTable.tsx";
import url from "../../module/fetch.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";
import filterValuesToVariable from "./FilterEtude.tsx";

const ListEtude = () => {
  const typeEtudeState = useGet(url`/api/couverture-hydraulique/type-etudes`);

  return (
    <>
      <Container>
        <PageTitle icon={<IconEtude />} title={"Liste des études"} />
        <QueryTable
          query={url`/api/couverture-hydraulique`}
          columns={[
            {
              Header: "Type d'étude",
              accessor: "typeEtudeLibelle",
              sortField: "typeEtudeLibelle",
              Filter: typeEtudeState?.data && (
                <SelectFilterFromList
                  listIdCodeLibelle={typeEtudeState?.data}
                  name={"typeEtudeId"}
                />
              ),
            },
            {
              Header: "Numéro",
              accessor: "etudeNumero",
              sortField: "etudeNumero",
              Filter: <FilterInput type="text" name="etudeNumero" />,
            },
            {
              Header: "Nom",
              accessor: "etudeLibelle",
              sortField: "etudeLibelle",
              Filter: <FilterInput type="text" name="etudeLibelle" />,
            },
            {
              Header: "Description",
              accessor: "etudeDescription",
              sortField: "etudeDescription",
              Filter: <FilterInput type="text" name="etudeDescription" />,
            },
            {
              Header: "Statut",
              accessor: "etudeStatut",
              sortField: "etudeStatut",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && EtudeStatutEnum[value.value]}
                  </div>
                );
              },
              Filter: (
                <SelectEnumOption
                  options={EtudeStatutEnum}
                  name={"etudeStatut"}
                />
              ),
            },
            {
              Header: "Communes",
              accessor: "listeCommune",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value
                      ?.map(
                        (e) =>
                          e.communeLibelle + " (" + e.communeCodeInsee + ")",
                      )
                      ?.join(", ")}
                  </div>
                );
              },
            },
            {
              Header: "Date de dernière mise à jour",
              accessor: "etudeDateMaj",
              sortField: "etudeDateMaj",
              Cell: (value) => {
                return (
                  <div>
                    {value?.value != null && formatDateTime(value.value)}
                  </div>
                );
              },
            },
          ]}
          idName={"tableEtudeId"}
          filterValuesToVariable={filterValuesToVariable}
          filterContext={useFilterContext({
            typeEtudeId: undefined,
            etudeNumero: undefined,
            etudeLibelle: undefined,
            etudeDescription: undefined,
            etudeStatut: undefined,
            etudeDateMaj: undefined,
          })}
        />
      </Container>
    </>
  );
};

export default ListEtude;
