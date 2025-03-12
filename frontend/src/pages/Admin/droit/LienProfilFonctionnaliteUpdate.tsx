import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import LienProfilFonctionnaliteForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./LienProfilFonctionnaliteForm.tsx";

const ProfilDroitUpdate = () => {
  const { profilOrganismeId, profilUtilisateurId } = useParams();

  const lienProfilFonctionnaliteState = useGet(
    url`/api/lien-profil-fonctionnalite/${profilOrganismeId}/${profilUtilisateurId}`,
  );

  if (!lienProfilFonctionnaliteState.isResolved) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle
        title="Modification d'un lien profil / groupe de fonctionnalités"
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues(lienProfilFonctionnaliteState.data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/lien-profil-fonctionnalite/update/${profilOrganismeId}/${profilUtilisateurId}`}
        isPost={false}
        redirectUrl={URLS.LIEN_PROFIL_FONCTIONNALITE_LIST}
        onSubmit={() => true}
      >
        <LienProfilFonctionnaliteForm />
      </MyFormik>
    </Container>
  );
};

export default ProfilDroitUpdate;
