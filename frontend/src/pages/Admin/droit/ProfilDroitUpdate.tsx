import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import ProfilDroitForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./ProfilDroitForm.tsx";

const ProfilDroitUpdate = () => {
  const { profilDroitId } = useParams();

  const profilDroitState = useGet(url`/api/profil-droit/${profilDroitId}`);

  if (!profilDroitState.isResolved) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle
        title="Modification d'un groupe de fonctionnalités"
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues(profilDroitState.data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/profil-droit/update/${profilDroitId}`}
        isPost={false}
        redirectUrl={URLS.PROFIL_DROIT_LIST}
        onSubmit={() => true}
      >
        <ProfilDroitForm />
      </MyFormik>
    </Container>
  );
};

export default ProfilDroitUpdate;
