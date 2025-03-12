import { useFormikContext } from "formik";
import { Col, Row } from "react-bootstrap";
import { useParams } from "react-router-dom";
import { object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import TYPE_CIVILITE from "../../../enums/CiviliteEnum.tsx";
import url from "../../../module/fetch.tsx";
import { email } from "../../../module/validators.tsx";
import { URLS } from "../../../routes.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

type ContactType = {
  contactActif: boolean;
  contactCivilite: string;
  contactFonctionContactId: string;
  contactNom: string;
  contactPrenom: string;
  contactNumeroVoie: string;
  contactSuffixeVoie: string;
  contactLieuDitText: string;
  contactLieuDitId: string;
  contactVoieText: string;
  contactVoieId: string;
  contactCodePostal: string;
  contactCommuneText: string;
  contactCommuneId: string;
  contactPays: string;
  contactTelephone: string;
  contactEmail: string;
  siteId: string;
  voieSaisieLibre: boolean;
  communeSaisieLibre: boolean;
  lieuDitSaisieLibre: boolean;
  listRoleId: string[];
  contactIsCompteService: boolean;
};

export const getInitialValues = (data?: ContactType) => ({
  contactActif: data?.contactActif ?? true,
  contactCivilite: data?.contactCivilite ?? null,
  contactFonctionContactId: data?.contactFonctionContactId ?? null,
  contactNom: data?.contactNom ?? null,
  contactPrenom: data?.contactPrenom ?? null,
  contactNumeroVoie: data?.contactNumeroVoie ?? null,
  contactSuffixeVoie: data?.contactSuffixeVoie ?? null,
  contactLieuDitText: data?.contactLieuDitText ?? null,
  contactLieuDitId: data?.contactLieuDitId ?? null,
  contactVoieText: data?.contactVoieText ?? null,
  contactVoieId: data?.contactVoieId ?? null,
  contactCodePostal: data?.contactCodePostal ?? null,
  contactCommuneText: data?.contactCommuneText ?? null,
  contactCommuneId: data?.contactCommuneId ?? null,
  contactPays: data?.contactPays ?? null,
  contactTelephone: data?.contactTelephone ?? null,
  contactEmail: data?.contactEmail ?? null,
  siteId: data?.siteId ?? null,
  listRoleId: data?.listRoleId ?? [],
  contactIsCompteService: data?.contactIsCompteService ?? false,

  voieSaisieLibre:
    data?.contactVoieText != null && data?.contactVoieText?.trim() !== "",
  communeSaisieLibre:
    data?.contactCommuneText != null && data?.contactCommuneText?.trim() !== "",
  lieuDitSaisieLibre:
    data?.contactLieuDitText != null && data?.contactLieuDitText?.trim() !== "",
});

export const validationSchema = object({
  contactEmail: email,
});

export const prepareVariables = (values: ContactType) => ({
  contactActif: values.contactActif,
  contactCivilite: values.contactCivilite,
  contactFonctionContactId: values.contactFonctionContactId,
  contactNom: values.contactNom,
  contactPrenom: values.contactPrenom,
  contactNumeroVoie: values.contactNumeroVoie,
  contactSuffixeVoie: values.contactSuffixeVoie,
  contactLieuDitText: values.contactLieuDitText,
  contactLieuDitId: values.contactLieuDitId,
  contactVoieText: values.contactVoieText,
  contactVoieId: values.contactVoieId,
  contactCodePostal: values.contactCodePostal,
  contactCommuneText: values.contactCommuneText,
  contactCommuneId: values.contactCommuneId,
  contactPays: values.contactPays,
  contactTelephone: values.contactTelephone,
  contactEmail: values.contactEmail,
  siteId: values.siteId,
  listRoleId: values.listRoleId,
  contactIsCompteService: values.contactIsCompteService,
});

const Contact = () => {
  const { appartenance, appartenanceId } = useParams();

  const roleState = useGet(url`/api/role/`);
  const siteState = useGet(url`/api/site/gestionnaire/` + appartenanceId);
  const fonctionContactState = useGet(url`/api/contact/fonctions`);

  const voieState = useGet(url`/api/voie/get`);
  const communeState = useGet(url`/api/commune/get-libelle-commune`);
  const lieuDitState = useGet(url`/api/lieu-dit/get`);

  const { values, setValues, setFieldValue } = useFormikContext<ContactType>();

  const listCivilite = Object.entries(TYPE_CIVILITE).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });

  const { handleShowClose, activesKeys } = useAccordionState([
    true,
    false,
    false,
    false,
  ]);

  return (
    <FormContainer>
      <AccordionCustom
        activesKeys={activesKeys}
        handleShowClose={handleShowClose}
        list={[
          {
            header: "Informations générales",
            content: (
              <>
                <Row className="mt-3">
                  <Col>
                    <SelectForm
                      name={"contactFonctionContactId"}
                      listIdCodeLibelle={fonctionContactState.data}
                      label="Fonction"
                      defaultValue={fonctionContactState.data?.find(
                        (e) => e.id === values.contactFonctionContactId,
                      )}
                      required={false}
                      setValues={setValues}
                    />
                  </Col>
                  <Col>
                    <SelectForm
                      name={"contactCivilite"}
                      listIdCodeLibelle={listCivilite}
                      label="Civilité"
                      defaultValue={listCivilite?.find(
                        (e) => e.id === values.contactCivilite,
                      )}
                      required={false}
                      setValues={setValues}
                    />
                  </Col>
                  <Col>
                    <CheckBoxInput name="contactActif" label="Actif" />
                  </Col>
                </Row>
                <Row>
                  <Col>
                    <TextInput label="Nom" name="contactNom" required={false} />
                  </Col>
                  <Col>
                    <TextInput
                      label="Prénom"
                      name="contactPrenom"
                      required={false}
                    />
                  </Col>
                  <Col>
                    <CheckBoxInput
                      name="contactIsCompteService"
                      label="Compte de service"
                    />
                  </Col>
                </Row>
                <Row className="mt-3">
                  <Col>
                    <TextInput
                      label="Téléphone"
                      name="contactTelephone"
                      required={false}
                    />
                  </Col>
                  <Col>
                    <TextInput
                      label="E-mail"
                      name="contactEmail"
                      required={false}
                    />
                  </Col>
                </Row>
              </>
            ),
          },
          {
            header: "Adresse",
            content: (
              <>
                <Row className="mt-3">
                  <Col>
                    <SelectForm
                      name={"contactCommuneId"}
                      listIdCodeLibelle={communeState.data}
                      label="Commune"
                      defaultValue={communeState?.data?.find(
                        (e) => e.id === values.contactCommuneId,
                      )}
                      setValues={setValues}
                      disabled={values.communeSaisieLibre}
                    />

                    <CheckBoxInput
                      name="communeSaisieLibre"
                      label="Commune non trouvée"
                    />

                    {values.communeSaisieLibre && (
                      <Row>
                        <Col>
                          <TextInput
                            name="contactCommuneText"
                            label="Commune (saisie libre)"
                            required={false}
                          />
                        </Col>
                        <Col>
                          <TextInput
                            label="Code postal"
                            name="contactCodePostal"
                            required={false}
                          />
                        </Col>
                      </Row>
                    )}
                  </Col>
                  <Col>
                    <SelectForm
                      name={"contactLieuDitId"}
                      listIdCodeLibelle={lieuDitState?.data?.filter(
                        (e) => e.communeId === values.contactCommuneId,
                      )}
                      label="Lieu-dit"
                      defaultValue={lieuDitState?.data?.find(
                        (e) => e.id === values.contactLieuDitId,
                      )}
                      optionDisabled="Veuillez saisir une commune"
                      setValues={setValues}
                      disabled={
                        values.lieuDitSaisieLibre || !values.contactCommuneId
                      }
                    />

                    <CheckBoxInput
                      name="lieuDitSaisieLibre"
                      label="Lieu-dit non trouvé"
                    />

                    {values.lieuDitSaisieLibre && (
                      <TextInput
                        name="contactLieuDitText"
                        label="Lieu-dit (saisie libre)"
                        required={false}
                      />
                    )}
                  </Col>

                  {appartenance === "gestionnaire" && (
                    <Col>
                      <SelectForm
                        name={"siteId"}
                        listIdCodeLibelle={siteState.data}
                        label="Site"
                        defaultValue={siteState?.data?.find(
                          (e) => e.id === values.siteId,
                        )}
                        setValues={setValues}
                      />
                    </Col>
                  )}
                </Row>
                <Row className="mt-3">
                  <Col>
                    <TextInput
                      label="Numéro de voie"
                      name="contactNumeroVoie"
                      required={false}
                    />
                  </Col>
                  <Col>
                    <TextInput
                      label="Suffixe"
                      name="contactSuffixeVoie"
                      required={false}
                    />
                  </Col>
                  <Col>
                    <SelectForm
                      name={"contactVoieId"}
                      listIdCodeLibelle={voieState?.data?.filter(
                        (e) => e.communeId === values.contactCommuneId,
                      )}
                      label="Voie"
                      defaultValue={voieState?.data?.find(
                        (e) => e.id === values.contactVoieId,
                      )}
                      optionDisabled="Veuillez saisir une commune"
                      setValues={setValues}
                      disabled={
                        values.voieSaisieLibre || !values.contactCommuneId
                      }
                    />

                    <CheckBoxInput
                      name="voieSaisieLibre"
                      label="Voie non trouvée"
                    />

                    {values.voieSaisieLibre && (
                      <TextInput
                        name="contactVoieText"
                        label="Voie (saisie libre)"
                        required={false}
                      />
                    )}
                  </Col>
                  <Col>
                    <TextInput
                      label="Pays"
                      name="contactPays"
                      required={false}
                    />
                  </Col>
                </Row>
              </>
            ),
          },
          {
            header: "Rôles",
            content: (
              <Row className="mt-3">
                <Multiselect
                  name={"listRoleId"}
                  label="Liste des rôles du contact"
                  options={roleState?.data}
                  getOptionValue={(t) => t.id}
                  getOptionLabel={(t) => t.libelle}
                  value={
                    values?.listRoleId?.map((e) =>
                      roleState?.data?.find(
                        (r: IdCodeLibelleType) => r.id === e,
                      ),
                    ) ?? undefined
                  }
                  onChange={(role) => {
                    const roleId = role.map((e) => e.id);
                    roleId.length > 0
                      ? setFieldValue("listRoleId", roleId)
                      : setFieldValue("listRoleId", undefined);
                  }}
                  isClearable={true}
                  required={false}
                />
              </Row>
            ),
          },
        ]}
      />

      <SubmitFormButtons
        returnLink={URLS.LIST_CONTACT(appartenanceId, appartenance)}
      />
    </FormContainer>
  );
};

export default Contact;
