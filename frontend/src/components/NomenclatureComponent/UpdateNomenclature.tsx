import { Container } from "react-bootstrap";
import { useLocation } from "react-router-dom";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import url from "../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";
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
  titrePage = "Modification",
  isFkRequired = false,
}: {
  nomenclatureId: string;
  typeNomenclature: NOMENCLATURE;
  redirectLink: string;
  titrePage: string;
  isFkRequired: boolean;
}) => {
  const nomenclatureState = useGet(
    url`/api/nomenclature/${typeNomenclature}/get/${nomenclatureId}`,
  );
  const { data }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/type-organisme/get`,
  );

  const location = useLocation();
  const state = location.state ?? {};
  const {
    hasProtectedValue: hasProtectedValue = true,
    listeFk: listeFk = data,
    libelleFk: libelleFk = "Type organisme parent",
    ...rest
  } = state;
  const initialValues: {
    hasProtectedValue: boolean;
    listeFk: IdCodeLibelleType[] | null;
    libelleFk: string | null;
  } = {
    hasProtectedValue,
    listeFk,
    libelleFk,
  };

  if (state) {
    window.history.replaceState(rest, "");
  }

  return (
    <Container>
      <PageTitle title={titrePage} icon={<IconEdit />} />
      <MyFormik
        initialValues={getInitialValue({
          ...initialValues,
          ...nomenclatureState.data,
        })}
        prepareVariables={(values) => prepareValues(values)}
        validationSchema={validationSchema}
        submitUrl={
          `/api/nomenclature/` + typeNomenclature + "/update/" + nomenclatureId
        }
        isPost={false}
        redirectUrl={redirectLink}
        onSubmit={() => true}
      >
        <Nomenclature
          hasProtectedValue={initialValues.hasProtectedValue}
          listeFk={initialValues.listeFk}
          libelleFk={initialValues.libelleFk}
          isFkRequired={isFkRequired}
        />
      </MyFormik>
    </Container>
  );
};

export default UpdateNomenclature;
