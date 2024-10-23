import { IdCodeLibelleType } from "./typeUtils.tsx";

function getStringListeAnomalie(
  listeAnomaliesPresent: Array<string>,
  listeAnomaliePossible: Array<IdCodeLibelleType>,
) {
  if (listeAnomaliePossible === undefined) {
    return;
  }
  return listeAnomaliePossible
    ?.filter((ano) => listeAnomaliesPresent.includes(ano.id))
    .map((e) => e.libelle)
    .join(", ");
}

export default getStringListeAnomalie;
