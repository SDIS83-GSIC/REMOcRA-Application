import format from "date-fns/format";

/** Retourne une date formatée avec le pattern fourni
 * @param dateToFormat: Date
 * @param pattern: string
 * @returns: string
 */
function formatDateWithPattern(dateToFormat: Date, pattern: string) {
  return format(dateToFormat, pattern);
}

/** Retourne une date au format 'dd/MM/yyyy HH:mm'
 * @param dateToFormat: Date
 * @returns: string
 */
function formatDateTime(dateToFormat: Date) {
  return formatDateWithPattern(dateToFormat, "dd/MM/yyyy HH:mm");
}

export default formatDateTime;
