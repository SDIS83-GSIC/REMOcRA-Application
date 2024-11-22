/**
 * Décore un entier pour l'afficher sous forme "naturelle"
 * @param value Number
 */
function decorateInteger(value: number) {
  return new Intl.NumberFormat("fr-fr", { useGrouping: true }).format(value);
}
export default decorateInteger;
