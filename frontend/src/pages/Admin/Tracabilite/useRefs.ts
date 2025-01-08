import { useGet } from "../../../components/Fetch/useFetch";

export type SelectOption = {
  label: string;
  value: string;
};

export type Refs = {
  typeOperations: SelectOption[];
  typeObjets: SelectOption[];
  typeUtilisateurs: SelectOption[];
  isLoading: boolean;
};

export const useRefs = (): Refs => {
  const { data } = useGet("/api/tracabilite/refs");

  const typeOperations = data
    ? data.typeOperations.map(stringToSelectOption)
    : [];

  const typeObjets = data ? data.typeObjets.map(stringToSelectOption) : [];

  const typeUtilisateurs = data
    ? data.typeUtilisateurs.map(stringToSelectOption)
    : [];

  return {
    typeOperations,
    typeObjets,
    typeUtilisateurs,
  };
};

const stringToSelectOption = (s: string) => ({
  label: s,
  value: s,
});
