import { useFormikContext } from "formik";
import { Row } from "react-bootstrap";
import { number, object, string } from "yup";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  FormContainer,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { hasDroit } from "../../droits.tsx";
import {
  DfciDebEntity,
  listTypeDebroussaillement,
  listTypeProgramme,
  listTypeTravaux,
} from "../../Entities/DfciDebEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import url from "../../module/fetch.tsx";
import { numberPositif, requiredString } from "../../module/validators.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValuesDeb = (data: DfciDebEntity) => ({
  dfciDebId: data?.dfciDebId,
  dfciDebLibelle: data?.dfciDebLibelle,
  dfciDebAnneeProgramme: data?.dfciDebAnneeProgramme,
  dfciDebAnneeTravaux: data?.dfciDebAnneeTravaux,
  dfciDebMoisTravaux: data?.dfciDebMoisTravaux,
  dfciDebAnneeEdition: data?.dfciDebAnneeEdition,
  dfciDebLargeur: data?.dfciDebLargeur,
  dfciDebSurface: data?.dfciDebSurface,
  dfciDebType: data?.dfciDebType,
  dfciDebGeometrie: data?.dfciDebGeometrie,
  dfciDebRemarque: data?.dfciDebRemarque,
  dfciDebProgramme: data?.dfciDebProgramme,
  dfciDebTravaux: data?.dfciDebTravaux,
  dfciDebDfciMassifId: data?.dfciDebDfciMassifId,
  dfciDebDfciOuvrageId: data?.dfciDebDfciOuvrageId,
  dfciDebDfciPrestataireId: data?.dfciDebDfciPrestataireId,
  dfciDebCode: data?.dfciDebCode,
  dfciDebVersion: data?.dfciDebVersion,
});

export const prepareVariablesDeb = (values: DfciDebEntity) => ({
  dfciDebId: values.dfciDebId,
  dfciDebLibelle: values.dfciDebLibelle,
  dfciDebAnneeProgramme: values.dfciDebAnneeProgramme ?? null,
  dfciDebAnneeTravaux: values.dfciDebAnneeTravaux ?? null,
  dfciDebMoisTravaux: values.dfciDebMoisTravaux ?? null,
  dfciDebAnneeEdition: values.dfciDebAnneeEdition ?? null,
  dfciDebLargeur: values.dfciDebLargeur,
  dfciDebSurface: values.dfciDebSurface,
  dfciDebType: values.dfciDebType,
  dfciDebGeometrie: values.dfciDebGeometrie,
  dfciDebRemarque: values.dfciDebRemarque ?? null,
  dfciDebProgramme: values.dfciDebProgramme ?? null,
  dfciDebTravaux: values.dfciDebTravaux ?? null,
  dfciDebDfciMassifId: values.dfciDebDfciMassifId,
  dfciDebDfciOuvrageId: values.dfciDebDfciOuvrageId ?? null,
  dfciDebDfciPrestataireId: values.dfciDebDfciPrestataireId ?? null,
  dfciDebCode: values.dfciDebCode,
  dfciDebVersion: values.dfciDebVersion,
});

export const validationSchemaDeb = object({
  dfciDebLibelle: requiredString,
  dfciDebAnneeProgramme: number().nullable(),
  dfciDebAnneeTravaux: number().nullable(),
  dfciDebMoisTravaux: number().nullable(),
  dfciDebAnneeEdition: number().nullable(),
  dfciDebLargeur: numberPositif,
  dfciDebSurface: numberPositif,
  dfciDebType: string(),
  dfciDebProgramme: string().nullable(),
  dfciDebTravaux: string().nullable(),
  dfciDebDfciMassifId: string(),
  dfciDebDfciOuvrageId: string().nullable(),
  dfciDebDfciPrestataireId: string().nullable(),
});

