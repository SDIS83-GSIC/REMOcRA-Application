import { Alert } from "react-bootstrap";
import { useParams } from "react-router-dom";
import SortIdCodeLibelle from "../../../components/SortIdCodeLibelle/SortIdCodeLibelle.tsx";
import { IconInfo } from "../../../components/Icon/Icon.tsx";

const SortCouche = () => {
  const { groupeCoucheId } = useParams<{ groupeCoucheId: string }>();

  return (
    <SortIdCodeLibelle
      beforeTri={
        <Alert
          variant="info"
          className="mt-2 mb-2 text-muted d-flex align-items-center"
        >
          <span>
            <IconInfo /> Les couches apparaîtront dans l&apos;ordre que vous
            définissez ici.
          </span>
        </Alert>
      }
      title="Réordonner les couches"
      apiAdressForGetOrder={`/api/admin/couche/groupe-couche/${groupeCoucheId}/get-ordre`}
      apiAdressForPutUpdateOrder={`/api/admin/couche/update-ordre`}
    />
  );
};

export default SortCouche;
