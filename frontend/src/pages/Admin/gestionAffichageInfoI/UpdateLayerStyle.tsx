import Container from "react-bootstrap/Container";
import { useParams } from "react-router-dom";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { IconEdit } from "../../../components/Icon/Icon.tsx";
import { URLS } from "../../../routes.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import url from "../../../module/fetch.tsx";
import CreateLayerStyleForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./CreateLayerStyleForm.tsx";

const UpdateLayerStyle = () => {
  const { styleId } = useParams();
  const styleState = useGet(url`/api/admin/couche/get-style/${styleId!}`)?.data;

  return (
    styleState && (
      <Container>
        <PageTitle title="Modification du style" icon={<IconEdit />} />
        <MyFormik
          initialValues={getInitialValues(styleState)}
          prepareVariables={(values) => prepareValues(values)}
          validationSchema={validationSchema}
          submitUrl={`/api/admin/couche/${styleId}/update`}
          isPost={true}
          redirectUrl={URLS.URL_LIST_LAYER_STYLE}
          onSubmit={() => true}
        >
          <CreateLayerStyleForm initalLayer={styleState.layerId} />
        </MyFormik>
      </Container>
    )
  );
};

export default UpdateLayerStyle;
