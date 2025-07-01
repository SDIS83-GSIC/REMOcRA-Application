import { useFormikContext } from "formik";
import { WKT } from "ol/format";
import { transform } from "ol/proj";
import { useEffect, useState } from "react";
import { Badge, Col, Row, Tab, Tabs } from "react-bootstrap";
import { array, number, object, string } from "yup";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import DeleteButton from "../../../components/Button/DeleteButton.tsx";
import {
  useGet,
  useGetRun,
  usePost,
} from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FieldSet,
  FileInput,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import {
  IconDelete,
  IconExport,
  IconWarning,
} from "../../../components/Icon/Icon.tsx";
import DIRECTION from "../../../enums/DirectionEnum.tsx";
import nomenclaturesEnum from "../../../enums/NomenclaturesEnum.tsx";
import OUI_NON_NA from "../../../enums/OuiNonNAEnum.tsx";
import RISQUE_METEO from "../../../enums/RisqueMeteoEnum.tsx";
import TypeSystemeSrid from "../../../enums/TypeSystemeSrid.tsx";
import url from "../../../module/fetch.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import {
  date,
  numberPositif,
  percentage,
  requiredDate,
  requiredString,
} from "../../../module/validators.tsx";
import { formatDateTimeForDateTimeInput } from "../../../utils/formatDateUtils.tsx";

type FormType = {
  rcci: RcciFormType;
  documentList: object;
};

type RcciFormType = {
  rcciId?: string;
  rcciCommentaireConclusion?: string;
  rcciComplement?: string;
  rcciCarroyageDfci?: string;
  rcciDateIncendie: Date;
  rcciDateModification: Date;
  rcciDirectionVent?: string;
  rcciForceVent?: number;
  rcciForcesOrdre?: string;
  rcciGdh?: Date;
  rcciGelLieux?: OUI_NON_NA;
  rcciGeometrie: string;
  rcciX: string; // placeholder pour formulaire
  rcciY: string; // placeholder pour formulaire
  rcciSrid: string; // placeholder pour formulaire
  rcciHygrometrie?: number;
  rcciIndiceRothermel?: number;
  rcciPointEclosion: string;
  rcciPremierCos?: string;
  rcciPremierEngin?: string;
  rcciSuperficieFinale?: number;
  rcciSuperficieReferent?: number;
  rcciSuperficieSecours?: number;
  rcciTemperature?: number;
  rcciVentLocal?: OUI_NON_NA;
  rcciVoieTexte?: string;
  rcciVoieId?: string;
  rcciCommuneId?: string;
  rcciRcciTypePrometheeFamilleId?: string; // placeholder pour formulaire
  rcciRcciTypePrometheePartitionId?: string; // placeholder pour formulaire
  rcciRcciTypePrometheeCategorieId?: string;
  rcciRcciTypeDegreCertitudeId?: string;
  rcciRcciTypeOrigineAlerteId: string;
  rcciRcciArriveeDdtmOnfId?: string;
  rcciRcciArriveeSdisId?: string;
  rcciRcciArriveeGendarmerieId?: string;
  rcciRcciArriveePoliceId?: string;
  rcciRcciIndiceRothermelId?: string;
  rcciUtilisateurId: string;
  rcciRisqueMeteo: RISQUE_METEO;
  documentList: RcciDocumentType[];
};

type RcciDocumentType = {
  documentId: string;
  documentNom: string;
  documentUrl: string;
};

type SectionWarning = {
  renseignements: boolean;
  constatations: boolean;
};

