import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { IconCrise } from "../../../components/Icon/Icon.tsx";
import MapCrise from "../../../components/Map/MapCrise/MapDeCrise.tsx";
import url from "../../../module/fetch.tsx";

const ModuleMapCrise = () => {
  const { criseId } = useParams();
  const criseState = useGet(url`/api/crise/` + criseId);

  return (
    criseId && (
      <>
        <PageTitle
          title={criseState?.data?.criseLibelle}
          icon={<IconCrise />}
        />
        <MapCrise criseId={criseId} />
      </>
    )
  );
};

export default ModuleMapCrise;
