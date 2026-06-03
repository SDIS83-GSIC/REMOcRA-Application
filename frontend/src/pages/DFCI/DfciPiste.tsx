import { useFormikContext } from "formik";
import { Row } from "react-bootstrap";
import { number, object, string } from "yup";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  DateInput,
  FormContainer,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import {
  DfciPisteEntity,
  listTypeCroisement,
  listTypeFoncier,
  listTypeImpasse,
  listTypeImpraticabilite,
  listTypeProgramme,
  listTypeTravaux,
  listTypeVoie,
} from "../../Entities/DfciPisteEntity.tsx";
import url from "../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredDate,
  requiredString,
} from "../../module/validators.tsx";
import { formatForDateInput } from "../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValuesPiste = (data: DfciPisteEntity) => ({
  dfciPisteId: data?.dfciPisteId,
  dfciPisteAdresse: data?.dfciPisteAdresse,
  dfciPisteAnneeProgramme: data?.dfciPisteAnneeProgramme,
  dfciPisteAnneeTravaux: data?.dfciPisteAnneeTravaux,
  dfciPisteCirculation: data?.dfciPisteCirculation ?? false,
  dfciPisteDateGps: formatForDateInput(data?.dfciPisteDateGps ?? new Date()),
  dfciPisteLibelle: data?.dfciPisteLibelle,
  dfciPisteNumero: data?.dfciPisteNumero,
  dfciPisteOuverture: data?.dfciPisteOuverture ?? false,
  dfciPisteEstDfci: data?.dfciPisteEstDfci ?? false,
  dfciPisteRetournement: data?.dfciPisteRetournement ?? false,
  dfciPisteNumTroncon: data?.dfciPisteNumTroncon,
  dfciPisteNumObjectif: data?.dfciPisteNumObjectif,
  dfciPisteLibelleObjectif: data?.dfciPisteLibelleObjectif,
  dfciPisteGeometrie: data?.dfciPisteGeometrie,
  dfciPistePraticabilite: data?.dfciPistePraticabilite ?? false,
  dfciPisteRemarque: data?.dfciPisteRemarque,
  dfciPisteImpraticabilite: data?.dfciPisteImpraticabilite,
  dfciPisteTravaux: data?.dfciPisteTravaux,
  dfciPisteVoie: data?.dfciPisteVoie,
  dfciPisteImpasse: data?.dfciPisteImpasse,
  dfciPisteFoncier: data?.dfciPisteFoncier,
  dfciPisteCroisement: data?.dfciPisteCroisement,
  dfciPisteProgramme: data?.dfciPisteProgramme,
  dfciPisteDfciCategoriePisteId: data?.dfciPisteDfciCategoriePisteId,
  dfciPisteDfciMassifId: data?.dfciPisteDfciMassifId,
  dfciPisteDfciPrestataireId: data?.dfciPisteDfciPrestataireId,
  dfciPisteDfciOuvrageId: data?.dfciPisteDfciOuvrageId,
  dfciPisteCode: data?.dfciPisteCode,
  dfciPisteVersion: data?.dfciPisteVersion,
});

export const prepareVariablesPiste = (values: DfciPisteEntity) => ({
  dfciPisteId: values.dfciPisteId,
  dfciPisteAdresse: values.dfciPisteAdresse ?? null,
  dfciPisteAnneeProgramme: values.dfciPisteAnneeProgramme ?? null,
  dfciPisteAnneeTravaux: values.dfciPisteAnneeTravaux ?? null,
  dfciPisteCirculation: values.dfciPisteCirculation,
  dfciPisteDateGps: values.dfciPisteDateGps
    ? new Date(values.dfciPisteDateGps).toISOString()
    : null,
  dfciPisteLibelle: values.dfciPisteLibelle,
  dfciPisteNumero: values.dfciPisteNumero,
  dfciPisteOuverture: values.dfciPisteOuverture,
  dfciPisteEstDfci: values.dfciPisteEstDfci,
  dfciPisteRetournement: values.dfciPisteRetournement,
  dfciPisteNumTroncon: values.dfciPisteNumTroncon,
  dfciPisteNumObjectif: values.dfciPisteNumObjectif ?? null,
  dfciPisteLibelleObjectif: values.dfciPisteLibelleObjectif ?? null,
  dfciPistePraticabilite: values.dfciPistePraticabilite,
  dfciPisteRemarque: values.dfciPisteRemarque ?? null,
  dfciPisteImpraticabilite: values.dfciPisteImpraticabilite ?? null,
  dfciPisteGeometrie: values.dfciPisteGeometrie,
  dfciPisteTravaux: values.dfciPisteTravaux ?? null,
  dfciPisteVoie: values.dfciPisteVoie,
  dfciPisteImpasse: values.dfciPisteImpasse,
  dfciPisteFoncier: values.dfciPisteFoncier ?? null,
  dfciPisteCroisement: values.dfciPisteCroisement,
  dfciPisteProgramme: values.dfciPisteProgramme ?? null,
  dfciPisteDfciCategoriePisteId: values.dfciPisteDfciCategoriePisteId,
  dfciPisteDfciMassifId: values.dfciPisteDfciMassifId,
  dfciPisteDfciPrestataireId: values.dfciPisteDfciPrestataireId ?? null,
  dfciPisteDfciOuvrageId: values.dfciPisteDfciOuvrageId ?? null,
  dfciPisteCode: values.dfciPisteCode,
  dfciPisteVersion: values.dfciPisteVersion,
});