export const getInitialValues = (
  data: {
    rcci: RcciFormType;
    documentList?: object;
  },
  userId,
) => {
  const [srid, geom] = data.rcci?.rcciGeometrie
    ? data.rcci.rcciGeometrie.split(/SRID=|;/).filter(Boolean)
    : [null, null];
  const feature = geom ? new WKT().readFeature(geom) : null;
  const [x, y] = feature
    ? feature.getGeometry().getFlatCoordinates()
    : [null, null];
  return {
    rcci: {
      rcciId: data.rcci?.rcciId ?? undefined,
      rcciCommentaireConclusion:
        data.rcci?.rcciCommentaireConclusion ?? undefined,
      rcciComplement: data.rcci?.rcciComplement ?? undefined,
      rcciCarroyageDfci: data.rcci?.rcciCarroyageDfci ?? undefined,
      rcciDateIncendie: formatDateTimeForDateTimeInput(
        data.rcci?.rcciDateIncendie ?? new Date(),
      ),
      rcciDateModification: formatDateTimeForDateTimeInput(
        data.rcci?.rcciDateModification ?? new Date(),
      ),
      rcciDirectionVent: data.rcci?.rcciDirectionVent ?? undefined,
      rcciForceVent: data.rcci?.rcciForceVent ?? undefined,
      rcciForcesOrdre: data.rcci?.rcciForcesOrdre ?? undefined,
      rcciGdh: data.rcci?.rcciGdh ?? undefined,
      rcciGelLieux: data.rcci?.rcciGelLieux ?? undefined,
      rcciGeometrie: data.rcci?.rcciGeometrie ?? undefined,
      rcciX: x,
      rcciY: y,
      rcciSrid: srid ?? null,
      rcciHygrometrie: data.rcci?.rcciHygrometrie ?? undefined,
      rcciRcciIndiceRothermelId:
        data.rcci?.rcciRcciIndiceRothermelId ?? undefined,
      rcciPointEclosion: data.rcci?.rcciPointEclosion ?? null,
      rcciPremierCos: data.rcci?.rcciPremierCos ?? undefined,
      rcciPremierEngin: data.rcci?.rcciPremierEngin ?? undefined,
      rcciSuperficieFinale: data.rcci?.rcciSuperficieFinale ?? undefined,
      rcciSuperficieReferent: data.rcci?.rcciSuperficieReferent ?? undefined,
      rcciSuperficieSecours: data.rcci?.rcciSuperficieSecours ?? undefined,
      rcciTemperature: data.rcci?.rcciTemperature ?? undefined,
      rcciVentLocal: data.rcci?.rcciVentLocal ?? undefined,
      rcciVoieTexte: data.rcci?.rcciVoieTexte ?? undefined,
      rcciVoieId: data.rcci?.rcciVoieId ?? undefined,
      rcciCommuneId: data.rcci?.rcciCommuneId ?? undefined,
      rcciRcciTypePrometheeFamilleId:
        data.rcci?.rcciRcciTypePrometheeFamilleId ?? undefined,
      rcciRcciTypePrometheePartitionId:
        data.rcci?.rcciRcciTypePrometheePartitionId ?? undefined,
      rcciRcciTypePrometheeCategorieId:
        data.rcci?.rcciRcciTypePrometheeCategorieId ?? undefined,
      rcciRcciTypeDegreCertitudeId:
        data.rcci?.rcciRcciTypeDegreCertitudeId ?? undefined,
      rcciRcciTypeOrigineAlerteId:
        data.rcci?.rcciRcciTypeOrigineAlerteId ?? null,
      rcciRcciArriveeDdtmOnfId:
        data.rcci?.rcciRcciArriveeDdtmOnfId ?? undefined,
      rcciRcciArriveeSdisId: data.rcci?.rcciRcciArriveeSdisId ?? undefined,
      rcciRcciArriveeGendarmerieId:
        data.rcci?.rcciRcciArriveeGendarmerieId ?? undefined,
      rcciRcciArriveePoliceId: data.rcci?.rcciRcciArriveePoliceId ?? undefined,
      rcciRisqueMeteo: data.rcci?.rcciRisqueMeteo ?? undefined,
      rcciUtilisateurId: data.rcci?.rcciUtilisateurId ?? userId,
      documentList: data.rcci?.documentList ?? undefined,
      voieSaisieLibre:
        data?.rcci?.rcciVoieTexte != null &&
        data?.rcci?.rcciVoieTexte?.trim() !== "",
      ...data.rcci,
    },
    documentList: data?.documentList || [],
  };
};

