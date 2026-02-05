import { useFormikContext } from "formik";
import { WKT } from "ol/format";
import { useEffect } from "react";
import { Col, Container, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { object } from "yup";
import AddRemoveComponent from "../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconAireAspiration } from "../../components/Icon/Icon.tsx";
import TypeSystemeSrid from "../../enums/TypeSystemeSrid.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";

export const getInitialValues = (
  data: any,
  srid: number,
): { listeAireAspiration: AireAspirationType[] } => ({
  listeAireAspiration: data.map((v: any) => {
    const geom = v.geometrie?.split(";");
    const geometry = geom ? new WKT().readGeometry(geom[1]) : undefined;
    const coords = (geometry as any)?.getCoordinates?.();
    const coordX = coords?.[0];
    const coordY = coords?.[1];
    const dataSrid = geom?.[0]?.split("=")?.pop();
    return {
      penaAspirationId: v.penaAspirationId,
      numero: v.numero,
      estNormalise: v.estNormalise,
      hauteurSuperieure3Metres: v.hauteurSuperieure3Metres,
      typePenaAspirationId: v.typePenaAspirationId,
      estDeporte: v.estDeporte,
      coordonneeX: coordX,
      coordonneeY: coordY,
      srid: dataSrid ? parseInt(dataSrid) : srid,
      typeSystemeSrid: dataSrid
        ? (TypeSystemeSrid.find((e) => e.srid === parseInt(dataSrid))?.srid ??
          TypeSystemeSrid[0].srid)
        : TypeSystemeSrid[0].srid,
      coordonneeXToDisplay: coordX,
      coordonneeYToDisplay: coordY,
    };
  }),
});

export const validationSchema = object({});
export const prepareVariables = (values: {
  listeAireAspiration: AireAspirationType[];
}) => ({
  listeAireAspiration: values.listeAireAspiration.map(
    (v: AireAspirationType) => {
      const coordX = v.coordonneeX ? parseFloat(String(v.coordonneeX)) : null;
      const coordY = v.coordonneeY ? parseFloat(String(v.coordonneeY)) : null;

      const hasValidCoords =
        coordX != null && !isNaN(coordX) && coordY != null && !isNaN(coordY);

      return {
        penaAspirationId: v.penaAspirationId,
        numero: v.numero,
        estNormalise: v.estNormalise,
        hauteurSuperieure3Metres: v.hauteurSuperieure3Metres,
        typePenaAspirationId: v.typePenaAspirationId,
        estDeporte: v.estDeporte,
        geometrie:
          v.estDeporte && v.srid && hasValidCoords
            ? `SRID=${v.srid};POINT(${coordX} ${coordY})`
            : null,
      };
    },
  ),
});

const AireAspiration = ({
  peiIdCarte,
  onSubmit,
}: {
  peiIdCarte?: string;
  onSubmit?: (values: any) => void;
}) => {
  let { penaId } = useParams();
  penaId = peiIdCarte ?? penaId;
  const { srid } = useAppContext();

  const listeAireAspirationState = useGet(
    url`/api/pena/get-aire-aspiration/` + penaId,
  );

  const { data } = listeAireAspirationState;

  return (
    data && (
      <MyFormik
        initialValues={getInitialValues(data, srid)}
        validationSchema={validationSchema}
        isPost={false}
        submitUrl={`/api/pena/upsert-pena-aspiration/` + penaId}
        prepareVariables={(values) => prepareVariables(values)}
        redirectUrl={peiIdCarte ? "" : URLS.PEI}
        onSubmit={onSubmit ?? (() => null)}
      >
        <FormAireAspiration peiIdCarte={peiIdCarte} />
      </MyFormik>
    )
  );
};

const FormAireAspiration = ({ peiIdCarte }: { peiIdCarte?: string }) => {
  const { values } = useFormikContext<{
    listeAireAspiration: AireAspirationType[];
  }>();
  const { srid } = useAppContext();

  return (
    <FormContainer>
      <Container>
        <Row>
          <PageTitle
            icon={<IconAireAspiration />}
            title={"Modification des aires d'aspiration"}
            displayReturnButton={peiIdCarte ? false : true}
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
              typeSystemeSrid: TypeSystemeSrid[0].srid,
              coordonneeXToDisplay: null,
              coordonneeYToDisplay: null,
            }}
            listeElements={values.listeAireAspiration}
          />
        </Row>
        <SubmitFormButtons returnLink={peiIdCarte ? false : true} />
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
  const { setFieldValue } = useFormikContext<{
    listeAireAspiration: AireAspirationType[];
  }>();
  const { srid } = useAppContext();

  const element = listeElements[index];

  const sridList = TypeSystemeSrid.filter(
    (v) => v.actif || v.srid === Number(srid),
  ).map((v) => ({
    id: `${v.srid}`,
    code: `${v.srid}`,
    libelle: v.nomSystem,
  }));

  // Récupération des géométries selon le SRID
  const geometrieState = useGet(
    url`/api/pei/get-geometrie-by-srid?${{
      coordonneeX: element.coordonneeXToDisplay,
      coordonneeY: element.coordonneeYToDisplay,
      srid: element.typeSystemeSrid,
    }}`,
  );

  const geometrieData = geometrieState?.data;

  // Conversion des coordonnées
  useEffect(() => {
    if (
      element.coordonneeXToDisplay != null &&
      element.coordonneeYToDisplay != null &&
      element.typeSystemeSrid != null
    ) {
      geometrieState?.run({
        coordonneeX: element.coordonneeXToDisplay,
        coordonneeY: element.coordonneeYToDisplay,
        srid: element.typeSystemeSrid,
      });
    }
  }, [
    element.coordonneeXToDisplay,
    element.coordonneeYToDisplay,
    element.typeSystemeSrid,
    geometrieState,
  ]);

  // Mettre à jour les coordonnées converties
  useEffect(() => {
    if (
      element.coordonneeXToDisplay !== undefined &&
      element.coordonneeYToDisplay !== undefined
    ) {
      const coordonnees = geometrieData?.find(
        (e: any) => Number(e.srid) === Number(srid),
      );

      if (coordonnees != null) {
        setFieldValue(
          `listeAireAspiration[${index}].coordonneeX`,
          coordonnees?.coordonneeX,
        );
        setFieldValue(
          `listeAireAspiration[${index}].coordonneeY`,
          coordonnees?.coordonneeY,
        );
        setFieldValue(`listeAireAspiration[${index}].srid`, srid);
      }
    }
  }, [
    element.coordonneeXToDisplay,
    element.coordonneeYToDisplay,
    srid,
    setFieldValue,
    geometrieData,
    index,
  ]);

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
              (e: { id: string }) =>
                e.id === listeElements[index].typePenaAspirationId,
            )}
            required={false}
            setFieldValue={setFieldValue}
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
      </Row>
      {listeElements[index].estDeporte && (
        <Row className="mt-3">
          <Col xs={4}>
            <SelectForm
              label="Système"
              name={`listeAireAspiration[${index}].typeSystemeSrid`}
              required={true}
              listIdCodeLibelle={sridList}
              defaultValue={sridList.find(
                (v) => v.id === `${element.typeSystemeSrid}`,
              )}
              onChange={(e: any) => {
                setFieldValue(
                  `listeAireAspiration[${index}].typeSystemeSrid`,
                  parseInt(e.code),
                );
                const sridActif = e.code;
                const projectionValeur = geometrieData?.find(
                  (proj: any) => proj.srid === parseInt(sridActif),
                );

                if (projectionValeur) {
                  setFieldValue(
                    `listeAireAspiration[${index}].coordonneeXToDisplay`,
                    projectionValeur.coordonneeX,
                  );
                  setFieldValue(
                    `listeAireAspiration[${index}].coordonneeYToDisplay`,
                    projectionValeur.coordonneeY,
                  );

                  // Coordonnées en SRID système
                  const coordonneesToSave = geometrieData?.find(
                    (proj: any) => proj.srid === srid,
                  );
                  if (coordonneesToSave) {
                    setFieldValue(
                      `listeAireAspiration[${index}].coordonneeX`,
                      coordonneesToSave.coordonneeX,
                    );
                    setFieldValue(
                      `listeAireAspiration[${index}].coordonneeY`,
                      coordonneesToSave.coordonneeY,
                    );
                    setFieldValue(`listeAireAspiration[${index}].srid`, srid);
                  }
                }
              }}
              setFieldValue={setFieldValue}
            />
          </Col>
          <Col xs={4}>
            <TextInput
              name={`listeAireAspiration[${index}].coordonneeXToDisplay`}
              label="Coordonnée X"
              required={listeElements[index].estDeporte}
              onChange={(e) => {
                setFieldValue(
                  `listeAireAspiration[${index}].coordonneeXToDisplay`,
                  e.target.value,
                );
              }}
            />
          </Col>
          <Col xs={4}>
            <TextInput
              name={`listeAireAspiration[${index}].coordonneeYToDisplay`}
              label="Coordonnée Y"
              required={listeElements[index].estDeporte}
              onChange={(e) => {
                setFieldValue(
                  `listeAireAspiration[${index}].coordonneeYToDisplay`,
                  e.target.value,
                );
              }}
            />
          </Col>
        </Row>
      )}
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
  typeSystemeSrid: number;
  coordonneeXToDisplay: string;
  coordonneeYToDisplay: string;
};

export default AireAspiration;
