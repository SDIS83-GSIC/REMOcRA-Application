import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import url from "../../module/fetch.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { URLS } from "../../routes.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconOldeb } from "../../components/Icon/Icon.tsx";
import OldebForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./OldebForm.tsx";

const OldebUpdate = () => {
  const { oldebId } = useParams();
  const oldebState = useGet(url`/api/oldeb/${oldebId}`);

  return (
    <Container>
      <PageTitle
        icon={<IconOldeb />}
        title={"Mise à jour d'une Obligation Légale de Débroussaillement"}
      />
      {oldebState.data && (
        <MyFormik
          initialValues={getInitialValues(oldebState.data)}
          validationSchema={validationSchema}
          isPost={false}
          isMultipartFormData={true}
          submitUrl={`/api/oldeb/${oldebId}`}
          prepareVariables={(values) => prepareValues(values)}
          redirectUrl={URLS.OLDEB_LIST}
        >
          <OldebForm isNew={false} returnLink={URLS.OLDEB_LIST} />
        </MyFormik>
      )}
    </Container>
  );
};

export default OldebUpdate;
