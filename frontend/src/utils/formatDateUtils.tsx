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

/** Retourne une date au format 'dd/MM/yyyy'
 * @param dateToFormat: Date
 * @returns: string
 */
export function formatDate(dateToFormat: Date) {
  return formatDateWithPattern(dateToFormat, "dd/MM/yyyy");
}

export default formatDateTime;
