import { useParams } from "react-router-dom";
import Container from "react-bootstrap/Container";
import url from "../../module/fetch.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { URLS } from "../../routes.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { IconOldeb } from "../../components/Icon/Icon.tsx";
import OldebForm, {
  getInitialValues,
  prepareValues,
  validationSchema,
} from "./OldebForm.tsx";

const OldebUpdate = ({
  oldebIdCarte: oldebIdCarte,
  onClose,
}: {
  oldebIdCarte: string | null;
  onClose: () => void;
}) => {
  const { oldebId } = useParams();
  const oldebIdFinal = oldebIdCarte ?? oldebId;
  const oldebState = useGet(
    oldebIdFinal ? url`/api/oldeb/${oldebIdFinal}` : "",
  );

  return (
    oldebIdFinal && (
      <Container>
        <PageTitle
          icon={<IconOldeb />}
          title={"Mise à jour d'une Obligation Légale de Débroussaillement"}
          displayReturnButton={oldebIdCarte == null}
        />
        {oldebState.data && (
          <MyFormik
            initialValues={getInitialValues(oldebState.data)}
            validationSchema={validationSchema}
            isPost={false}
            isMultipartFormData={true}
            submitUrl={`/api/oldeb/${oldebIdFinal!}`}
            prepareVariables={(values) => prepareValues(values)}
            redirectUrl={!oldebIdCarte ? URLS.OLDEB_LIST : undefined}
            onSubmit={() => onClose()}
          >
            <OldebForm returnButton={oldebIdCarte == null} />
          </MyFormik>
        )}
      </Container>
    )
  );
};

export default OldebUpdate;
