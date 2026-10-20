/**
 * Utils permettant de récupérer un id, code et libelle
 * pour les enums
 * @param enumType Type de l'enum
 * @returns liste d'id code libelle pour toutes les valeurs de l'enum
 */
export function idCodeLibelleFromEnum<T>(enumType: T) {
  return Object.entries(enumType).map(([key, value]) => {
    return { id: key, code: key, libelle: value.toString() };
  });
}
