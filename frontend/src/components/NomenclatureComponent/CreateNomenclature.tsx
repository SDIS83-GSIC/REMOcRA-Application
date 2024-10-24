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
}: {
  typeNomenclature: NOMENCLATURE;
  redirectLink: string;
}) => {
  const { state } = useLocation();
  let initialValues: {
    hasProtectedValue: boolean;
    listeFk: IdCodeLibelleType[] | null;
    fkLibelle: string | null;
  } = {
    hasProtectedValue: true,
    listeFk: null,
    fkLibelle: null,
  };
  if (state) {
    initialValues = state;
    window.history.replaceState(null, "");
  }

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
        <Nomenclature
          returnLink={redirectLink}
          hasProtectedValue={initialValues.hasProtectedValue}
          listeFk={initialValues.listeFk}
          fkLibelle={initialValues.fkLibelle}
        />
      </MyFormik>
    </Container>
  );
};

export default CreateNomenclature;
