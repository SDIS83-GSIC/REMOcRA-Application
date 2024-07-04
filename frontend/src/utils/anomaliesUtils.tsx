import { TYPE_DATA_CACHE } from "../enums/NomenclaturesEnum.tsx";
import ensureData from "./ensureData.tsx";

function getStringListeAnomalie(listeAnomaliesPresent: Array<string>) {
  const listeAnomaliePossible = ensureData(TYPE_DATA_CACHE.ANOMALIE);
  if (listeAnomaliePossible === undefined) {
    return;
  }
  return listeAnomaliePossible
    ?.filter((ano) => listeAnomaliesPresent.includes(ano.id))
    .map((e) => e.libelle)
    .join(", ");
}

export default getStringListeAnomalie;
