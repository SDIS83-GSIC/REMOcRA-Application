import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import MapComponent from "../../../components/Map/Map.tsx";
import url from "../../../module/fetch.tsx";

const MapEtude = () => {
  const { etudeId } = useParams();

  const etudeState = useGet(url`/api/couverture-hydraulique/etude/` + etudeId);

  return (
    <>
      <PageTitle title={etudeState?.data?.etudeLibelle} icon={<IconEtude />} />
      <MapComponent etudeId={etudeId} />
    </>
  );
};

export default MapEtude;