const DfciDeb = ({ readOnly }: { readOnly: boolean }) => {
  const { user } = useAppContext();
  const { values, setFieldValue } = useFormikContext<DfciDebEntity>();

  const listMassif: IdCodeLibelleType[] = useGet(url`/api/dfci/massif`).data;
  const listPrestataire: IdCodeLibelleType[] = useGet(
    url`/api/dfci/prestataire`,
  ).data;
  const listOuvrage: IdCodeLibelleType[] = useGet(url`/api/dfci/ouvrage`).data;

  const hasDroitUpdateDfciDeb = hasDroit(user, TYPE_DROIT.DFCI_DEB_U);
  const isDisabled = readOnly || !hasDroitUpdateDfciDeb;

  return (
    <>
      {listMassif && listPrestataire && listOuvrage && (
        <FormContainer>
          {!hasDroitUpdateDfciDeb && !readOnly && (
            <p className="fade alert alert-danger show">
              Vous n'avez pas le droit de modification
            </p>
          )}
          <Row>
            <TextInput
              name="dfciDebLibelle"
              label="Libellé"
              disabled={isDisabled}
              value={values.dfciDebLibelle}
              required={true}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciDebDfciMassifId"
              listIdCodeLibelle={listMassif}
              label="Massif"
              defaultValue={
                values.dfciDebDfciMassifId
                  ? listMassif.find((e) => e.id === values.dfciDebDfciMassifId)
                  : undefined
              }
              disabled={true}
              required={true}
            />
          </Row>
          <Row>
            <NumberInput
              name="dfciDebLargeur"
              label="Largeur (en mètres)"
              disabled={true}
              value={values.dfciDebLargeur}
              required={true}
              step={0.1}
            />
          </Row>
          <Row>
            <NumberInput
              name="dfciDebSurface"
              label="Surface (au dixième d'hectare)"
              disabled={isDisabled}
              value={values.dfciDebSurface}
              required={true}
              min={0}
              step={0.1}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciDebType"
              listIdCodeLibelle={listTypeDebroussaillement}
              label="Type de débroussaillement"
              defaultValue={
                values.dfciDebType
                  ? listTypeDebroussaillement.find(
                      (e) => e.id === values.dfciDebType,
                    )
                  : undefined
              }
              disabled={isDisabled}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciDebMoisTravaux"
              label="Mois des travaux"
              disabled={isDisabled}
              value={values.dfciDebMoisTravaux}
              required={false}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciDebAnneeTravaux"
              label="Année des travaux"
              disabled={isDisabled}
              value={values.dfciDebAnneeTravaux}
              required={false}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciDebTravaux"
              listIdCodeLibelle={listTypeTravaux}
              label="Type de travaux"
              defaultValue={
                values.dfciDebTravaux
                  ? listTypeTravaux.find((e) => e.id === values.dfciDebTravaux)
                  : undefined
              }
              disabled={isDisabled}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciDebAnneeEdition"
              label="Année de l'édition"
              disabled={true}
              value={values.dfciDebAnneeEdition}
              required={false}
            />
          </Row>
          <Row>
            <PositiveNumberInput
              name="dfciDebAnneeProgramme"
              label="Année de programmation"
              disabled={isDisabled}
              value={values.dfciDebAnneeProgramme}
              required={false}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciDebProgramme"
              listIdCodeLibelle={listTypeProgramme}
              label="Type de programmation"
              defaultValue={
                values.dfciDebProgramme
                  ? listTypeProgramme.find(
                      (e) => e.id === values.dfciDebProgramme,
                    )
                  : undefined
              }
              disabled={isDisabled}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciDebDfciPrestataireId"
              listIdCodeLibelle={listPrestataire}
              label="Prestataire"
              defaultValue={
                values.dfciDebDfciPrestataireId
                  ? listPrestataire.find(
                      (e) => e.id === values.dfciDebDfciPrestataireId,
                    )
                  : undefined
              }
              disabled={isDisabled}
              required={false}
              setFieldValue={setFieldValue}
            />
          </Row>
          <Row>
            <SelectForm
              name="dfciDebDfciOuvrageId"
              listIdCodeLibelle={listOuvrage}
              label="Ouvrage"
              defaultValue={
                values.dfciDebDfciOuvrageId
                  ? listOuvrage.find(
                      (e) => e.id === values.dfciDebDfciOuvrageId,
                    )
                  : undefined
              }
              disabled={true}
              required={false}
            />
          </Row>
          <Row>
            <TextAreaInput
              name="dfciDebRemarque"
              label="Remarque"
              value={values.dfciDebRemarque}
              required={false}
              disabled={isDisabled}
            />
          </Row>
          {!readOnly && hasDroitUpdateDfciDeb && <SubmitFormButtons />}
        </FormContainer>
      )}
    </>
  );
};
export default DfciDeb;
