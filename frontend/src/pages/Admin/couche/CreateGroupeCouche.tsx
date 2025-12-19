import { Container } from "react-bootstrap";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconMapComponent } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import {
  getInitialValue,
  GroupeCouche,
  groupeCoucheValidationSchema,
  prepareValues,
} from "./GroupeCouche.tsx";

const CreateGroupeCouche = () => {
  return (
    <Container>
      <PageTitle
        title="Création d'un groupe de couche"
        icon={<IconMapComponent />}
      />
      <MyFormik
        initialValues={getInitialValue()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={groupeCoucheValidationSchema}
        submitUrl={`/api/admin/groupe-couche/create`}
        isPost={true}
        redirectUrl={URLS.LIST_GROUPE_COUCHE}
        onSubmit={() => true}
      >
        <GroupeCouche />
      </MyFormik>
    </Container>
  );
};

export default CreateGroupeCouche;
