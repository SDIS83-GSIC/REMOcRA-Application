import { useParams } from "react-router-dom";
import EtudeStatutEnum from "../../../Entities/EtudeEntity.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import MapCouvertureHydraulique from "../../../components/Map/MapCouvertureHydraulique/MapCouvertureHydraulique.tsx";
import url from "../../../module/fetch.tsx";

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
        <MapCouvertureHydraulique
          etudeId={etudeId}
          disabledEditPeiProjet={
            EtudeStatutEnum[
              etudeState?.data?.etudeStatut as keyof typeof EtudeStatutEnum
            ] === EtudeStatutEnum.TERMINEE
          }
          reseauImporte={etudeState?.data?.reseauImporte}
        />
      </>
    )
  );
};

export default MapEtude;
