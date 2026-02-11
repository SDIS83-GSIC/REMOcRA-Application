import { useEffect, useState } from "react";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import { useGetRun } from "../../components/Fetch/useFetch.tsx";
import { ItemSearch } from "../../components/Localisation/useLocalisation.tsx";
import url from "../../module/fetch.tsx";

export type IdLibelleData = {
  id: string;
  libelle: string;
};

const AccesRapideTypeahead = ({
  label,
  queryUrl,
  setter,
  dependentObject,
}: {
  label: string;
  queryUrl: string;
  setter: (value: string | null) => void;
  dependentObject?: ItemSearch | null;
}) => {
  const [motif, setMotif] = useState<string>("");
  const queryParams: Record<string, any> = { motifLibelle: motif };

  if (dependentObject) {
    queryParams.dependenceObjId = dependentObject.id;
  }

  const { data, run, isResolved } = useGetRun(
    url`${queryUrl}?${new URLSearchParams(queryParams).toString()}`,
    {},
  );

  useEffect(run, [run]);

  return (
    <AsyncTypeahead
      minLength={2}
      placeholder={label}
      emptyLabel={"Aucun résultat"}
      promptText={"Saisissez au moins 2 lettres"}
      searchText={"Recherche en cours"}
      isLoading={!isResolved}
      labelKey={"libelle"}
      options={data}
      onSearch={(query) => {
        if (query.length > 0) {
          setMotif(query);
        }
      }}
      onChange={(value) => {
        if (value.length > 0) {
          setter(value[0]);
        } else {
          setter(null);
        }
      }}
    />
  );
};

export default AccesRapideTypeahead;
