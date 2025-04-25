import { useParams } from "react-router-dom";
import SortIdCodeLibelle from "../../../components/SortIdCodeLibelle/SortIdCodeLibelle.tsx";

const AnomalieSort = () => {
  const { anomalieCategorieId } = useParams();

  return (
    <SortIdCodeLibelle
      title="Gestion de l'ordre des anomalies"
      apiAdressForGetOrder={`/api/tri-nomenclature/anomalie/get-ordre/${anomalieCategorieId}`}
      apiAdressForPutUpdateOrder={`/api/tri-nomenclature/anomalie/update-ordre`}
    />
  );
};

export default AnomalieSort;
