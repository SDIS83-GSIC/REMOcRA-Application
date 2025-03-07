import { useFormikContext } from "formik";
import { Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { object } from "yup";
import { WKT } from "ol/format";
import AddRemoveComponent from "../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  NumberInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { IconAireAspiration } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";

export const getInitialValues = (
  data: any,
): { listeAireAspiration: AireAspirationType[] } => ({
  listeAireAspiration: data.map((v: any) => {
    const { sridStr, geom } = v.geometrie.cadastreParcelleGeometrie.split(";");
    const geometry = new WKT().readGeometry(geom);
    return {
      penaAspirationId: v.penaAspirationId,
      numero: v.numero,
      estNormalise: v.estNormalise,
      hauteurSuperieure3Metres: v.hauteurSuperieure3Metres,
      typePenaAspirationId: v.typePenaAspirationId,
      estDeporte: v.estDeporte,
      coordonneeX: geometry.getCoordinates()[0],
      coordonneeY: geometry.getCoordinates()[1],
      srid: sridStr.split("=").pop(),
    };
  }),
});

export const validationSchema = object({});
export const prepareVariables = (values: {
  listeAireAspiration: AireAspirationType[];
}) => ({
  listeAireAspiration: values.listeAireAspiration.map(
    (v: AireAspirationType) => ({
      penaAspirationId: v.penaAspirationId,
      numero: v.numero,
      estNormalise: v.estNormalise,
      hauteurSuperieure3Metres: v.hauteurSuperieure3Metres,
      typePenaAspirationId: v.typePenaAspirationId,
      estDeporte: v.estDeporte,
      geometrie: `SRID=${v.srid};POINT(${v.coordonneeX} ${v.coordonneeY})`,
    }),
  ),
});

const AireAspiration = () => {
  const { penaId } = useParams();

  // TODO get initial values
  const listeAireAspirationState = useGet(
    url`/api/pena/get-aire-aspiration/` + penaId,
  );
  const { data } = listeAireAspirationState;

  return (
    data && (
      <MyFormik
        initialValues={getInitialValues(data)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/pena/upsert-pena-aspiration/` + penaId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={URLS.PEI}
      >
        <FormAireAspiration />
      </MyFormik>
    )
  );
};

const FormAireAspiration = () => {
  const { values } = useFormikContext();
  const { srid } = useAppContext();

  return (
    <FormContainer>
      <Container>
        <Row>
          <PageTitle
            icon={<IconAireAspiration />}
            title={"Modification des aires d'aspiration"}
          />
        </Row>
        <Row>
          <AddRemoveComponent
            name="listeAireAspiration"
            createComponentToRepeat={createComponentToRepeat}
            defaultElement={{
              penaAspirationId: null,
              numero: "",
              estNormalise: false,
              hauteurSuperieure3Metres: false,
              typePenaAspirationId: null,
              estDeporte: false,
              coordonneeX: null,
              coordonneeY: null,
              srid: srid,
            }}
            listeElements={values.listeAireAspiration}
          />
        </Row>
        <SubmitFormButtons returnLink={URLS.PEI} />
      </Container>
    </FormContainer>
  );
};

const ComposantToRepeat = ({
  index,
  listeElements,
}: {
  index: number;
  listeElements: AireAspirationType[];
}) => {
  const typePenaAspirationState = useGet(url`/api/pena/type-pena-aspiration`);
  const { setValues } = useFormikContext();

  return (
    <div>
      <Row className="align-items-center mt-3">
        <Col>
          <TextInput
            name={`listeAireAspiration[${index}].numero`}
            label="Numéro"
            required={true}
          />
        </Col>
        <Col>
          <SelectForm
            name={`listeAireAspiration[${index}].typePenaAspirationId`}
            label="Dispositif d'aspiration"
            listIdCodeLibelle={typePenaAspirationState.data}
            defaultValue={typePenaAspirationState?.data?.find(
              (e) => e.id === listeElements[index].typePenaAspirationId,
            )}
            required={false}
            setValues={setValues}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name={`listeAireAspiration[${index}].estNormalise`}
            label={"Est normalisée"}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name={`listeAireAspiration[${index}].hauteurSuperieure3Metres`}
            label={"Hauteur > 3 mètres"}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <CheckBoxInput
            name={`listeAireAspiration[${index}].estDeporte`}
            label={"Est déportée"}
          />
        </Col>
        {listeElements[index].estDeporte && (
          <>
            <Col>
              <NumberInput
                name={`listeAireAspiration[${index}].coordonneeX`}
                label="Coordonnée X"
                required={false}
              />
            </Col>
            <Col>
              <NumberInput
                name={`listeAireAspiration[${index}].coordonneeY`}
                label="Coordonnée Y"
                required={false}
              />
            </Col>
          </>
        )}
      </Row>
    </div>
  );
};

function createComponentToRepeat(index: number, listeElements: any[]) {
  return <ComposantToRepeat index={index} listeElements={listeElements} />;
}

type AireAspirationType = {
  penaAspirationId: string;
  numero: string;
  estNormalise: boolean;
  hauteurSuperieure3Metres: boolean;
  typePenaAspirationId?: string;
  estDeporte: boolean;
  coordonneeX: string;
  coordonneeY: string;
  srid: number;
};

export default AireAspiration;