export const prepareValues = (values: {
  rcci: RcciFormType;
  documentList?: object;
}) => {
  const formData = new FormData();
  formData.append(
    "rcci",
    JSON.stringify({
      rcciId: values.rcci.rcciId,
      rcciCommentaireConclusion: values.rcci.rcciCommentaireConclusion,
      rcciComplement: values.rcci.rcciComplement,
      rcciCarroyageDfci: values.rcci.rcciCarroyageDfci,
      rcciDateIncendie: values.rcci.rcciDateIncendie
        ? new Date(values.rcci.rcciDateIncendie).toISOString()
        : null,
      rcciDateModification: values.rcci.rcciDateModification
        ? new Date(values.rcci.rcciDateModification).toISOString()
        : new Date(),
      rcciDirectionVent: values.rcci.rcciDirectionVent,
      rcciForceVent: values.rcci.rcciForceVent,
      rcciForcesOrdre: values.rcci.rcciForcesOrdre,
      rcciGdh: values.rcci.rcciGdh
        ? new Date(values.rcci.rcciGdh).toISOString()
        : null,
      rcciGelLieux: values.rcci.rcciGelLieux,
      rcciGeometrie:
        "SRID=" +
        values.rcci.rcciSrid +
        ";POINT(" +
        values.rcci.rcciX +
        " " +
        values.rcci.rcciY +
        ")",
      rcciHygrometrie: values.rcci.rcciHygrometrie,
      rcciRcciIndiceRothermelId: values.rcci.rcciRcciIndiceRothermelId,
      rcciPointEclosion: values.rcci.rcciPointEclosion,
      rcciPremierCos: values.rcci.rcciPremierCos,
      rcciPremierEngin: values.rcci.rcciPremierEngin,
      rcciSuperficieFinale: values.rcci.rcciSuperficieFinale,
      rcciSuperficieReferent: values.rcci.rcciSuperficieReferent,
      rcciSuperficieSecours: values.rcci.rcciSuperficieSecours,
      rcciTemperature: values.rcci.rcciTemperature,
      rcciVentLocal: values.rcci.rcciVentLocal,
      rcciVoieTexte:
        values.rcci.rcciVoieTexte != null &&
        values.rcci.rcciVoieTexte?.trim() !== ""
          ? values.rcci.rcciVoieTexte
          : null,
      rcciVoieId: values.rcci.rcciVoieId,
      rcciCommuneId: values.rcci.rcciCommuneId,
      rcciRcciTypePrometheeCategorieId:
        values.rcci.rcciRcciTypePrometheeCategorieId,
      rcciRcciTypeDegreCertitudeId: values.rcci.rcciRcciTypeDegreCertitudeId,
      rcciRcciTypeOrigineAlerteId: values.rcci.rcciRcciTypeOrigineAlerteId,
      rcciRcciArriveeDdtmOnfId: values.rcci.rcciRcciArriveeDdtmOnfId,
      rcciRcciArriveeSdisId: values.rcci.rcciRcciArriveeSdisId,
      rcciRcciArriveeGendarmerieId: values.rcci.rcciRcciArriveeGendarmerieId,
      rcciRcciArriveePoliceId: values.rcci.rcciRcciArriveePoliceId,
      rcciUtilisateurId: values.rcci.rcciUtilisateurId,
      rcciRisqueMeteo: values.rcci?.rcciRisqueMeteo,
      documentList: values.rcci.documentList,
    }),
  );
  if (values.documentList) {
    values.documentList.forEach((file) => {
      formData.append(`document[]`, file);
    });
  }
  return formData;
};

export const validationSchema = object({
  rcci: object({
    rcciId: string(),
    rcciCommentaireConclusion: string(),
    rcciComplement: string(),
    rcciCarroyageDfci: string(),
    rcciDateIncendie: requiredDate,
    rcciDateModification: requiredDate,
    rcciDirectionVent: string(),
    rcciForceVent: numberPositif,
    rcciForcesOrdre: string(),
    rcciGdh: date,
    rcciGelLieux: string(),
    rcciX: requiredString,
    rcciY: requiredString,
    rcciSrid: requiredString,
    rcciHygrometrie: percentage,
    rcciIndiceRothermelId: string(),
    rcciPointEclosion: requiredString,
    rcciPremierCos: string(),
    rcciPremierEngin: string(),
    rcciSuperficieFinale: numberPositif,
    rcciSuperficieReferent: numberPositif,
    rcciSuperficieSecours: numberPositif,
    rcciTemperature: number(),
    rcciVentLocal: string(),
    rcciVoieTexte: string(),
    rcciVoieId: string(),
    rcciCommuneId: string(),
    rcciRcciTypePrometheeCategorieId: string(),
    rcciRcciTypeDegreCertitudeId: string(),
    rcciRcciTypeOrigineAlerteId: requiredString,
    rcciRcciArriveeDdtmOnfId: string(),
    rcciRcciArriveeSdisId: string(),
    rcciRcciArriveeGendarmerieId: string(),
    rcciRcciArriveePoliceId: string(),
    rcciUtilisateurId: requiredString,
    rcciRisqueMeteo: string(),
    documentList: array(),
  }),
});