export const validationSchemaDfciPiste = object({
  dfciPisteAdresse: string().nullable(),
  dfciPisteAnneeProgramme: number().nullable(),
  dfciPisteAnneeTravaux: number().nullable(),
  dfciPisteCirculation: requiredBoolean,
  dfciPisteDateGps: requiredDate,
  dfciPisteLibelle: requiredString,
  dfciPisteNumero: requiredString,
  dfciPisteOuverture: requiredBoolean,
  dfciPisteEstDfci: requiredBoolean,
  dfciPisteRetournement: requiredBoolean,
  dfciPisteNumTroncon: number(),
  dfciPisteNumObjectif: number().nullable(),
  dfciPisteLibelleObjectif: string().nullable(),
  dfciPistePraticabilite: requiredBoolean,
  dfciPisteImpraticabilite: string().nullable(),
  dfciPisteTravaux: string().nullable(),
  dfciPisteVoie: string(),
  dfciPisteImpasse: string(),
  dfciPisteFoncier: string().nullable(),
  dfciPisteCroisement: string(),
  dfciPisteProgramme: string().nullable(),
  dfciPisteDfciCategoriePisteId: string(),
  dfciPisteDfciMassifId: string(),
  dfciPisteDfciPrestataireId: string().nullable(),
  dfciPisteDfciOuvrageId: string().nullable(),
});

