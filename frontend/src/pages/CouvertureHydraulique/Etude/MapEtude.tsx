import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import MapComponent from "../../../components/Map/Map.tsx";
import url from "../../../module/fetch.tsx";
import TYPE_CARTE from "../../../enums/TypeCarte.tsx";
import EtudeStatutEnum from "../../../Entities/EtudeEntity.tsx";

const MapEtude = () => {
  const { etudeId } = useParams();

  const etudeState = useGet(url`/api/couverture-hydraulique/etude/` + etudeId);

  return (
    etudeId && (
      <>
        <PageTitle
          title={etudeState?.data?.etudeLibelle}
          icon={<IconEtude />}
        />
        <MapComponent
          etudeId={etudeId}
          typeCarte={TYPE_CARTE.COUVERTURE_HYDRAULIQUE}
          disabledEditPeiProjet={
            EtudeStatutEnum[etudeState?.data?.etudeStatut] ===
            EtudeStatutEnum.TERMINEE
          }
        />
      </>
    )
  );
};

export default MapEtude;
