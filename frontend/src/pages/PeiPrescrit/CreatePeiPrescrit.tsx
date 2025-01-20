import { Container } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconPrescrit } from "../../components/Icon/Icon.tsx";
import PeiPrescrit, {
  getInitialValues,
  prepareVariables,
} from "./PeiPrescrit.tsx";

const CreatePeiPrescrit = ({
  coordonneeX,
  coordonneeY,
  srid,
  onSubmit,
}: CreatePeiPrescritType) => {
  return (
    <Container>
      <PageTitle icon={<IconPrescrit />} title="Prescription de PEI" />
      <MyFormik
        initialValues={getInitialValues({
          peiPrescritCoordonneeX: coordonneeX,
          peiPrescritCoordonneeY: coordonneeY,
          peiPrescritSrid: srid,
        })}
        isPost={true}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmit}
        submitUrl={`/api/pei-prescrit/create`}
      >
        <PeiPrescrit />
      </MyFormik>
    </Container>
  );
};

type CreatePeiPrescritType = {
  coordonneeX: number;
  coordonneeY: number;
  srid: string;
  onSubmit: () => void;
};

export default CreatePeiPrescrit;
