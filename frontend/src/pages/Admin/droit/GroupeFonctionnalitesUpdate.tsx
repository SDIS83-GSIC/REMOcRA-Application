import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import GroupeFonctionnalitesForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./GroupeFonctionnalitesForm.tsx";

const GroupeFonctionnalitesUpdate = () => {
  const { groupeFonctionnalitesId } = useParams();

  const groupeFonctionnalitesState = useGet(
    url`/api/groupe-fonctionnalites/${groupeFonctionnalitesId}`,
  );

  if (!groupeFonctionnalitesState.isResolved) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle
        title="Modification d'un groupe de fonctionnalités"
        icon={<IconEdit />}
      />
      <MyFormik
        initialValues={getInitialValues(groupeFonctionnalitesState.data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/groupe-fonctionnalites/update/${groupeFonctionnalitesId}`}
        isPost={false}
        redirectUrl={URLS.GROUPE_FONCTIONNALITES_LIST}
        onSubmit={() => true}
      >
        <GroupeFonctionnalitesForm />
      </MyFormik>
    </Container>
  );
};

export default GroupeFonctionnalitesUpdate;
