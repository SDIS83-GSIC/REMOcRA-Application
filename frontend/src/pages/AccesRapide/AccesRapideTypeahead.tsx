import { useEffect, useState } from "react";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import url from "../../module/fetch.tsx";
import { useGetRun } from "../../components/Fetch/useFetch.tsx";

export type IdLibelleData = {
  id: string;
  libelle: string;
};

const AccesRapideTypeahead = ({
  label,
  queryUrl,
  setter,
}: {
  label: string;
  queryUrl: string;
  setter: (value: string) => void;
}) => {
  const [motif, setMotif] = useState<string>("");
  const { data, run, isResolved } = useGetRun(
    url`${queryUrl}?${{ motifLibelle: motif }}`,
    {},
  );

  useEffect(run, [motif, run]);

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
        if (query.length > 1) {
          setMotif(query);
        }
      }}
      onChange={(value) => {
        if (value.length > 0) {
          setter(value[0].id);
        }
      }}
    />
  );
};

export default AccesRapideTypeahead;
