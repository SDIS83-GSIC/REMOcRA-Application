import Container from "react-bootstrap/Container";
import { useParams } from "react-router-dom";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import CreateLayerMetadataForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CreateLayerMetadataForm.tsx";

const UpdateLayerMetadata = () => {
  const { styleId } = useParams();
  const styleState = useGet(url`/api/admin/couche/get-style/${styleId!}`)?.data;

  return (
    styleState && (
      <Container>
        <PageTitle title="Modification des métadonnées" icon={<IconEdit />} />
        <MyFormik
          initialValues={getInitialValues(styleId, styleState)}
          prepareVariables={(values) => prepareValues(values, styleId)}
          validationSchema={validationSchema}
          submitUrl={`/api/admin/couche/${styleId}/update`}
          isPost={true}
          redirectUrl={URLS.URL_LIST_LAYER_STYLE}
          onSubmit={() => true}
        >
          <CreateLayerMetadataForm initalLayer={styleState.layerId} />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateLayerMetadata;
