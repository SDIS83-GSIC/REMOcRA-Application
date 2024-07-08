import { useFormikContext } from "formik";
import { Button } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import { object } from "yup";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import AccordionCustom from "../../components/Accordion/Accordion.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FormContainer,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SelectNomenclaturesForm from "../../components/Form/SelectNomenclaturesForm.tsx";
import TYPE_DATA_CACHE from "../../enums/NomenclaturesEnum.tsx";
import TYPE_NATURE_DECI from "../../enums/TypeNatureDeci.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";
import url from "../../module/fetch.tsx";
import { requiredString } from "../../module/validators.tsx";
import ensureDataCache from "../../utils/ensureData.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValues = (data: PeiEntity) => ({
  peiId: data.peiId ?? null,
  peiNumeroComplet: data.peiNumeroComplet ?? null,
  peiNumeroInterne: data.peiNumeroInterne ?? null,
  peiTypePei: data.peiTypePei ?? null,
  peiDisponibiliteTerrestre: data.peiDisponibiliteTerrestre ?? null,

  peiAutoriteDeciId: data.peiAutoriteDeciId ?? null,
  peiServicePublicDeciId: data.peiServicePublicDeciId ?? null,
  peiMaintenanceDeciId: data.peiMaintenanceDeciId ?? null,

  peiCommuneId: data.peiCommuneId ?? null,
  peiVoieId: data.peiVoieId ?? null,
  peiNumeroVoie: data.peiNumeroVoie ?? null,
  peiSuffixeVoie: data.peiSuffixeVoie ?? null,
  peiLieuDitId: data.peiLieuDitId ?? null,
  peiCroisementId: data.peiCroisementId ?? null,
  peiComplementAdresse: data.peiComplementAdresse ?? null,
  peiEnFace: data.peiEnFace ?? null,

  peiDomaineId: data.peiDomaineId ?? null,
  peiNatureId: data.peiNatureId ?? null,
  peiSiteId: data.peiSiteId ?? null,
  peiGestionnaireId: data.peiGestionnaireId ?? null,
  peiNatureDeciId: data.peiNatureDeciId ?? null,
  peiNiveauId: data.peiNiveauId ?? null,
});

export const validationSchema = object({
  peiAutoriteDeciId: requiredString,
  peiServicePublicDeciId: requiredString,
  peiNatureId: requiredString,
  peiNatureDeciId: requiredString,
});

export const prepareVariables = (data: PeiEntity, values: PeiEntity) => ({
  peiId: data.peiId ?? null,
  peiNumeroComplet: values.peiNumeroComplet ?? null,
  peiNumeroInterne: values.peiNumeroInterne ?? null,
  peiTypePei: data.peiTypePei,
  peiDisponibiliteTerrestre: data.peiDisponibiliteTerrestre ?? null,

  peiAutoriteDeciId: values.peiAutoriteDeciId ?? null,
  peiServicePublicDeciId: values.peiServicePublicDeciId ?? null,
  peiMaintenanceDeciId: values.peiMaintenanceDeciId ?? null,

  peiCommuneId: values.peiCommuneId ?? null,
  peiVoieId: values.peiVoieId ?? null,
  peiNumeroVoie: values.peiNumeroVoie ?? null,
  peiSuffixeVoie: values.peiSuffixeVoie ?? null,
  peiLieuDitId: values.peiLieuDitId ?? null,
  peiCroisementId: values.peiCroisementId ?? null,
  peiComplementAdresse: values.peiComplementAdresse ?? null,
  peiEnFace: values.peiEnFace ?? null,

  peiDomaineId: values.peiDomaineId ?? null,
  peiNatureId: values.peiNatureId ?? null,
  peiNatureDeciId: values.peiNatureDeciId ?? null,
  peiSiteId: values.peiSiteId ?? null,
  peiGestionnaireId: values.peiGestionnaireId ?? null,
  peiNiveauId: values.peiNiveauId ?? null,
});

