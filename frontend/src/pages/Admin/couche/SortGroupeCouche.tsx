import { Alert } from "react-bootstrap";
import SortIdCodeLibelle from "../../../components/SortIdCodeLibelle/SortIdCodeLibelle.tsx";
import { IconInfo } from "../../../components/Icon/Icon.tsx";

const SortGroupeCouche = () => {
  return (
    <SortIdCodeLibelle
      beforeTri={
        <Alert
          variant="info"
          className="mt-2 mb-2 text-muted d-flex align-items-center"
        >
          <span>
            <IconInfo /> Les groupes apparaîtront dans l&apos;ordre que vous
            définissez ici.
          </span>
        </Alert>
      }
      title="Réordonner les groupes"
      apiAdressForGetOrder={`/api/admin/groupe-couche/get-ordre`}
      apiAdressForPutUpdateOrder={`/api/admin/groupe-couche/update-ordre`}
    />
  );
};

export default SortGroupeCouche;
