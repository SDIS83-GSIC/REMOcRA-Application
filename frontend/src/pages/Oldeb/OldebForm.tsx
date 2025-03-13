import { FieldArray, useFormikContext } from "formik";
import { useEffect, useState } from "react";
import {
  Accordion,
  Badge,
  Button,
  Card,
  Col,
  Form,
  ListGroup,
  Row,
  Tab,
  Table,
  Tabs,
} from "react-bootstrap";
import { array, boolean, object, string } from "yup";
import DeleteButton from "../../components/Button/DeleteButton.tsx";
import { useGet, useGetRun } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FieldSet,
  FileInput,
  FormContainer,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import {
  IconAdd,
  IconDelete,
  IconExport,
} from "../../components/Icon/Icon.tsx";
import TYPE_CIVILITE from "../../enums/CiviliteEnum.tsx";
import nomenclaturesEnum from "../../enums/NomenclaturesEnum.tsx";
import url from "../../module/fetch.tsx";
import {
  date,
  email,
  intPositif,
  requiredString,
} from "../../module/validators.tsx";
import {
  formatDate,
  formatDateTimeForDateTimeInput,
} from "../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

type FormType = {
  oldeb: OldebFormType;
  propriete: OldebProprieteType;
  locataire: OldebLocataireFormType;
  visiteList: OldebVisiteFormType[];
  documentList: object;
  isRent: boolean;
};

type OldebFormType = {
  oldebId?: string;
  oldebGeometrie: string;
  oldebCommuneId: string;
  oldebCadastreSectionId: string;
  oldebCadastreParcelleId: string;
  oldebOldebTypeAccesId: string;
  oldebOldebTypeZoneUrbanismeId: string;
  oldebNumVoie: string;
  oldebVoieId: string;
  oldebLieuDitId: string;
  oldebVolume: number;
  oldebLargeurAcces: string;
  oldebPortailElectrique: boolean;
  oldebCodePortail: string;
  oldebActif: string;
  caracteristiqueList: string[];
};

type OldebProprieteType = {
  oldebProprieteOldebProprietaireId: string;
  oldebProprieteOldebTypeResidenceId: string;
};

type OldebLocataireFormType = {
  oldebLocataireId?: string;
  oldebLocataireOrganisme: boolean;
  oldebLocataireRaisonSociale: string;
  oldebLocataireCivilite: string;
  oldebLocataireNom: string;
  oldebLocatairePrenom: string;
  oldebLocataireTelephone: string;
  oldebLocataireEmail: string;
};

type OldebVisiteFormType = {
  oldebVisiteId?: string;
  oldebVisiteCode: string;
  oldebVisiteDateVisite?: Date;
  oldebVisiteAgent: string;
  oldebVisiteObservation?: string;
  oldebVisiteOldebId: string;
  oldebVisiteDebroussaillementParcelleId: string;
  oldebVisiteDebroussaillementAccesId: string;
  oldebVisiteOldebTypeAvisId: string;
  oldebVisiteOldebTypeActionId: string;
  anomalieList: [];
  suiteList: OldebVisiteSuiteType[];
  documentList: OldebVisiteDocumentType[];
};

type OldebVisiteSuiteType = {
  oldebVisiteSuiteId?: string;
  oldebVisiteSuiteOldebTypeSuiteId: string;
  oldebVisiteSuiteDate?: Date;
  oldebVisiteSuiteObservation?: string;
};

type OldebVisiteDocumentType = {
  documentId: string;
  documentNom: string;
  documentUrl: string;
};

export const getInitialValues = (data?: {
  oldeb: OldebFormType;
  propriete: OldebProprieteType;
  locataire?: OldebLocataireFormType;
  visiteList?: OldebVisiteFormType[];
  documentList?: object;
}) => ({
  oldeb: data?.oldeb || {
    caracteristiqueList: [],
    oldebVolume: 0,
    oldebGeometrie: null,
    oldebActif: true,
  },
  propriete: data?.propriete || {},
  locataire: data?.locataire,
  visiteList: data?.visiteList,
  documentList: data?.documentList || {},
  isRent: data?.locataire ? true : false,
});

