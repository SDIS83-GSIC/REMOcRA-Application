import { Container } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import url from "../../module/fetch.tsx";
import PageTitle from "../Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import MyFormik from "../Form/MyFormik.tsx";
import { IconEdit } from "../Icon/Icon.tsx";
import {
  getInitialValue,
  Nomenclature,
  prepareValues,
  validationSchema,
} from "./Nomenclature.tsx";

const UpdateNomenclature = ({
  nomenclatureId,
  typeNomenclature,
  redirectLink,
}: {
  nomenclatureId: string;
  typeNomenclature: NOMENCLATURE;
  redirectLink: string;
}) => {
  const nomenclatureState = useGet(
    url`/api/` + typeNomenclature + "/get/" + nomenclatureId,
  );

  const { state } = useLocation();
  let initialValues: {
    hasProtectedValue: boolean;
  } = {
    hasProtectedValue: true,
  };
  if (state) {
    initialValues = state;
    window.history.replaceState(null, "");
  }

  return (
    <Container>
      <PageTitle title="Modification" icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValue(nomenclatureState.data)}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/` + typeNomenclature + "/update/" + nomenclatureId}
        isPost={false}
        redirectUrl={redirectLink}
        onSubmit={() => true}
      >
        <Nomenclature
          returnLink={redirectLink}
          hasProtectedValue={initialValues.hasProtectedValue}
        />
      </MyFormik>
    </Container>
  );
};

export default UpdateNomenclature;
