import url, { getFetchOptions } from "../module/fetch.tsx";
import { URLS } from "../routes.tsx";

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
