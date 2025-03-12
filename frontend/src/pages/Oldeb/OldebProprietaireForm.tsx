import { useFormikContext } from "formik";
import { Col, Row } from "react-bootstrap";
import { boolean, object, string } from "yup";
import {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import TYPE_CIVILITE from "../../enums/CiviliteEnum.tsx";
import { email, requiredString } from "../../module/validators.tsx";

type OldebProprietaireFormType = {
  oldebProprietaireId?: string;
  oldebProprietaireOrganisme: boolean;
  oldebProprietaireRaisonSociale: string;
  oldebProprietaireCivilite: string;
  oldebProprietaireNom: string;
  oldebProprietairePrenom: string;
  oldebProprietaireTelephone: string;
  oldebProprietaireEmail: string;
  oldebProprietaireNumVoie: string;
  oldebProprietaireVoie: string;
  oldebProprietaireLieuDit: string;
  oldebProprietaireCodePostal: string;
  oldebProprietaireVille: string;
  oldebProprietairePays: string;
};
export const getInitialValues = (data: OldebProprietaireFormType) => data || {};

export const prepareValues = (values: OldebProprietaireFormType) => values;
export const validationSchema = object({
  oldebProprietaireOrganisme: boolean(),
  oldebProprietaireRaisonSociale: string(),
  oldebProprietaireCivilite: requiredString,
  oldebProprietaireNom: requiredString,
  oldebProprietairePrenom: requiredString,
  oldebProprietaireTelephone: string(),
  oldebProprietaireEmail: email,
  oldebProprietaireNumVoie: string(),
  oldebProprietaireVoie: string(),
  oldebProprietaireLieuDit: string(),
  oldebProprietaireCodePostal: requiredString,
  oldebProprietaireVille: requiredString,
  oldebProprietairePays: requiredString,
});

const OldebProprietaireForm = () => {
  const { values, setFieldValue } =
    useFormikContext<OldebProprietaireFormType>();

  const civiliteList = Object.entries(TYPE_CIVILITE).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });

  return (
    <FormContainer>
      <Row>
        <Col>
          <CheckBoxInput
            label="Le propriétaire est un organisme"
            name="oldebProprietaireOrganisme"
            required={false}
          />
        </Col>
        <Col>
          <TextInput
            label="Raison sociale"
            name="oldebProprietaireRaisonSociale"
            required={false}
            disabled={!values?.oldebProprietaireOrganisme}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <SelectForm
            name={"oldebProprietaireCivilite"}
            listIdCodeLibelle={civiliteList}
            label="Civilité"
            defaultValue={civiliteList?.find(
              (v) => v.id === values?.oldebProprietaireCivilite,
            )}
            required={true}
            setFieldValue={setFieldValue}
          />
        </Col>
        <Col>
          <TextInput label="Nom" name="oldebProprietaireNom" required={true} />
        </Col>
        <Col>
          <TextInput
            label="Prénom"
            name="oldebProprietairePrenom"
            required={true}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <TextInput
            label="Numéro"
            name="oldebProprietaireNumVoie"
            required={false}
          />
        </Col>
        <Col>
          <TextInput
            label="Voie"
            name="oldebProprietaireVoie"
            required={false}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <TextInput
            label="Lieu-dit"
            name="oldebProprietaireLieuDit"
            required={false}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <TextInput
            label="Code postal"
            name="oldebProprietaireCodePostal"
            required={true}
          />
        </Col>
        <Col>
          <TextInput
            label="Ville"
            name="oldebProprietaireVille"
            required={true}
          />
        </Col>
        <Col>
          <TextInput
            label="Pays"
            name="oldebProprietairePays"
            required={true}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <TextInput
            label="Téléphone"
            name="oldebProprietaireTelephone"
            required={false}
          />
        </Col>
        <Col>
          <TextInput
            label="E-mail"
            name="oldebProprietaireEmail"
            required={false}
          />
        </Col>
      </Row>
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default OldebProprietaireForm;
