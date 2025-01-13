import url, { getFetchOptions } from "../module/fetch.tsx";

function isEmptyOrNull(value: string) {
  return value == null || value.trim().length === 0;
}

export default isEmptyOrNull;

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
