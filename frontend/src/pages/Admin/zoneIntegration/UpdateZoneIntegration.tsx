import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import ZoneIntegration, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./ZoneIntegration.tsx";

const UpdateZoneIntegration = () => {
  const { zoneIntegrationId } = useParams();

  const zoneIntegrationState = useGet(
    url`/api/zone-integration/` + zoneIntegrationId,
  );
  return (
    <Container>
      <PageTitle
        icon={<IconEtude />}
        title={"Mise à jour d'une zone de compétence"}
      />
      <MyFormik
        initialValues={getInitialValues(zoneIntegrationState?.data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/zone-integration/update/` + zoneIntegrationId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_ZONE_INTEGRATION}
      >
        <ZoneIntegration />
      </MyFormik>
    </Container>
  );
};

export default UpdateZoneIntegration;
