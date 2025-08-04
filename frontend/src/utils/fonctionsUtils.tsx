import { TypeModuleRemocra } from "../components/ModuleRemocra/ModuleRemocra.tsx";
import url, { getFetchOptions } from "../module/fetch.tsx";
import { URLS } from "../routes.tsx";
import { THEMATIQUE_POINT_EAU, THEMATIQUE_RCI } from "./constantsUtils.tsx";

function isEmptyOrNull(value: string) {
  return value == null || value.trim().length === 0;
}

export default isEmptyOrNull;

/**
 * Raccourcit une chaîne de caractères si elle dépasse une longueur max.
 * Si la chaîne dépasse la longueur spécifiée, elle sera coupée et un "..." sera ajouté à la fin.
 *
 * @param {string} str - La chaîne de caractères à raccourcir.
 * @param {number} maxLength - La longueur maximale autorisée pour la chaîne de caractères.
 * @returns {string} - La chaîne de caractères raccourcie (si nécessaire), sinon la chaîne d'origine.
 */
export function shortenString(str: string, maxLength: number): string {
  if (str.length > maxLength) {
    return str.substring(0, maxLength) + "...";
  } else {
    return str;
  }
}

/**
 * Récupère la clé d'une énumération (enum) à partir de sa valeur.
 *
 * @param enumParam - L'énumération à parcourir.
 * @param keyParam - La valeur de l'énumération pour laquelle on souhaite obtenir la clé correspondante.
 * @returns La clé correspondant à la valeur dans l'énumération, ou `undefined` si aucune correspondance n'est trouvée.
 */
export function getEnumKey(enumParam: any, keyParam: string) {
  return Object.keys(enumParam).find((key) => enumParam[key] === keyParam);
}
export function downloadOutputFile(
  urlApi: string,
  myObject: any,
  fileName: string,
  successToastMessage: string,
  successToast: (e: string) => void,
  errorToast: (e) => void,
) {
  // On doit passer par un POST pour pouvoir envoyer la liste des paramètres
  fetch(
    url`${urlApi}`,
    getFetchOptions({
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: myObject,
    }),
  )
    .then((response) => {
      if (!response.ok) {
        return response.text().then((text) => {
          errorToast(text);
          throw new Error(text);
        });
      }
      return response.blob();
    })
    .then((blob) => {
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = fileName; // Nom du fichier à télécharger

      a.click();
      window.URL.revokeObjectURL(url); // Libération de la mémoire
      successToast(successToastMessage);
    });
}

export function navigateGoBack(
  location: any,
  navigate: any,
  urlRetour?: string,
) {
  if (location.state?.from?.slice(-1)[0]) {
    navigate(location.state.from.slice(-1)[0], {
      state: {
        ...location.state,
        from: location.state.from.slice(0, -1),
      },
    });
  } else if (urlRetour) {
    navigate(urlRetour);
  } else {
    navigate(URLS.ACCUEIL);
  }
}

/**
 * Permet d'obtenir la thématique associée à un type de module pour les courriers.
 * Pour l'instant, seules les thématiques "Point d'eau" et "RCCI" sont utilisées pour les courriers
 * @param typeModule
 * @returns
 */
export function getThematiqueFromTypeModule(
  typeModule: string | undefined,
): string {
  switch (typeModule) {
    case TypeModuleRemocra.DECI:
      return THEMATIQUE_POINT_EAU;
    case TypeModuleRemocra.RCI:
      return THEMATIQUE_RCI;
    case TypeModuleRemocra.COUVERTURE_HYDRAULIQUE:
    case TypeModuleRemocra.CARTOGRAPHIE_PERSONNALISEE:
    case TypeModuleRemocra.OPERATIONS_DIVERSES:
    case TypeModuleRemocra.DFCI:
    case TypeModuleRemocra.OLDEBS:
    case TypeModuleRemocra.PERMIS:
    case TypeModuleRemocra.ADRESSES:
    case TypeModuleRemocra.RISQUES:
    case TypeModuleRemocra.CRISE:
    case TypeModuleRemocra.ADMIN:
    case TypeModuleRemocra.COURRIER:
    case TypeModuleRemocra.DOCUMENT:
    case TypeModuleRemocra.RAPPORT_PERSONNALISE:
    case TypeModuleRemocra.PEI_PRESCRIT:
    case TypeModuleRemocra.PERSONNALISE:
    case TypeModuleRemocra.DASHBOARD:
    default:
      return THEMATIQUE_POINT_EAU;
  }
}
