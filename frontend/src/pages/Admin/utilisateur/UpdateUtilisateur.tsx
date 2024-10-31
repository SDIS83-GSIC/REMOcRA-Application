import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import Utilisateur, {
  getInitialValues,
  prepareVariables,
  validationSchema,
} from "./Utilisateur.tsx";

const UpdateUtilisateur = () => {
  const { utilisateurId } = useParams();
  const { data } = useGet(url`/api/utilisateur/get/` + utilisateurId);

  return (
    <Container>
      <PageTitle icon={<IconEdit />} title={"Mise à jour d'un utilisateur"} />
      <MyFormik
        initialValues={getInitialValues(data)}
        validationSchema={validationSchema}
        isPost={false}
        isMultipartFormData={false}
        submitUrl={`/api/utilisateur/update/` + utilisateurId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.LIST_UTILISATEUR}
      >
        <Utilisateur />
      </MyFormik>
    </Container>
  );
};

export default UpdateUtilisateur;