type SelectDataType = {
  listAutoriteDeci: IdCodeLibelleType[];
  listServicePublicDeci: IdCodeLibelleType[];
  listMaintenanceDeci: IdCodeLibelleType[];
  listGestionnaire: IdCodeLibelleType[];
  listCommune: IdCodeLibelleType[];
  listLieuDit: (IdCodeLibelleType & { communeId: string })[];
  listVoie: (IdCodeLibelleType & { communeId: string })[];
  listSite: (IdCodeLibelleType & { gestionnaireId: string })[];
};

const Pei = () => {
  const { values, setValues, setFieldValue }: { values: PeiEntity } =
    useFormikContext();
  const selectDataState = useGet(url`/api/pei/referentiel-for-update-pei`);

  const { data: selectData }: { data: SelectDataType | undefined } =
    selectDataState;

  return (
    selectData && (
      <FormContainer>
        <Container>
          <h1>PEI n°{values.peiNumeroComplet}</h1>
          <AccordionCustom
            list={[
              {
                header: "Informations générales",
                content: (
                  <FormEntetePei
                    values={values}
                    selectData={selectData}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
              {
                header: "Localisation",
                content: (
                  <FormLocalisationPei
                    values={values}
                    selectData={selectData}
                    setValues={setValues}
                  />
                ),
              },
              {
                header: "Caractéristiques techniques",
                content: <>TODO</>,
              },
              {
                header: "Documents",
                content: <>TODO</>,
              },
            ]}
          />

          <Button type="submit" variant="primary">
            Valider
          </Button>
        </Container>
      </FormContainer>
    )
  );
};

export default Pei;

const FormEntetePei = ({
  values,
  selectData,
  setValues,
  setFieldValue,
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
  setFieldValue: (champ: string, newValue: any | undefined) => void;
}) => {
  const listNatureDeci: IdCodeLibelleType[] = ensureDataCache(
    TYPE_DATA_CACHE.NATURE_DECI,
  );

  const codeNatureDeci =
    listNatureDeci &&
    listNatureDeci.find((e) => e.id === values.peiNatureDeciId)?.code;

  return (
    listNatureDeci && (
      <>
        <Row>
          <Col xs={12} sm={5} lg={2}>
            <PositiveNumberInput
              name="peiNumeroInterne"
              label="Numéro interne"
            />
          </Col>
          <Col>
            <SelectNomenclaturesForm
              name={"peiNatureId"}
              nomenclature={
                values.peiTypePei === TYPE_PEI.PIBI
                  ? TYPE_DATA_CACHE.NATURE_PIBI
                  : values.peiTypePei === TYPE_PEI.PENA
                    ? TYPE_DATA_CACHE.NATURE_PENA
                    : TYPE_DATA_CACHE.NATURE
              }
              label="Nature du PEI"
              valueId={values.peiNatureId}
              required={true}
              setValues={setValues}
            />
          </Col>
        </Row>
        <Row className="mt-3">
          <Col>
            <SelectForm
              name={"peiAutoriteDeciId"}
              listIdCodeLibelle={selectData.listAutoriteDeci}
              label="Autorité de police DECI"
              defaultValue={selectData.listAutoriteDeci?.find(
                (e) => e.id === values.peiAutoriteDeciId,
              )}
              required={true}
              setValues={setValues}
            />
          </Col>
          <Col>
            <SelectForm
              name={"peiServicePublicDeciId"}
              listIdCodeLibelle={selectData.listServicePublicDeci}
              label="Service Public DECI"
              defaultValue={selectData.listServicePublicDeci?.find(
                (e) => e.id === values.peiServicePublicDeciId,
              )}
              required={true}
              setValues={setValues}
            />
          </Col>
          <Col>
            <SelectForm
              name={"peiMaintenanceDeciId"}
              listIdCodeLibelle={selectData.listMaintenanceDeci}
              label="Maintenance et CTP"
              defaultValue={selectData.listMaintenanceDeci?.find(
                (e) => e.id === values.peiMaintenanceDeciId,
              )}
              required={false}
              setValues={setValues}
            />
          </Col>
        </Row>
        <Row className="mt-3">
          <Col>
            <SelectForm
              name={"peiNatureDeciId"}
              label="Type de DECI"
              listIdCodeLibelle={listNatureDeci}
              defaultValue={listNatureDeci?.find(
                (e) => e.id === values.peiNatureDeciId,
              )}
              required={true}
              setOtherValues={() => {
                setFieldValue("peiGestionnaireId", null);
                setFieldValue("peiSiteId", null);
              }}
              setValues={setValues}
            />
          </Col>
          {(codeNatureDeci === TYPE_NATURE_DECI.PRIVE ||
            codeNatureDeci === TYPE_NATURE_DECI.PRIVE_CONVENTIONNE) && (
            <>
              <Col>
                <SelectForm
                  name={"peiGestionnaireId"}
                  listIdCodeLibelle={selectData.listGestionnaire}
                  label="Gestionnaire"
                  defaultValue={selectData.listGestionnaire.find(
                    (e) => e.id === values.peiGestionnaireId,
                  )}
                  required={false}
                  setValues={setValues}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"peiSiteId"}
                  listIdCodeLibelle={selectData.listSite.filter(
                    (e) => e.gestionnaireId === values.peiGestionnaireId,
                  )}
                  label="Site"
                  defaultValue={selectData.listSite.find(
                    (e) => e.id === values.peiSiteId,
                  )}
                  required={false}
                  setValues={setValues}
                />
              </Col>
            </>
          )}
        </Row>
      </>
    )
  );
};

const FormLocalisationPei = ({
  values,
  selectData,
  setValues,
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
}) => {
  return (
    <>
      <h2>Adresse</h2>
      <Row className="mt-3">
        <Col>
          <SelectForm
            name={"peiCommuneId"}
            listIdCodeLibelle={selectData.listCommune}
            label="Commune"
            defaultValue={selectData.listCommune.find(
              (e) => e.id === values.peiCommuneId,
            )}
            required={true}
            setValues={setValues}
          />
        </Col>
        <Col>
          {values.peiCommuneId && (
            <SelectForm
              name={"peiLieuDitId"}
              listIdCodeLibelle={selectData.listLieuDit.filter(
                (e) => e.communeId === values.peiCommuneId,
              )}
              label="Lieu dit"
              defaultValue={selectData.listLieuDit.find(
                (e) => e.id === values.peiLieuDitId,
              )}
              required={false}
              setValues={setValues}
            />
          )}
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <TextInput
            name="peiNumeroVoie"
            label="Numéro de voie"
            required={false}
          />
        </Col>
        <Col>
          <TextInput name="peiSuffixeVoie" label="Suffixe" required={false} />
        </Col>
        <Col>
          <SelectForm
            name={"peiVoieId"}
            listIdCodeLibelle={selectData.listVoie.filter(
              (e) => e.communeId === values.peiCommuneId,
            )}
            label="Voie"
            defaultValue={selectData.listVoie.find(
              (e) => e.id === values.peiVoieId,
            )}
            required={true}
            setValues={setValues}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="peiEnFace"
            label="Situé en face ?"
            defaultCheck={values.peiEnFace}
          />
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <SelectNomenclaturesForm
            name={"peiNiveauId"}
            nomenclature={TYPE_DATA_CACHE.NIVEAU}
            label="Niveau"
            valueId={values.peiNiveauId}
            required={false}
            setValues={setValues}
          />
        </Col>
        <Col>
          <SelectForm
            name={"peiCroisementId"}
            listIdCodeLibelle={selectData.listVoie.filter(
              (e) => e.communeId === values.peiCommuneId,
            )}
            label="Croisement avec"
            defaultValue={selectData.listVoie.find(
              (e) => e.id === values.peiCroisementId,
            )}
            required={false}
            setValues={setValues}
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"peiDomaineId"}
            nomenclature={TYPE_DATA_CACHE.DOMAINE}
            label="Domaine"
            valueId={values.peiDomaineId}
            required={true}
            setValues={setValues}
          />
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <TextAreaInput
            name="peiComplementAdresse"
            label="Complément d'adresse"
            required={false}
          />
        </Col>
      </Row>
    </>
  );
};
