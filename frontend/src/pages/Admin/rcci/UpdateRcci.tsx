import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconRCCI } from "../../../components/Icon/Icon.tsx";
import RcciForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./RcciForm.tsx";

const UpdateRcci = ({
  rcciId,
  onSubmit,
}: {
  rcciId: string;
  onSubmit: () => void;
}) => {
  const { user } = useAppContext();

  const { data } = useGet(`/api/rcci/${rcciId}/`, {});

  return (
    data && (
      <Container>
        <PageTitle
          displayReturnButton={false}
          icon={<IconRCCI />}
          title={"Mise à jour d'une RCCI"}
        />
        <MyFormik
          initialValues={getInitialValues(data, user!.utilisateurId)}
          validationSchema={validationSchema}
          isPost={false}
          isMultipartFormData={true}
          submitUrl={`/api/rcci/${rcciId}/`}
          prepareVariables={(values) => prepareValues(values)}
          onSubmit={onSubmit}
        >
          <RcciForm />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateRcci;
