import { useGet } from "../../../components/Fetch/useFetch";
import { Refs, stringToSelectOption } from "./HistoriqueTracabilite";

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
