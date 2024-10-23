import { Container } from "react-bootstrap";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import PageTitle from "../Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../Form/MyFormik.tsx";
import { IconCreate } from "../Icon/Icon.tsx";
import {
  getInitialValue,
  Nomenclature,
  prepareValues,
  validationSchema,
} from "./Nomenclature.tsx";

const CreateNomenclature = ({
  typeNomenclature,
  redirectLink,
}: {
  typeNomenclature: NOMENCLATURE;
  redirectLink: string;
}) => {
  return (
    <Container>
      <PageTitle title="Création" icon={<IconCreate />} />
      <MyFormik
        initialValues={getInitialValue()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/` + typeNomenclature + "/create/"}
        isPost={true}
        redirectUrl={redirectLink}
        onSubmit={() => true}
      >
        <Nomenclature returnLink={redirectLink} />
      </MyFormik>
    </Container>
  );
};

export default CreateNomenclature;
