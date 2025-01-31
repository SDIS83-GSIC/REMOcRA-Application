import { Container } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";
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
  titrePage = "Création",
  isFkRequired = false,
}: {
  typeNomenclature: NOMENCLATURE;
  redirectLink: string;
  titrePage: string;
  isFkRequired: boolean;
}) => {
  const { state } = useLocation();
  let initialValues: {
    hasProtectedValue: boolean;
    listeFk: IdCodeLibelleType[] | null;
    libelleFk: string | null;
  } = {
    hasProtectedValue: true,
    listeFk: null,
    libelleFk: null,
  };
  if (state) {
    initialValues = state;
    window.history.replaceState({ from: state.from }, "");
  }

  return (
    <Container>
      <PageTitle title={titrePage} icon={<IconCreate />} />
      <MyFormik
        initialValues={getInitialValue()}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={`/api/nomenclature/` + typeNomenclature + "/create/"}
        isPost={true}
        redirectUrl={redirectLink}
        onSubmit={() => true}
      >
        <Nomenclature
          returnLink={redirectLink}
          hasProtectedValue={initialValues.hasProtectedValue}
          listeFk={initialValues.listeFk}
          libelleFk={initialValues.libelleFk}
          isFkRequired={isFkRequired}
        />
      </MyFormik>
    </Container>
  );
};

export default CreateNomenclature;
