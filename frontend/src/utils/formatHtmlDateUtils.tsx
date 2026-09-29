import { formatDate, formatDateHeure } from "./formatDateUtils.tsx";

/**
 * Formate les dates dans le HTML retourné par le backend.
 * Remplace le contenu des éléments <time> par les dates formatées
 * selon leur classe (date-to-convert ou date-time-to-convert).
 *
 * @param html - Le HTML contenant les éléments <time> à formater
 * @returns Le HTML avec les dates formatées
 */
export const formatHtmlDates = (html: string): string => {
  const parser = new DOMParser();
  const doc = parser.parseFromString(html, "text/html");

  // Traiter les dates sans heure (formatDate)
  const dateOnlyElements = doc.querySelectorAll(".date-to-convert");
  dateOnlyElements.forEach((el) => {
    const isoDateString = el.getAttribute("datetime") || "";
    if (isoDateString) {
      try {
        const date = new Date(isoDateString);
        el.textContent = formatDate(date);
      } catch (_) {
        el.textContent = isoDateString;
      }
    }
  });

  // Traiter les dates avec heure (formatDateHeure)
  const dateTimeElements = doc.querySelectorAll(".date-time-to-convert");
  dateTimeElements.forEach((el) => {
    const isoDateString = el.getAttribute("datetime") || "";
    if (isoDateString) {
      try {
        const date = new Date(isoDateString);
        el.textContent = formatDateHeure(date);
      } catch (_) {
        el.textContent = isoDateString;
      }
    }
  });

  return doc.body.innerHTML;
};