const RcciForm = () => {
  const { user, srid } = useAppContext();
  const [currentTab, setCurrentTab] = useState("renseignements");
  const [sectionWarning, setSectionWarning] = useState<SectionWarning>();
  const { values, setFieldValue, errors, isSubmitting } =
    useFormikContext<FormType>();

  const { error: errorToast } = useToastContext();

  const sridList = TypeSystemeSrid.filter(
    (v) => v.actif || v.srid === srid,
  ).map((v) => ({
    id: `${v.srid}`,
    code: `${v.srid}`,
    libelle: v.nomSystem,
  }));

  const rcciTypePrometheeFamilleState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.RCCI_TYPE_PROMETHEE_FAMILLE}`,
  );
  const rcciTypePrometheePartitionState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.RCCI_TYPE_PROMETHEE_PARTITION}`,
  );
  const rcciTypePrometheeCategorieState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.RCCI_TYPE_PROMETHEE_CATEGORIE}`,
  );
  const rcciTypeDegreCertitudeState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.RCCI_TYPE_DEGRE_CERTITUDE}`,
  );
  const rcciTypeOrigineAlerteState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.RCCI_TYPE_ORIGINE_ALERTE}`,
  );

  const rcciIndiceRothermelState = useGet(
    url`/api/nomenclatures/list/${nomenclaturesEnum.RCCI_INDICE_ROTHERMEL}`,
  );

  const directionList = Object.entries(DIRECTION).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });
  const referentielState = useGetRun(
    url`/api/rcci/refs?${{ geometrie: `SRID=${values.rcci.rcciSrid};POINT(${values.rcci.rcciX} ${values.rcci.rcciY})` }}`,
  );

  const { isLoading, data, run } = usePost(url`/api/dfci/check`);

  const { run: runReferentielState } = referentielState;

  useEffect(() => {
    if (!values.rcci.rcciX || !values.rcci.rcciY) {
      return;
    }
    run({
      geometry: `SRID=${values.rcci.rcciSrid};POINT(${values.rcci.rcciX} ${values.rcci.rcciY})`,
    });

    runReferentielState({
      geometrie: `SRID=${values.rcci.rcciSrid};POINT(${values.rcci.rcciX} ${values.rcci.rcciY})`,
    });
  }, [
    run,
    values.rcci.rcciSrid,
    values.rcci.rcciX,
    values.rcci.rcciY,
    runReferentielState,
  ]);

  useEffect(() => {
    setFieldValue("rcci.rcciCarroyageDfci", data?.carroyageDfciCoordonneee);
  }, [isLoading, data, setFieldValue]);

  useEffect(() => {
    if (isSubmitting && errors.rcci) {
      setSectionWarning({
        renseignements:
          errors.rcci.rcciDateIncendie ||
          errors.rcci.rcciRcciTypeOrigineAlerteId,
        constatations:
          errors.rcci.rcciSrid ||
          errors.rcci.rcciX ||
          errors.rcci.rcciY ||
          errors.rcci.rcciPointEclosion,
      });
      errorToast("Champs obligatoires non renseignés");
    }
  }, [isSubmitting, errors, errorToast]);

  const listOuiNonNA = Object.entries(OUI_NON_NA).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });

  const listRisqueMeteo = Object.entries(RISQUE_METEO).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });

  return (
    <>
      <Row>
        <Col>Nouveau départ en cours de saisie par {user.username}</Col>
      </Row>
      <Tabs
        id="tabs"
        activeKey={currentTab}
        onSelect={(key) => setCurrentTab(key)}
      >
        <Tab
          eventKey="renseignements"
          title={
            <span>
              Renseignements incendie{" "}
              {sectionWarning?.renseignements && <IconWarning />}
            </span>
          }
        >
          <FieldSet title={"Renseignements"}>
            <Row>
              <Col>
                <DateTimeInput
                  label="Date"
                  name={"rcci.rcciDateIncendie"}
                  required={true}
                  value={values.rcci.rcciDateIncendie}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciTypeOrigineAlerteId"}
                  label="Origine"
                  listIdCodeLibelle={rcciTypeOrigineAlerteState.data}
                  defaultValue={rcciTypeOrigineAlerteState.data?.find(
                    (v) => v.id === values.rcci.rcciRcciTypeOrigineAlerteId,
                  )}
                  required={true}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectForm
                  label="Commune"
                  name={"rcci.rcciCommuneId"}
                  listIdCodeLibelle={referentielState.data?.listCommune}
                  defaultValue={referentielState.data?.listCommune?.find(
                    (v) => v.id === values.rcci.rcciCommuneId,
                  )}
                  setFieldValue={setFieldValue}
                  required={false}
                />
              </Col>

              <Col>
                <SelectForm
                  name={"rcci.rcciVoieId"}
                  listIdCodeLibelle={referentielState.data?.listVoie?.filter(
                    (e) => e.communeId === values.rcci.rcciCommuneId,
                  )}
                  label="Voie"
                  defaultValue={referentielState.data?.listVoie?.find(
                    (e) => e.id === values.rcci.rcciVoieId,
                  )}
                  required={!values.voieSaisieLibre} // Requis si la saisie libre n'est pas activée ; si elle l'est, TODO XOR entre les 2 types
                  setFieldValue={setFieldValue}
                  disabled={
                    values.rcci.rcciVoieTexte != null &&
                    values.rcci.rcciVoieTexte?.trim() !== ""
                  }
                />
                <CheckBoxInput
                  name="voieSaisieLibre"
                  label="Voie non trouvée"
                />
                {values.voieSaisieLibre && (
                  <TextInput
                    name="rcci.rcciVoieTexte"
                    label="Voie (saisie libre)"
                    required={false}
                    disabled={
                      values.rcci.rcciVoieId != null &&
                      values.rcci.rcciVoieId?.trim() !== ""
                    }
                  />
                )}
              </Col>
            </Row>
            <Row>
              <Col>
                <TextAreaInput
                  label="Complément"
                  name={"rcci.rcciComplement"}
                  required={false}
                />
              </Col>
            </Row>
          </FieldSet>
          <FieldSet title={"Arrivée sur site des référents"}>
            <Row>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciArriveeDdtmOnfId"}
                  label="DDTM - ONF"
                  listIdCodeLibelle={referentielState.data?.ddtmonf}
                  defaultValue={referentielState.data?.ddtmonf?.find(
                    (v) => v.id === values.rcci.rcciRcciArriveeDdtmOnfId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciArriveeSdisId"}
                  label="SDIS"
                  listIdCodeLibelle={referentielState.data?.sdis}
                  defaultValue={referentielState.data?.sdis?.find(
                    (v) => v.id === values.rcci.rcciRcciArriveeSdisId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectForm
                  name={"rcci.rcciArriveeGendarmerieId"}
                  label="Gendarmerie"
                  listIdCodeLibelle={referentielState.data?.gendarmerie}
                  defaultValue={referentielState.data?.gendarmerie?.find(
                    (v) => v.id === values.rcci.rcciRcciArriveeGendarmerieId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciArriveePoliceId"}
                  label="Police"
                  listIdCodeLibelle={referentielState.data?.police}
                  defaultValue={referentielState.data?.police?.find(
                    (v) => v.id === values.rcci.rcciRcciArriveePoliceId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
          </FieldSet>
        </Tab>
        <Tab
          eventKey="constatations"
          title={
            <span>
              Constatations {sectionWarning?.constatations && <IconWarning />}
            </span>
          }
        >
          <FieldSet title={"Coordonnées"}>
            <Row>
              <Col />
              <Col>
                <SelectForm
                  label="Système"
                  name={"rcci.rcciSrid"}
                  required={true}
                  listIdCodeLibelle={sridList}
                  defaultValue={sridList.find(
                    (v) => v.id === values.rcci.rcciSrid,
                  )}
                  onChange={(e) => {
                    const prevSrid = values.rcci.rcciSrid;
                    const nextSrid = e.target.value;
                    if (prevSrid !== nextSrid) {
                      setFieldValue(e.target.name, nextSrid);
                      if (values.rcci.rcciX && values.rcci.rcciY) {
                        const [x, y] = transform(
                          [
                            parseFloat(values.rcci.rcciX),
                            parseFloat(values.rcci.rcciY),
                          ],
                          `EPSG:${prevSrid}`,
                          `EPSG:${nextSrid}`,
                        );
                        setFieldValue("rcci.rcciX", x);
                        setFieldValue("rcci.rcciY", y);
                      }
                    }
                  }}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <TextInput label="X" name={"rcci.rcciX"} required={true} />
              </Col>
              <Col>
                <TextInput label="Y" name={"rcci.rcciY"} required={true} />
              </Col>
            </Row>
            <Row>
              <Col>
                <TextInput
                  label="Carroyage DFCI"
                  name={"rcci.rcciCarroyageDfci"}
                  required={false}
                  readOnly={true}
                  disabled={true}
                />
              </Col>
              <Col>
                <TextInput
                  label="Point d'éclosion"
                  name={"rcci.rcciPointEclosion"}
                  required={true}
                />
              </Col>
            </Row>
          </FieldSet>
          <FieldSet title={"Météo"}>
            <Row>
              <Col>
                <DateTimeInput
                  label="GDH"
                  name={"rcci.rcciGdh"}
                  required={false}
                  value={
                    values.rcci.rcciGdh &&
                    formatDateTimeForDateTimeInput(values.rcci.rcciGdh)
                  }
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciVentLocal"}
                  listIdCodeLibelle={listOuiNonNA}
                  label="Vent local"
                  defaultValue={listOuiNonNA?.find(
                    (v) => v.id === values?.rcci.rcciVentLocal,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <NumberInput
                  label={"Hygrométrie (%)"}
                  name={"rcci.rcciHygrometrie"}
                  required={false}
                  min={0}
                  max={100}
                  step={1}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciDirectionVent"}
                  listIdCodeLibelle={directionList}
                  label="Direction"
                  defaultValue={directionList?.find(
                    (v) => v.id === values?.rcci.rcciDirectionVent,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <NumberInput
                  label={"Température (°C)"}
                  name={"rcci.rcciTemperature"}
                  required={false}
                  step={1}
                />
              </Col>
              <Col>
                <NumberInput
                  label={"Force (km/h)"}
                  name={"rcci.rcciForceVent"}
                  required={false}
                  min={0}
                  step={1}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciIndiceRothermelId"}
                  label="Indice ROTHERMEL"
                  listIdCodeLibelle={rcciIndiceRothermelState.data}
                  defaultValue={rcciIndiceRothermelState.data?.find(
                    (v) => v.id === values.rcci.rcciRcciIndiceRothermelId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciRisqueMeteo"}
                  listIdCodeLibelle={listRisqueMeteo}
                  label="Risque météo"
                  defaultValue={listRisqueMeteo?.find(
                    (v) => v.id === values?.rcci.rcciRisqueMeteo,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
          </FieldSet>
          <FieldSet title={"Feu"}>
            <Row>
              <Col>
                <NumberInput
                  label={"Superficie arrivée secours (m²)"}
                  name={"rcci.recciSuperficieSecours"}
                  required={false}
                  step={0.01}
                  min={0}
                />
              </Col>
              <Col>
                <TextInput
                  label={"Premier engin sur les lieux"}
                  name={"rcci.rcciPremierEngin"}
                  required={false}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <NumberInput
                  label={"Superficie arrivée référent (m²)"}
                  name={"rcci.recciSuperficieReferent"}
                  required={false}
                  step={0.01}
                  min={0}
                />
              </Col>
              <Col>
                <TextInput
                  label={"Premier COS"}
                  name={"rcci.rcciPremierEngin"}
                  required={false}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <NumberInput
                  label={"Superficie finale (m²)"}
                  name={"rcci.recciSuperficieFinale"}
                  required={false}
                  step={0.01}
                  min={0}
                />
              </Col>
              <Col>
                <TextInput
                  label={"Forces de l'ordre présentes"}
                  name={"rcci.rcciForcesOrddre"}
                  required={false}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <SelectForm
                  name={"rcci.rcciGelLieux"}
                  listIdCodeLibelle={listOuiNonNA}
                  label="Gel des lieux"
                  defaultValue={listOuiNonNA?.find(
                    (v) => v.id === values?.rcci.rcciGelLieux,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
              <Col />
            </Row>
          </FieldSet>
        </Tab>
        <Tab eventKey="causes" title="Causes et résultats des investigations">
          <FieldSet title={"Localisation et accès"}>
            <Row>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciTypePrometheeFamilleId"}
                  label="Prométhée famille"
                  listIdCodeLibelle={rcciTypePrometheeFamilleState.data}
                  defaultValue={rcciTypePrometheeFamilleState.data?.find(
                    (v) => v.id === values.rcci.rcciRcciTypePrometheeFamilleId,
                  )}
                  required={false}
                  setFieldValue={(name, value) => {
                    setFieldValue(
                      "rcci.rcciRcciTypePrometheePartitionId",
                      null,
                    );
                    setFieldValue(
                      "rcci.rcciRcciTypePrometheeCategorieId",
                      undefined,
                    );
                    setFieldValue(name, value);
                  }}
                />
                <SelectForm
                  name={"rcci.rcciRcciTypePrometheePartitionId"}
                  label="Prométhée partition"
                  listIdCodeLibelle={rcciTypePrometheePartitionState.data?.filter(
                    (v) =>
                      v.lienId === values.rcci.rcciRcciTypePrometheeFamilleId,
                  )}
                  defaultValue={rcciTypePrometheePartitionState.data?.find(
                    (v) =>
                      v.id === values.rcci.rcciRcciTypePrometheePartitionId,
                  )}
                  required={false}
                  setFieldValue={(name, value) => {
                    setFieldValue(
                      "rcci.rcciRcciTypePrometheeCategorieId",
                      null,
                    );
                    setFieldValue(name, value);
                  }}
                  disabled={values.rcci.rcciRcciTypePrometheeFamilleId == null}
                />
                <SelectForm
                  name={"rcci.rcciRcciTypePrometheeCategorieId"}
                  label="Prométhée catégorie"
                  listIdCodeLibelle={rcciTypePrometheeCategorieState.data?.filter(
                    (v) =>
                      v.lienId === values.rcci.rcciRcciTypePrometheePartitionId,
                  )}
                  defaultValue={rcciTypePrometheeCategorieState.data?.find(
                    (v) =>
                      v.id === values.rcci.rcciRcciTypePrometheeCategorieId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                  disabled={
                    values.rcci.rcciRcciTypePrometheeFamilleId == null ||
                    values.rcci.rcciRcciTypePrometheePartitionId == null
                  }
                />
              </Col>
              <Col>
                <SelectForm
                  name={"rcci.rcciRcciTypeDegreCertitudeId"}
                  label="Degré certitude"
                  listIdCodeLibelle={rcciTypeDegreCertitudeState.data}
                  defaultValue={rcciTypeDegreCertitudeState.data?.find(
                    (v) => v.id === values.rcci.rcciRcciTypeDegreCertitudeId,
                  )}
                  required={false}
                  setFieldValue={setFieldValue}
                />
              </Col>
            </Row>
            <Row>
              <Col>
                <TextAreaInput
                  name={"rcci.rcci"}
                  label={"Commentaires et conclusions"}
                  required={false}
                />
              </Col>
            </Row>
          </FieldSet>
        </Tab>
        <Tab eventKey="documents" title="Documents">
          {values.rcci.documentList?.length > 0 && (
            <FieldSet title={"Documents existants"}>
              {values.rcci.documentList?.map((file, index) => (
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
                        `rcci.documentList`,
                        values.rcci.documentList.filter((v, i) => i !== index),
                      );
                    }}
                  >
                    <IconDelete />
                  </Badge>
                </div>
              ))}
            </FieldSet>
          )}
          <FieldSet title={"Documents ajoutés"}>
            <FileInput
              multiple={true}
              accept={"image/*,.pdf"}
              required={false}
              name={`documentHolder`}
              onChange={(e) => {
                if (e.target.files.length) {
                  setFieldValue(`documentList`, [
                    ...values.documentList,
                    ...e.target.files,
                  ]);
                }
                setFieldValue(`documentHolder`, null);
              }}
            />
            {values.documentList?.length > 0 &&
              values.documentList.map((file, index) => (
                <div key={index}>
                  {file.name}
                  <DeleteButton
                    onClick={() => {
                      setFieldValue(
                        "documentList",
                        values.documentList.filter((v, i) => i !== index),
                      );
                    }}
                  >
                    <IconDelete />
                  </DeleteButton>
                </div>
              ))}
          </FieldSet>
        </Tab>
      </Tabs>
    </>
  );
};

export default RcciForm;
