import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { URLS } from "../../routes.tsx";
import url from "../../module/fetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconProprietaire } from "../../components/Icon/Icon.tsx";
import OldebProprietaireForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./OldebProprietaireForm.tsx";

const OldebProprietaireUpdate = () => {
  const { proprietaireId } = useParams();
  const proprietaireState = useGet(url`/api/proprietaire/${proprietaireId}`);

  return (
    <Container>
      <PageTitle
        icon={<IconProprietaire />}
        title={"Mise à jour d'un propriétaire"}
      />
      <MyFormik
        initialValues={getInitialValues(proprietaireState.data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/proprietaire/${proprietaireId}`}
        prepareVariables={(values) => prepareValues(values)}
        redirectUrl={URLS.OLDEB_PROPRIETAIRE_LIST}
      >
        <OldebProprietaireForm isNew={false} />
      </MyFormik>
    </Container>
  );
};

export default OldebProprietaireUpdate;