export const prepareValues = (values: {
  oldeb: OldebFormType;
  propriete: OldebProprieteType;
  locataire?: OldebLocataireFormType;
  visiteList?: OldebVisiteFormType[];
  documentList?: object;
  isRent?: boolean;
}) => {
  const formData = new FormData();

  formData.append("oldeb", JSON.stringify(values.oldeb));
  formData.append("propriete", JSON.stringify(values.propriete));
  if (values.isRent && values.locataire) {
    formData.append("locataire", JSON.stringify(values.locataire));
  }
  values.visiteList &&
    formData.append("visiteList", JSON.stringify(values.visiteList));
  if (values.documentList) {
    Object.keys(values.documentList).forEach((code) => {
      if (values.documentList) {
        values.documentList[code].forEach((file) => {
          formData.append(`document_${code}[]`, file);
        });
      }
    });
  }

  return formData;
};

export const validationSchema = object({
  oldeb: object({
    oldebId: string(),
    oldebGeometrie: requiredString,
    oldebCommuneId: requiredString,
    oldebCadastreSectionId: requiredString,
    oldebCadastreParcelleId: requiredString,
    oldebOldebTypeAccesId: string(),
    oldebOldebTypeZoneUrbanismeId: string(),
    oldebNumVoie: string(),
    oldebVoieId: string(),
    oldebLieuDitId: string(),
    oldebVolume: intPositif,
    oldebLargeurAcces: intPositif,
    oldebPortailElectrique: boolean(),
    oldebCodePortail: string(),
  }).required(),
  locataire: object().when("isRent", {
    is: true,
    then: () =>
      object({
        oldebLocataireOrganisme: boolean(),
        oldebLocataireRaisonSociale: string(),
        oldebLocataireCivilite: requiredString,
        oldebLocataireNom: requiredString,
        oldebLocatairePrenom: requiredString,
        oldebLocataireTelephone: string(),
        oldebLocataireEmail: email,
      }).required(),
  }),
  propriete: object({
    oldebProprieteOldebProprietaireId: requiredString,
    oldebProprieteOldebTypeResidenceId: requiredString,
  }),
  visiteList: array().of(
    object({
      oldebVisiteId: string(),
      oldebVisiteCode: requiredString,
      oldebVisiteDateVisite: date,
      oldebVisiteAgent: requiredString,
      oldebVisiteObservation: string(),
      oldebVisiteDebroussaillementParcelleId: requiredString,
      oldebVisiteDebroussaillementAccesId: requiredString,
      oldebVisiteOldebTypeAvisId: requiredString,
      oldebVisiteOldebTypeActionId: requiredString,
      anomalieList: array().of(string()),
      suiteList: array().of(
        object({
          oldebVisiteSuiteId: string(),
          oldebVisiteSuiteOldebTypeSuiteId: requiredString,
          oldebVisiteSuiteDate: date,
          oldebVisiteSuiteObservation: string(),
        }),
      ),
      documentList: array(),
    }),
  ),
  isRent: boolean(),
});

