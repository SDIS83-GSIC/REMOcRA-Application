import Container from "react-bootstrap/Container";
import { useParams } from "react-router-dom";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import CoucheMetadataForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CoucheMetadataForm.tsx";

const UpdateCoucheMetadata = () => {
  const { coucheMetadataId: coucheMetadataId } = useParams();
  const coucheMetadataState = useGet(
    url`/api/admin/couche-metadata/get-couche-metadata/${coucheMetadataId!}`,
  )?.data;

  return (
    coucheMetadataState && (
      <Container>
        <PageTitle title="Modification des métadonnées" icon={<IconEdit />} />
        <MyFormik
          initialValues={getInitialValues(
            coucheMetadataId,
            coucheMetadataState,
          )}
          prepareVariables={(values) => prepareValues(values, coucheMetadataId)}
          validationSchema={validationSchema}
          submitUrl={`/api/admin/couche-metadata/${coucheMetadataId}/update`}
          isPost={true}
          redirectUrl={URLS.LIST_COUCHE_METADATA}
          onSubmit={() => true}
        >
          <CoucheMetadataForm coucheInitiale={coucheMetadataState.coucheId} />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateCoucheMetadata;
