import { Container } from "react-bootstrap";
import { useParams } from "react-router-dom";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import { IconMapComponent } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import {
  getInitialValue,
  GroupeCouche,
  groupeCoucheValidationSchema,
  prepareValues,
} from "./GroupeCouche.tsx";

const UpdateGroupeCouche = () => {
  const { groupeCoucheId } = useParams<{ groupeCoucheId: string }>();
  const { data } = useGet(`/api/admin/groupe-couche/${groupeCoucheId}`);
  return (
    data && (
      <Container>
        <PageTitle
          title="Mise à jour d'un groupe de couche"
          icon={<IconMapComponent />}
        />
        <MyFormik
          initialValues={getInitialValue(data)}
          prepareVariables={(values) => prepareValues(values)}
          validationSchema={groupeCoucheValidationSchema}
          submitUrl={`/api/admin/groupe-couche/${groupeCoucheId}`}
          isPost={false}
          redirectUrl={URLS.LIST_GROUPE_COUCHE}
          onSubmit={() => true}
        >
          <GroupeCouche />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateGroupeCouche;