const DfciPiste = ({ readOnly }: { readOnly: boolean }) => {
  const { values, setFieldValue } = useFormikContext<DfciPisteEntity>();

  const listCategoPiste: IdCodeLibelleType[] = useGet(
    url`/api/dfci/catego-piste`,
  ).data;
  const listMassif: IdCodeLibelleType[] = useGet(url`/api/dfci/massif`).data;
  const listPrestataire: IdCodeLibelleType[] = useGet(
    url`/api/dfci/prestataire`,
  ).data;
  const listOuvrage: IdCodeLibelleType[] = useGet(url`/api/dfci/ouvrage`).data;

  return (
    <>
      {listCategoPiste && listMassif && listPrestataire && listOuvrage && (
        <FormContainer>
          <Row>
            <TextInput
              name="dfciPisteLibelle"
              label="Piste"
              disabled={readOnly}
              value={values.dfciPisteLibelle}
              required={true}
            />
          </Row>
          <Row>
            <TextInput
              name="dfciPisteNumero"
              label="N° de la piste"
              disabled={true}
              value={values.dfciPisteNumero}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteDfciCategoriePisteId"
              listIdCodeLibelle={listCategoPiste}
              label="Catégorie de piste"
              defaultValue={
                values.dfciPisteDfciCategoriePisteId
                  ? listCategoPiste.find(
                      (e) => e.id === values.dfciPisteDfciCategoriePisteId,
                    )
                  : undefined
              }
              disabled={true}
              required={true}
            />
          </Row>
          <Row>
            <TextInput
              name="dfciPisteAdresse"
              label="Adresse"
              disabled={readOnly}
              value={values.dfciPisteAdresse}
              required={false}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteDfciMassifId"
              listIdCodeLibelle={listMassif}
              label="Massif"
              defaultValue={
                values.dfciPisteDfciMassifId
                  ? listMassif.find(
                      (e) => e.id === values.dfciPisteDfciMassifId,
                    )
                  : undefined
              }
              disabled={true}
              required={true}
            />
          </Row>
          <Row>
            <DateInput
              name="dfciPisteDateGps"
              label="Date GPS"
              readOnly={true}
              value={values.dfciPisteDateGps}
              required={true}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPisteEstDfci"
              label="Est une piste DFCI"
              disabled={true}
              checked={values.dfciPisteEstDfci}
              required={true}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPisteCirculation"
              label="Circulation praticable"
              disabled={true}
              checked={values.dfciPisteCirculation}
              required={true}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPistePraticabilite"
              label="Praticabilité"
              disabled={readOnly}
              checked={values.dfciPistePraticabilite}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteImpraticabilite"
              listIdCodeLibelle={listTypeImpraticabilite}
              label="Type d'impraticabilité"
              defaultValue={
                values.dfciPisteImpraticabilite
                  ? listTypeImpraticabilite.find(
                      (e) => e.id === values.dfciPisteImpraticabilite,
                    )
                  : undefined
              }
              disabled={readOnly}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPisteOuverture"
              label="Ouverte au public"
              disabled={true}
              checked={values.dfciPisteOuverture}
              required={true}
            />
          </Row>
          <Row>
            <CheckBoxInput
              name="dfciPisteRetournement"
              label="Retournement possible"
              disabled={true}
              checked={values.dfciPisteRetournement}
              required={true}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciPisteNumTroncon"
              label="N° du tronçon"
              disabled={true}
              value={values.dfciPisteNumTroncon}
              required={true}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciPisteNumObjectif"
              label="N° de l'objectif"
              disabled={true}
              value={values.dfciPisteNumObjectif}
              required={false}
            />
          </Row>
          <Row>
            <TextInput
              name="dfciPisteLibelleObjectif"
              label="Libellé de l'objectif"
              disabled={true}
              value={values.dfciPisteLibelleObjectif}
              required={false}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciPisteAnneeTravaux"
              label="Année des travaux"
              disabled={readOnly}
              value={values.dfciPisteAnneeTravaux}
              required={false}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteTravaux"
              listIdCodeLibelle={listTypeTravaux}
              label="Type de travaux"
              defaultValue={
                values.dfciPisteTravaux
                  ? listTypeTravaux.find(
                      (e) => e.id === values.dfciPisteTravaux,
                    )
                  : undefined
              }
              disabled={readOnly}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteCroisement"
              listIdCodeLibelle={listTypeCroisement}
              label="Type de croisement"
              defaultValue={
                values.dfciPisteCroisement
                  ? listTypeCroisement.find(
                      (e) => e.id === values.dfciPisteCroisement,
                    )
                  : undefined
              }
              disabled={true}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteImpasse"
              listIdCodeLibelle={listTypeImpasse}
              label="Type d'impasse"
              defaultValue={
                values.dfciPisteImpasse
                  ? listTypeImpasse.find(
                      (e) => e.id === values.dfciPisteImpasse,
                    )
                  : undefined
              }
              disabled={true}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteVoie"
              listIdCodeLibelle={listTypeVoie}
              label="Type de voie"
              defaultValue={
                values.dfciPisteVoie
                  ? listTypeVoie.find((e) => e.id === values.dfciPisteVoie)
                  : undefined
              }
              disabled={true}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciPisteAnneeProgramme"
              label="Année de programmation"
              disabled={readOnly}
              value={values.dfciPisteAnneeProgramme}
              required={false}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteProgramme"
              listIdCodeLibelle={listTypeProgramme}
              label="Type de programmation"
              defaultValue={
                values.dfciPisteProgramme
                  ? listTypeProgramme.find(
                      (e) => e.id === values.dfciPisteProgramme,
                    )
                  : undefined
              }
              disabled={readOnly}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteFoncier"
              listIdCodeLibelle={listTypeFoncier}
              label="Foncier"
              defaultValue={
                values.dfciPisteFoncier
                  ? listTypeFoncier.find(
                      (e) => e.id === values.dfciPisteFoncier,
                    )
                  : undefined
              }
              disabled={readOnly}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteDfciPrestataireId"
              listIdCodeLibelle={listPrestataire}
              label="Prestataire"
              defaultValue={
                values.dfciPisteDfciPrestataireId
                  ? listPrestataire.find(
                      (e) => e.id === values.dfciPisteDfciPrestataireId,
                    )
                  : undefined
              }
              required={false}
              disabled={readOnly}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciPisteDfciOuvrageId"
              listIdCodeLibelle={listOuvrage}
              label="Ouvrage"
              defaultValue={
                values.dfciPisteDfciOuvrageId
                  ? listOuvrage.find(
                      (e) => e.id === values.dfciPisteDfciOuvrageId,
                    )
                  : undefined
              }
              disabled={true}
              required={false}
            />
          </Row>
          <Row>
            <TextAreaInput
              name="dfciPisteRemarque"
              label="Remarque"
              value={values.dfciPisteRemarque}
              disabled={readOnly}
              required={false}
            />
          </Row>
          {!readOnly && <SubmitFormButtons />}
        </FormContainer>
      )}
    </>
  );
};

export default DfciPiste;
