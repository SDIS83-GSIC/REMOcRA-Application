import { Container } from "react-bootstrap";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconRCCI } from "../../../components/Icon/Icon.tsx";
import RcciForm, {
  getInitialValues,
  prepareValues,
  RcciFormType,
  validationSchema,
} from "./RcciForm.tsx";

const CreateRcci = ({
  creationRcciGeometrie,
  onSubmit,
}: {
  creationRcciGeometrie: { rcci: { rcciGeometrie: string } } | null;
  onSubmit: () => void;
}) => {
  const { user, epsg } = useAppContext();
  return (
    <Container>
      <PageTitle
        displayReturnButton={false}
        icon={<IconRCCI />}
        title={"Création d'une RCCI"}
      />
      <MyFormik
        initialValues={getInitialValues(
          creationRcciGeometrie
            ? { rcci: creationRcciGeometrie.rcci as RcciFormType }
            : { rcci: undefined },
          user!.utilisateurId,
        )}
        validationSchema={validationSchema}
        isPost={true}
        isMultipartFormData={true}
        submitUrl={`/api/rcci/create/`}
        prepareVariables={(values) =>
          prepareValues(values, epsg.name.split(":").pop()!)
        }
        onSubmit={onSubmit}
      >
        <RcciForm />
      </MyFormik>
    </Container>
  );
};

export default CreateRcci;
