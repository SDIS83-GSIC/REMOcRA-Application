import format from "date-fns/format";

/** Retourne une date et heure formatée avec le pattern fourni
 * @param dateToFormat: Date
 * @param pattern: string
 * @returns: string
 */
function formatDateWithPattern(dateToFormat: Date, pattern: string) {
  return format(dateToFormat, pattern);
}

/** Retourne une date et heure au format 'dd/MM/yyyy HH:mm'
 * @param dateToFormat: Date
 * @returns: string
 */
function formatDateTime(dateToFormat: Date) {
  return formatDateWithPattern(dateToFormat, "dd/MM/yyyy HH:mm");
}

/** Retourne une date au format 'dd-MM-yyyy HH:mm:ss'
 * utilisé pour préremplir le composant DateTimeInput
 * @param dateToFormat: Date
 * @returns: string
 */
export function formatDateTimeForDateTimeInput(dateToFormat: Date) {
  return formatDateWithPattern(dateToFormat, "yyyy-MM-dd HH:mm");
}

/** Retourne une date au format 'dd/MM/yyyy'
 * @param dateToFormat: Date
 * @returns: string
 */
export function formatDate(dateToFormat: Date) {
  return formatDateWithPattern(dateToFormat, "dd/MM/yyyy");
}

/** Retourne une date/heure pour affichage textuel au format 'dd/MM/yyyy à HH:mm'
 * @param dateToFormat: Date
 * @returns: string
 */
export function formatDateHeure(dateToFormat: Date) {
  return formatDateWithPattern(dateToFormat, "dd/MM/yyyy à HH:mm");
}

/** Retourne une date pour assigner la valeur au format attendu par le composant DateInput
 * @param dateToFormat: Date
 * @returns: string
 */
export function formatForDateInput(dateToFormat: Date) {
  return dateToFormat.split("T").shift();
}

export default formatDateTime;
