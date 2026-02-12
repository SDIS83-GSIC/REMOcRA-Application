import { useEffect, useState } from "react";
import { AsyncTypeahead } from "react-bootstrap-typeahead";
import { useGetRun } from "../../components/Fetch/useFetch.tsx";
import { ItemSearch } from "../../components/Localisation/useLocalisation.tsx";

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
  const [state, setState] = useState({ isLoading: false, options: [] });

  if (dependentObject) {
    queryParams.dependenceObjId = dependentObject.id;
  }

  const queryString = motif
    ? `${queryUrl}?${new URLSearchParams(queryParams).toString()}`
    : null;

  const { data, run, isResolved } = useGetRun(queryString || "", {});

  useEffect(() => {
    if (motif.length > 0) {
      setState((prevState) => ({ ...prevState, isLoading: true }));
      run();
    }
  }, [motif, run]);

  useEffect(() => {
    if (isResolved) {
      setState(() => ({ isLoading: false, options: data || [] }));
    }
  }, [isResolved, data]);

  return (
    <AsyncTypeahead
      minLength={2}
      placeholder={label}
      emptyLabel={"Aucun résultat"}
      promptText={"Saisissez au moins 2 lettres"}
      searchText={"Recherche en cours"}
      isLoading={state.isLoading}
      labelKey={"libelle"}
      options={state.options}
      onSearch={(query) => {
        if (query.length < 2) {
          // des communes de 2 caractères existent
          return;
        }
        setMotif(query);
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
