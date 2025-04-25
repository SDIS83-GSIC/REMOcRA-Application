import SortIdCodeLibelle from "../../../components/SortIdCodeLibelle/SortIdCodeLibelle.tsx";

const SortAnomalieCategorie = () => {
  return (
    <SortIdCodeLibelle
      title="Gestion de l'ordre des catégories d'anomalies"
      apiAdressForGetOrder={`/api/tri-nomenclature/anomalie-categorie/get-ordre`}
      apiAdressForPutUpdateOrder={`/api/tri-nomenclature/anomalie-categorie/update-ordre`}
    />
  );
};

export default SortAnomalieCategorie;
