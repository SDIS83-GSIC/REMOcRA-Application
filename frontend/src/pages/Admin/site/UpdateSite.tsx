import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEtude } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import Site, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Site.tsx";

const UpdateSite = () => {
  const { siteId } = useParams();

  const siteState = useGet(url`/api/site/` + siteId);
  return (
    <Container>
      <PageTitle icon={<IconEtude />} title={"Mise à jour d'un site"} />
      <MyFormik
        initialValues={getInitialValues(siteState?.data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/site/update/` + siteId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_SITE}
      >
        <Site />
      </MyFormik>
    </Container>
  );
};

export default UpdateSite;