const OldebForm = () => {
  const [currentTab, setCurrentTab] = useState("parcelle");
  const [currentVisite, setCurrentVisite] = useState<number>(null);
  const { values, setFieldValue } = useFormikContext<FormType>();
  // const [isRent, setIsRent] = useState<boolean>(values.isRent ?? false);

  const civiliteList = Object.entries(TYPE_CIVILITE).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });
  const communeState = useGet(url`/api/commune/get-libelle-commune`);
  const { run: fetchVoie, data: voieList } = useGetRun(
    url`/api/commune/${values.oldeb.oldebCommuneId}/voie`,
    {},
  );
  const { run: fetchLieuDit, data: lieuDitList } = useGetRun(
    url`/api/commune/${values.oldeb.oldebCommuneId}/lieu-dit`,
    {},
  );
  const { run: fetchCadastreSection, data: cadastreSectionList } = useGetRun(
    url`/api/cadastre/commune/${values.oldeb.oldebCommuneId}/section`,
    {},
  );
  const { run: fetchCadastreParcelle, data: cadastreParcelleList } = useGetRun(
    url`/api/cadastre/section/${values.oldeb.oldebCadastreSectionId}/parcelle`,
    {},
  );
  const proprietaireState = useGet(url`/api/proprietaire/options`);
  const typeCategorieZoneUrbanismeState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_ZONE_URBANISME}`,
  );
  const typeAccesState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_ACCES}`,
  );
  const typeResidenceState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_RESIDENCE}`,
  );
  const typeCategorieCaracteristiqueState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_CATEGORIE_CARACTERISTIQUE}`,
  );
  const typeCaracteristiqueState = useGet(url`/api/oldeb/caracteristique`);
  const typeOldebTypeDebrousaillementState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_DEBROUSSAILLEMENT}`,
  );
  const typeOldebTypeAvisState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_AVIS}`,
  );
  const typeOldebTypeActionState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_ACTION}`,
  );

  useEffect(() => {
    if (values.oldeb.oldebCommuneId) {
      fetchVoie();
      fetchLieuDit();
      fetchCadastreSection();
    }
  }, [
    fetchVoie,
    fetchLieuDit,
    fetchCadastreSection,
    values.oldeb.oldebCommuneId,
  ]);

  useEffect(() => {
    if (values.oldeb.oldebCadastreSectionId) {
      fetchCadastreParcelle();
    }
  }, [fetchCadastreParcelle, values.oldeb.oldebCadastreSectionId]);

  return (
    <FormContainer>
      <FieldSet title={"Informations OLD"}>
        <Row>
          <Col>
            <SelectForm
              name={"oldeb.oldebCommuneId"}
              listIdCodeLibelle={communeState.data}
              label="Commune"
              defaultValue={communeState?.data?.find(
                (v) => v.id === values.oldeb.oldebCommuneId,
              )}
              required={true}
              onChange={(e) => {
                setFieldValue("oldeb.oldebVoieId", undefined);
                setFieldValue("oldeb.oldebLieuDitId", undefined);
                setFieldValue("oldeb.oldebCadastreSectionId", undefined);
                setFieldValue("oldeb.oldebCommuneId", e.target.value);
              }}
            />
          </Col>
          <Col>
            <SelectForm
              name={"oldeb.oldebCadastreSectionId"}
              listIdCodeLibelle={
                cadastreSectionList &&
                cadastreSectionList.map((v) => {
                  return {
                    id: v.cadastreSectionId,
                    libelle: v.cadastreSectionNumero,
                  };
                })
              }
              label="Section"
              defaultValue={cadastreSectionList?.find(
                (v) => v.id === values.oldeb.oldebCadastreSectionId,
              )}
              required={true}
              onChange={(e) => {
                setFieldValue("oldeb.oldebCadastreParcelleId", undefined);
                setFieldValue("oldeb.oldebCadastreSectionId", e.target.value);
              }}
            />
          </Col>
          <Col>
            <SelectForm
              name={"oldeb.oldebCadastreParcelleId"}
              listIdCodeLibelle={
                values.oldeb.oldebCadastreSectionId
                  ? cadastreParcelleList.map((v) => {
                      return {
                        id: v.cadastreParcelleId,
                        libelle: v.cadastreParcelleNumero,
                      };
                    })
                  : []
              }
              label="Parcelle"
              defaultValue={
                values.oldeb.oldebCadastreSectionId &&
                cadastreSectionList?.find(
                  (v) => v.id === values.oldeb.oldebCadastreParcelleId,
                )
              }
              required={true}
              setFieldValue={setFieldValue}
            />
          </Col>
          <Col>
            <SelectForm
              name={"oldeb.oldebOldebTypeZoneUrbanismeId"}
              listIdCodeLibelle={typeCategorieZoneUrbanismeState?.data}
              label="Zone"
              defaultValue={typeCategorieZoneUrbanismeState?.data?.find(
                (v) => v.id === values.oldeb.oldebOldebTypeZoneUrbanismeId,
              )}
              required={true}
              setFieldValue={setFieldValue}
            />
          </Col>
        </Row>
      </FieldSet>
      <Tabs
        id="tabs"
        activeKey={currentTab}
        onSelect={(key) => setCurrentTab(key)}
      >
        <Tab eventKey="parcelle" title="Parcelle">
          <FieldSet title={"Localisation et accès"}>
            <Row>
              <Col>
                <TextInput
                  label="Numéro"
                  name="oldeb.oldebNumVoie"
                  required={false}
                />
              </Col>
              <Col>
                <SelectForm
                  label="Voie"
                  name="oldeb.oldebVoieId"
                  required={false}
                  listIdCodeLibelle={voieList}
                  defaultValue={voieList?.find(
                    (e) => e.id === values.oldeb.oldebVoieId,
                  )}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <SelectForm
                  label="Lieu-dit"
                  name="oldeb.oldebLieuDitId"
                  required={false}
                  listIdCodeLibelle={lieuDitList}
                  defaultValue={lieuDitList?.find(
                    (e) => e.id === values.oldeb.oldebLieuDitId,
                  )}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectForm
                  name={"oldeb.oldebOldebTypeAccesId"}
                  listIdCodeLibelle={typeAccesState?.data}
                  label="Type d'accès"
                  defaultValue={typeAccesState?.data?.find(
                    (v) => v.id === values.oldeb.oldebOldebTypeAccesId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <TextInput
                  label="Largeur"
                  name="oldeb.oldebLargeurAcces"
                  required={false}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <CheckBoxInput
                  label="Portail électrique"
                  name="oldeb.oldebPortailElectrique"
                  required={false}
                />
              </Col>
              <Col>
                <TextInput
                  label="Code portail"
                  name="oldeb.oldebCodePortail"
                  required={false}
                  disabled={!values.oldeb.oldebPortailElectrique}
                />
              </Col>
            </Row>
          </FieldSet>
          <FieldSet title={"Propriétaire"}>
            <Row>
              <Col>
                <SelectForm
                  name={"propriete.oldebProprieteOldebProprietaireId"}
                  listIdCodeLibelle={proprietaireState.data}
                  label="Propriétaire"
                  defaultValue={proprietaireState?.data?.find(
                    (v) =>
                      v.id ===
                      values.propriete.oldebProprieteOldebProprietaireId,
                  )}
                  required={true}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"propriete.oldebProprieteOldebTypeResidenceId"}
                  listIdCodeLibelle={typeResidenceState.data}
                  label="Type de propriété"
                  defaultValue={typeResidenceState?.data?.find(
                    (v) =>
                      v.id ===
                      values.propriete.oldebProprieteOldebTypeResidenceId,
                  )}
                  required={true}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <CheckBoxInput
                  name={"isRent"}
                  label={"La propriété est en location"}
                  checked={values.isRent}
                  onChange={() => {
                    const newValue = !values.isRent;
                    setFieldValue("isRent", newValue);
                    if (!newValue) {
                      setFieldValue("locataire", undefined);
                    } else {
                      setFieldValue("locataire", {});
                    }
                  }}
                />
              </Col>
            </Row>
          </FieldSet>
          {values.isRent && (
            <FieldSet title={"Locataire"}>
              <Row>
                <Col>
                  <CheckBoxInput
                    label="Le locataire est un organisme"
                    name="locataire.oldebLocataireOrganisme"
                    required={false}
                  />
                </Col>
                <Col>
                  <TextInput
                    label="Raison sociale"
                    name="locataire.oldebLocataireRaisonSociale"
                    disabled={!values.locataire?.oldebLocataireOrganisme}
                    required={false}
                  />
                </Col>
              </Row>
              <Row>
                <Col>
                  <SelectForm
                    name={"locataire.oldebLocataireCivilite"}
                    listIdCodeLibelle={civiliteList}
                    label="Civilité"
                    defaultValue={civiliteList?.find(
                      (v) => v.id === values.locataire?.oldebLocataireCivilite,
                    )}
                    required={true}
                    setFieldValue={setFieldValue}
                  />
                </Col>
                <Col>
                  <TextInput
                    label="Nom"
                    name="locataire.oldebLocataireNom"
                    required={true}
                  />
                </Col>
                <Col>
                  <TextInput
                    label="Prénom"
                    name="locataire.oldebLocatairePrenom"
                    required={true}
                  />
                </Col>
              </Row>
              <Row>
                <Col>
                  <TextInput
                    label="Téléphone"
                    name="locataire.oldebLocataireTelephone"
                    required={false}
                  />
                </Col>
                <Col>
                  <TextInput
                    label="E-mail"
                    name="locataire.oldebLocataireEmail"
                    required={false}
                  />
                </Col>
              </Row>
            </FieldSet>
          )}
        </Tab>
        <Tab eventKey="environnement" title="Environnement">
          <FieldSet title={"Environnement"}>
            <Row>
              <Col>
                <NumberInput
                  name={"oldeb.oldebVolume"}
                  required={true}
                  label={"Volume de la ressource en eau mobilisable (m³)"}
                  min={0}
                  step={1}
                />
              </Col>
            </Row>
            <Row>
              <Col xs={6}>
                <Form.Label>Élements à vérifier</Form.Label>
                {typeCategorieCaracteristiqueState.data?.map(
                  (categorie, categorieIdx) => (
                    <Card key={categorieIdx}>
                      <Card.Header>{categorie.libelle}</Card.Header>
                      <ListGroup>
                        {typeCaracteristiqueState.data
                          ?.filter(
                            (caracteristique) =>
                              caracteristique.lienId === categorie.id &&
                              values.oldeb.caracteristiqueList.indexOf(
                                caracteristique.id,
                              ) === -1,
                          )
                          .map((caracteristique) => (
                            <ListGroup.Item
                              key={caracteristique.id}
                              onClick={() => {
                                if (
                                  values.oldeb.caracteristiqueList?.indexOf(
                                    caracteristique.id,
                                  ) === -1
                                ) {
                                  setFieldValue("oldeb.caracteristiqueList", [
                                    ...values.oldeb.caracteristiqueList,
                                    caracteristique.id,
                                  ]);
                                }
                              }}
                            >
                              (+) {caracteristique.libelle}
                            </ListGroup.Item>
                          ))}
                      </ListGroup>
                    </Card>
                  ),
                )}
              </Col>
              <Col xs={6}>
                <Form.Label>Élements présents</Form.Label>
                {typeCategorieCaracteristiqueState.data?.map(
                  (categorie, categorieIdx) => (
                    <Card key={categorieIdx}>
                      <Card.Header>{categorie.libelle}</Card.Header>
                      <ListGroup className="list-group-flush">
                        {typeCaracteristiqueState.data
                          ?.filter(
                            (caracteristique) =>
                              caracteristique.lienId === categorie.id &&
                              values.oldeb.caracteristiqueList.indexOf(
                                caracteristique.id,
                              ) !== -1,
                          )
                          .map((caracteristique) => (
                            <ListGroup.Item
                              key={caracteristique.id}
                              onClick={() => {
                                setFieldValue(
                                  "oldeb.caracteristiqueList",
                                  values.oldeb.caracteristiqueList.filter(
                                    (c) => c !== caracteristique.id,
                                  ),
                                );
                              }}
                            >
                              (-) {caracteristique.libelle}
                            </ListGroup.Item>
                          ))}
                      </ListGroup>
                    </Card>
                  ),
                )}
              </Col>
            </Row>
          </FieldSet>
        </Tab>
        <Tab eventKey="visite" title="Visites">
          <FieldArray
            name={"visiteList"}
            render={(arrayHelpers) => (
              <FieldSet title={"Visites"}>
                <Button
                  variant={"primary"}
                  onClick={() => {
                    const now = new Date();
                    arrayHelpers.push({
                      oldebVisiteCode: (+now).toString(36), // code basé sur le timestamp pour upload
                      oldebVisiteDateVisite: now,
                      oldebVisiteAgent: undefined,
                      oldebVisiteObservation: undefined,
                      oldebVisiteDebroussaillementParcelleId: undefined,
                      oldebVisiteDebroussaillementAccesId: undefined,
                      oldebVisiteOldebTypeAvisId: undefined,
                      oldebVisiteOldebTypeActionId: undefined,
                      anomalieList: [],
                      documentList: [],
                    });
                  }}
                >
                  <IconAdd /> Ajouter une visite
                </Button>
                {currentVisite != null && (
                  <DeleteButton
                    title={"Supprimer la visite"}
                    onClick={() => {
                      const toRemove = currentVisite;
                      // Suppression des documents associés
                      const {
                        // eslint-disable-next-line @typescript-eslint/no-unused-vars
                        [values.visiteList[currentVisite].oldebVisiteCode]: _,
                        ...rest
                      } = values.documentList;
                      setFieldValue(`documentList`, rest);
                      setCurrentVisite(null);
                      arrayHelpers.remove(toRemove);
                    }}
                  />
                )}
                <Table bordered striped hover>
                  <tr>
                    <th>Date</th>
                    <th>Agent</th>
                    <th>Parcelle</th>
                    <th>Voie</th>
                    <th>Anomalies</th>
                    <th>Avis</th>
                    <th>Action</th>
                  </tr>
                  {values.visiteList?.map((visite, idx) => (
                    <tr key={idx} onClick={() => setCurrentVisite(idx)}>
                      <td>
                        {visite.oldebVisiteDateVisite &&
                          formatDate(visite.oldebVisiteDateVisite)}
                      </td>
                      <td>{visite.oldebVisiteAgent}</td>
                      <td>
                        {
                          typeOldebTypeDebrousaillementState?.data?.find(
                            (v) =>
                              v.id ===
                              visite.oldebVisiteDebroussaillementParcelleId,
                          )?.libelle
                        }
                      </td>
                      <td>
                        {
                          typeOldebTypeDebrousaillementState?.data?.find(
                            (v) =>
                              v.id ===
                              visite.oldebVisiteDebroussaillementAccesId,
                          )?.libelle
                        }
                      </td>
                      <td>{visite.anomalieList.length}</td>
                      <td>
                        {
                          typeOldebTypeAvisState?.data?.find(
                            (v) => v.id === visite.oldebVisiteOldebTypeAvisId,
                          )?.libelle
                        }
                      </td>
                      <td>
                        {
                          typeOldebTypeActionState?.data?.find(
                            (v) => v.id === visite.oldebVisiteOldebTypeActionId,
                          )?.libelle
                        }
                      </td>
                    </tr>
                  ))}
                </Table>
              </FieldSet>
            )}
          />
          {currentVisite != null && (
            <FieldSet title={"Détail de la visite"}>
              <VisiteForm
                visiteList={values.visiteList}
                documentList={values.documentList}
                currentVisite={currentVisite}
                setFieldValue={setFieldValue}
                typeOldebTypeDebrousaillement={
                  typeOldebTypeDebrousaillementState?.data
                }
                typeOldebTypeAvis={typeOldebTypeAvisState?.data}
                typeOldebTypeAction={typeOldebTypeActionState?.data}
              />
            </FieldSet>
          )}
        </Tab>
      </Tabs>
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

const VisiteForm = ({
  visiteList,
  documentList,
  currentVisite,
  setFieldValue,
  typeOldebTypeDebrousaillement,
  typeOldebTypeAvis,
  typeOldebTypeAction,
}: {
  visiteList: OldebVisiteFormType[];
  documentList: object;
  currentVisite: number;
  setFieldValue: (...object) => object;
  typeOldebTypeDebrousaillement: IdCodeLibelleType[];
  typeOldebTypeAvis: IdCodeLibelleType[];
  typeOldebTypeAction: IdCodeLibelleType[];
}) => {
  const [currentTab, setCurrentTab] = useState("anomalieList");
  const [currentVisiteSuite, setCurrentVisiteSuite] = useState<number>(null);

  const typeOldebTypeSuiteState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_SUITE}`,
  );
  const typeCategorieAnomalieState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.OLDEB_TYPE_CATEGORIE_ANOMALIE}`,
  );
  const typeAnomalieState = useGet(url`/api/oldeb/anomalie`);

  useEffect(() => {
    // Changement de visite => reset de la partie "Suites"
    setCurrentVisiteSuite(null);
  }, [currentVisite]);

  return (
    <>
      <Row>
        <Col>
          <DateTimeInput
            label="Date"
            name={`visiteList.${currentVisite}.oldebVisiteDateVisite`}
            required={true}
            value={
              visiteList[currentVisite].oldebVisiteDateVisite &&
              formatDateTimeForDateTimeInput(
                visiteList[currentVisite].oldebVisiteDateVisite,
              )
            }
          />
        </Col>
        <Col>
          <TextInput
            label="Agent"
            name={`visiteList.${currentVisite}.oldebVisiteAgent`}
            required={true}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <SelectForm
            name={`visiteList.${currentVisite}.oldebVisiteDebroussaillementParcelleId`}
            label="Débroussaillement parcelle"
            listIdCodeLibelle={typeOldebTypeDebrousaillement}
            defaultValue={typeOldebTypeDebrousaillement?.find(
              (v) =>
                v.id ===
                visiteList[currentVisite]
                  .oldebVisiteDebroussaillementParcelleId,
            )}
            required={true}
            setFieldValue={setFieldValue}
          />
        </Col>
        <Col>
          <SelectForm
            name={`visiteList.${currentVisite}.oldebVisiteDebroussaillementAccesId`}
            label="Débroussaillement accès"
            listIdCodeLibelle={typeOldebTypeDebrousaillement}
            defaultValue={typeOldebTypeDebrousaillement?.find(
              (v) =>
                v.id ===
                visiteList[currentVisite].oldebVisiteDebroussaillementAccesId,
            )}
            required={true}
            setFieldValue={setFieldValue}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <SelectForm
            name={`visiteList.${currentVisite}.oldebVisiteOldebTypeAvisId`}
            label="Avis"
            listIdCodeLibelle={typeOldebTypeAvis}
            defaultValue={typeOldebTypeAvis?.find(
              (v) =>
                v.id === visiteList[currentVisite].oldebVisiteOldebTypeAvisId,
            )}
            required={true}
            setFieldValue={setFieldValue}
          />
        </Col>
        <Col>
          <SelectForm
            name={`visiteList.${currentVisite}.oldebVisiteOldebTypeActionId`}
            label="Action"
            listIdCodeLibelle={typeOldebTypeAction}
            defaultValue={typeOldebTypeAction?.find(
              (v) =>
                v.id === visiteList[currentVisite].oldebVisiteOldebTypeActionId,
            )}
            required={true}
            setFieldValue={setFieldValue}
          />
        </Col>
      </Row>
      <Tabs
        id="tabs"
        activeKey={currentTab}
        onSelect={(key) => setCurrentTab(key)}
      >
        <Tab eventKey={"anomalieList"} title={"Anomalies"}>
          <Accordion>
            {typeCategorieAnomalieState.data?.map((categorie, categorieIdx) => (
              <Accordion.Item key={categorieIdx} eventKey={categorieIdx}>
                <Accordion.Header>{categorie.libelle}</Accordion.Header>
                <Accordion.Body>
                  {typeAnomalieState?.data
                    ?.filter((anomalie) => anomalie.lienId === categorie.id)
                    .map((anomalie, anomalieIdx) => (
                      <div key={anomalieIdx}>
                        <CheckBoxInput
                          label={anomalie.libelle}
                          name={`visiteList.${currentVisite}.anomalieList[]`}
                          checked={
                            visiteList[currentVisite].anomalieList.indexOf(
                              anomalie.id,
                            ) !== -1
                          }
                          onChange={() => {
                            if (
                              visiteList[currentVisite].anomalieList.indexOf(
                                anomalie.id,
                              ) === -1
                            ) {
                              setFieldValue(
                                `visiteList.${currentVisite}.anomalieList`,
                                [
                                  ...visiteList[currentVisite].anomalieList,
                                  anomalie.id,
                                ],
                              );
                            } else {
                              setFieldValue(
                                `visiteList.${currentVisite}.anomalieList`,
                                visiteList[currentVisite].anomalieList.filter(
                                  (a) => a !== anomalie.id,
                                ),
                              );
                            }
                          }}
                        />
                      </div>
                    ))}
                </Accordion.Body>
              </Accordion.Item>
            ))}
          </Accordion>
        </Tab>
        <Tab eventKey={"suiteList"} title={"Suites"}>
          <FieldArray
            name={`visiteList.${currentVisite}.suiteList`}
            render={(arrayHelpers) => (
              <>
                <Button
                  variant={"primary"}
                  onClick={() =>
                    arrayHelpers.push({
                      oldebVisiteSuiteDate: new Date(),
                      oldebVisiteSuiteOldebTypeSuiteId: undefined,
                      oldebVisiteSuiteObservation: undefined,
                    })
                  }
                >
                  <IconAdd /> Ajouter une suite
                </Button>
                {currentVisiteSuite != null && (
                  <DeleteButton
                    title={"Supprimer la suite"}
                    onClick={() => {
                      const toRemove = currentVisiteSuite;
                      setCurrentVisiteSuite(null);
                      arrayHelpers.remove(toRemove);
                    }}
                  />
                )}
                <Table bordered striped hover>
                  <tr>
                    <th>Date</th>
                    <th>Type</th>
                    <th>Observation</th>
                  </tr>
                  {visiteList[currentVisite]?.suiteList?.map(
                    (visiteSuite, idx) => (
                      <tr key={idx} onClick={() => setCurrentVisiteSuite(idx)}>
                        <td>
                          {visiteSuite.oldebVisiteSuiteDate &&
                            formatDate(visiteSuite.oldebVisiteSuiteDate)}
                        </td>
                        <td>
                          {
                            typeOldebTypeSuiteState?.data?.find(
                              (v) =>
                                v.id ===
                                visiteSuite.oldebVisiteSuiteOldebTypeSuiteId,
                            )?.libelle
                          }
                        </td>
                        <td>{visiteSuite.oldebVisiteSuiteObservation}</td>
                      </tr>
                    ),
                  )}
                </Table>
                {currentVisiteSuite != null && (
                  <VisiteSuiteForm
                    fieldName={`visiteList.${currentVisite}`}
                    setFieldValue={setFieldValue}
                    currentVisiteSuiteList={visiteList[currentVisite].suiteList}
                    currentVisiteSuiteIdx={currentVisiteSuite}
                    typeOldebTypeSuite={typeOldebTypeSuiteState?.data}
                  />
                )}
              </>
            )}
          />
        </Tab>
        <Tab eventKey={"documentList"} title={"Documents"}>
          <FileInput
            multiple={true}
            accept={"image/*,.pdf"}
            required={false}
            name={`documentHolder`}
            onChange={(e) => {
              if (e.target.files.length) {
                // /!\ CASCADE RÉALISÉE PAR UN PROFESSIONNEL /!\
                setFieldValue(`documentList`, {
                  ...documentList,
                  [visiteList[currentVisite].oldebVisiteCode]: [
                    ...(documentList[
                      visiteList[currentVisite].oldebVisiteCode
                    ] || []),
                    ...e.target.files,
                  ],
                });
              }
              e.target.value = null;
              setFieldValue(`documentHolder`, null);
            }}
          />
          {visiteList[currentVisite].documentList.length > 0 && (
            <FieldSet title={"Fichiers existants"}>
              {visiteList[currentVisite].documentList?.map((file, index) => (
                <div key={index}>
                  {file.documentNom}
                  <Badge
                    pill
                    variant={"info"}
                    onClick={() => {
                      const win = window.open(file.documentUrl, "_blank");
                      win.focus();
                    }}
                  >
                    <IconExport />
                  </Badge>
                  <Badge
                    pill
                    variant={"danger"}
                    onClick={() => {
                      setFieldValue(
                        `visiteList.${currentVisite}.documentList`,
                        visiteList[currentVisite].documentList.filter(
                          (v, i) => i !== index,
                        ),
                      );
                    }}
                  >
                    <IconDelete />
                  </Badge>
                </div>
              ))}
            </FieldSet>
          )}
          {documentList[visiteList[currentVisite].oldebVisiteCode] && (
            <FieldSet title={"Fichiers ajoutés"}>
              {documentList[visiteList[currentVisite].oldebVisiteCode].map(
                (file, index) => (
                  <div key={index}>
                    {file.name}
                    <Badge
                      pill
                      variant={"danger"}
                      onClick={() => {
                        setFieldValue(
                          `documentList.${visiteList[currentVisite].oldebVisiteCode}`,
                          documentList[
                            visiteList[currentVisite].oldebVisiteCode
                          ].filter((v, i) => i !== index),
                        );
                      }}
                    >
                      <IconDelete />
                    </Badge>
                  </div>
                ),
              )}
            </FieldSet>
          )}
        </Tab>
        <Tab eventKey={"observation"} title={"Observations"}>
          <TextAreaInput
            name={`visiteList.${currentVisite}.oldebVisiteObservation`}
            required={false}
          />
        </Tab>
      </Tabs>
    </>
  );
};

const VisiteSuiteForm = ({
  fieldName,
  setFieldValue,
  currentVisiteSuiteList,
  currentVisiteSuiteIdx,
  typeOldebTypeSuite,
}: {
  fieldName: string;
  setFieldValue: (...object) => object;
  currentVisiteSuiteList: OldebVisiteSuiteType[];
  currentVisiteSuiteIdx: number;
  typeOldebTypeSuite: IdCodeLibelleType[];
}) => {
  return (
    <Row>
      <Col>
        <DateTimeInput
          label="Date"
          name={`${fieldName}.suiteList.${currentVisiteSuiteIdx}.oldebVisiteSuiteDate`}
          required={true}
          value={
            currentVisiteSuiteList[currentVisiteSuiteIdx]
              .oldebVisiteSuiteDate &&
            formatDateTimeForDateTimeInput(
              currentVisiteSuiteList[currentVisiteSuiteIdx]
                .oldebVisiteSuiteDate,
            )
          }
        />
      </Col>
      <Col>
        <SelectForm
          name={`${fieldName}.suiteList.${currentVisiteSuiteIdx}.oldebVisiteSuiteOldebTypeSuiteId`}
          label="Type"
          listIdCodeLibelle={typeOldebTypeSuite}
          defaultValue={typeOldebTypeSuite?.find(
            (v) =>
              v.id ===
              currentVisiteSuiteList[currentVisiteSuiteIdx]
                .oldebVisiteSuiteOldebTypeSuiteId,
          )}
          required={true}
          setFieldValue={setFieldValue}
        />
      </Col>
      <Col>
        <TextInput
          label="Observation"
          name={`${fieldName}.suiteList.${currentVisiteSuiteIdx}.oldebVisiteSuiteObservation`}
          required={false}
        />
      </Col>
    </Row>
  );
};

export default OldebForm;
