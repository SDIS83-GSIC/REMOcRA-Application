import { useFormikContext } from "formik";
import { useEffect, useMemo } from "react";
import { Form } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import { object } from "yup";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FormContainer,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import FormDocuments, {
  setDocumentInFormData,
} from "../../components/Form/FormDocuments.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SelectNomenclaturesForm from "../../components/Form/SelectNomenclaturesForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconCreate, IconEdit } from "../../components/Icon/Icon.tsx";
import { hasDroit } from "../../droits.tsx";
import DISPONIBILITE_PEI from "../../enums/DisponibiliteEnum.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import NOMENCLATURE from "../../enums/NomenclaturesEnum.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import TYPE_NATURE_DECI from "../../enums/TypeNatureDeci.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";
import TypeSystemeSrid from "../../enums/TypeSystemeSrid.tsx";
import url from "../../module/fetch.tsx";
import { requiredNumber, requiredString } from "../../module/validators.tsx";
import { URLS } from "../../routes.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

export const getInitialValues = (data?: PeiEntity) => ({
  peiId: data?.peiId ?? null,
  peiNumeroComplet: data?.peiNumeroComplet ?? null,
  peiNumeroInterne: data?.peiNumeroInterne ?? null,
  peiTypePei: data?.peiTypePei ?? null,
  peiDisponibiliteTerrestre: data?.peiDisponibiliteTerrestre ?? null,
  peiAnneeFabrication: data?.peiAnneeFabrication ?? null,

  coordonneeX: data?.coordonneeX ?? null,
  coordonneeY: data?.coordonneeY ?? null,
  srid: data?.srid ?? null,
  typeSystemeSrid:
    TypeSystemeSrid.find((e) => e.srid === data?.srid)?.srid ??
    TypeSystemeSrid[0].srid,

  coordonneeXToDisplay: data?.coordonneeX ?? null,
  coordonneeYToDisplay: data?.coordonneeY ?? null,

  peiAutoriteDeciId: data?.peiAutoriteDeciId ?? null,
  peiServicePublicDeciId: data?.peiServicePublicDeciId ?? null,
  peiMaintenanceDeciId: data?.peiMaintenanceDeciId ?? null,

  peiCommuneId: data?.peiCommuneId ?? null,
  peiVoieId: data?.peiVoieId ?? null,
  peiVoieTexte: data?.peiVoieTexte ?? null,
  peiNumeroVoie: data?.peiNumeroVoie ?? null,
  peiSuffixeVoie: data?.peiSuffixeVoie ?? null,
  peiLieuDitId: data?.peiLieuDitId ?? null,
  peiCroisementId: data?.peiCroisementId ?? null,
  peiComplementAdresse: data?.peiComplementAdresse ?? null,
  peiEnFace: data?.peiEnFace ?? null,

  peiDomaineId: data?.peiDomaineId ?? null,
  peiNatureId: data?.peiNatureId ?? null,
  peiSiteId: data?.peiSiteId ?? null,
  peiGestionnaireId: data?.peiGestionnaireId ?? null,
  peiNatureDeciId: data?.peiNatureDeciId ?? null,
  peiNiveauId: data?.peiNiveauId ?? null,
  peiZoneSpecialeId: data?.peiZoneSpecialeId ?? null,

  // DONNEES PIBI
  pibiDiametreId: data?.pibiDiametreId ?? null,
  pibiServiceEauId: data?.pibiServiceEauId ?? null,
  pibiNumeroScp: data?.pibiNumeroScp ?? null,
  pibiRenversable: data?.pibiRenversable ?? null,
  pibiDispositifInviolabilite: data?.pibiDispositifInviolabilite ?? null,
  pibiModeleId: data?.pibiModeleId ?? null,
  pibiMarqueId: data?.pibiMarqueId ?? null,
  pibiReservoirId: data?.pibiReservoirId ?? null,
  pibiDebitRenforce: data?.pibiDebitRenforce ?? null,
  pibiTypeCanalisationId: data?.pibiTypeCanalisationId ?? null,
  pibiTypeReseauId: data?.pibiTypeReseauId ?? null,
  pibiDiametreCanalisation: data?.pibiDiametreCanalisation ?? null,
  pibiSurpresse: data?.pibiSurpresse ?? null,
  pibiAdditive: data?.pibiAdditive ?? null,
  pibiJumeleId: data?.pibiJumeleId ?? null,

  // DONNEES PENA
  penaCapacite: data?.penaCapacite ?? null,
  penaCapaciteIllimitee: data?.penaCapaciteIllimitee ?? null,
  penaCapaciteIncertaine: data?.penaCapaciteIncertaine ?? null,
  penaMateriauId: data?.penaMateriauId ?? null,
  penaQuantiteAppoint: data?.penaQuantiteAppoint ?? null,
  penaDisponibiliteHbe: data?.penaDisponibiliteHbe ?? null,
  penaEquipeHbe: data?.penaEquipeHbe ?? false,

  documents: data?.documents ?? [],

  voieSaisieLibre:
    data?.peiVoieTexte != null && data?.peiVoieTexte?.trim() !== "",
});

export const validationSchema = object({
  peiAutoriteDeciId: requiredString,
  peiServicePublicDeciId: requiredString,
  peiNatureId: requiredString,
  peiNatureDeciId: requiredString,

  peiCommuneId: requiredString,
  //peiVoieId: requiredString,
  // TODO ici un XOR entre voie et voieText ?
  peiDomaineId: requiredString,
  coordonneeX: requiredNumber,
  coordonneeY: requiredNumber,
});

export const prepareVariables = (values: PeiEntity, data?: PeiEntity) => {
  const formData = new FormData();

  setDocumentInFormData(values?.documents, data?.documents, formData);

  formData.append("peiTypePei", values.peiTypePei);

  const dataPei = {
    peiId: data?.peiId,
    peiNumeroComplet: values.peiNumeroComplet ?? null,
    peiNumeroInterne: values.peiNumeroInterne ?? null,
    peiTypePei: values.peiTypePei,
    peiDisponibiliteTerrestre:
      data?.peiDisponibiliteTerrestre ??
      DISPONIBILITE_PEI.INDISPONIBLE.toUpperCase(),
    peiAnneeFabrication: values?.peiAnneeFabrication ?? null,
    peiGeometrie:
      "SRID=" +
      values.typeSystemeSrid +
      ";POINT(" +
      values.coordonneeX +
      " " +
      values.coordonneeY +
      ")",

    peiAutoriteDeciId: values.peiAutoriteDeciId ?? null,
    peiServicePublicDeciId: values.peiServicePublicDeciId ?? null,
    peiMaintenanceDeciId: values.peiMaintenanceDeciId ?? null,

    peiCommuneId: values.peiCommuneId ?? null,
    peiVoieId: values.peiVoieId ?? null,
    peiVoieTexte:
      values.peiVoieTexte != null && values.peiVoieTexte?.trim() !== ""
        ? values.peiVoieTexte
        : null,
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

    coordonneeX: values?.coordonneeX ?? null,
    coordonneeY: values?.coordonneeY ?? null,

    // DONNEES INITIALES
    peiNumeroInterneInitial: data?.peiNumeroInterne ?? null,
    peiCommuneIdInitial: data?.peiCommuneId ?? null,
    peiZoneSpecialeIdInitial: data?.peiZoneSpecialeId ?? null,
    peiNatureDeciIdInitial: data?.peiNatureDeciId ?? null,
    peiDomaineIdInitial: data?.peiDomaineId ?? null,
  };

  formData.append(
    "peiData",
    JSON.stringify(
      values.peiTypePei === TYPE_PEI.PIBI
        ? {
            ...dataPei,
            // DONNEES PIBI
            pibiDiametreId: values.pibiDiametreId ?? null,
            pibiServiceEauId: values.pibiServiceEauId ?? null,
            pibiNumeroScp: values.pibiNumeroScp ?? null,
            pibiRenversable: values.pibiRenversable ?? null,
            pibiDispositifInviolabilite:
              values.pibiDispositifInviolabilite ?? null,
            pibiModeleId: values.pibiModeleId ?? null,
            pibiMarqueId: values.pibiMarqueId ?? null,
            pibiReservoirId: values.pibiReservoirId ?? null,
            pibiDebitRenforce: values.pibiDebitRenforce ?? null,
            pibiTypeCanalisationId: values.pibiTypeCanalisationId ?? null,
            pibiTypeReseauId: values.pibiTypeReseauId ?? null,
            pibiDiametreCanalisation: values.pibiDiametreCanalisation ?? null,
            pibiSurpresse: values.pibiSurpresse ?? null,
            pibiAdditive: values.pibiAdditive ?? null,
            pibiJumeleId: values.pibiJumeleId ?? null,
          }
        : {
            ...dataPei,

            // DONNEES PENA
            penaCapacite: values.penaCapacite ?? null,
            penaCapaciteIllimitee: values.penaCapaciteIllimitee ?? null,
            penaCapaciteIncertaine: values.penaCapaciteIncertaine ?? null,
            penaMateriauId: values.penaMateriauId ?? null,
            penaQuantiteAppoint: values.penaQuantiteAppoint ?? null,
            penaDisponibiliteHbe:
              data?.penaDisponibiliteHbe ??
              DISPONIBILITE_PEI.INDISPONIBLE.toUpperCase(),
            penaEquipeHbe: values.penaEquipeHbe,
          },
    ),
  );

  return formData;
};

type SelectDataType = {
  listAutoriteDeci: IdCodeLibelleType[];
  listServicePublicDeci: IdCodeLibelleType[];
  listMaintenanceDeci: IdCodeLibelleType[];
  listGestionnaire: IdCodeLibelleType[];
  listCommune: IdCodeLibelleType[];
  listLieuDit: (IdCodeLibelleType & { communeId: string })[];
  listVoie: (IdCodeLibelleType & { communeId: string })[];
  listSite: (IdCodeLibelleType & { gestionnaireId: string })[];
  listModele: (IdCodeLibelleType & { marqueId: string })[];
  listServiceEau: IdCodeLibelleType[];
  listPeiJumelage: IdCodeLibelleType[];
  listDiametreWithNature: IdCodeLibelleType & { natureId: string }[];
};

const Pei = ({ isNew = false }: { isNew?: boolean }) => {
  // On récupère l'utilisateur pour prendre en compte les droits
  const { user, srid }: { user: UtilisateurEntity; srid: string } =
    useAppContext();

  const {
    values,
    setValues,
    setFieldValue,
  }: {
    values: PeiEntity & {
      typeSystemeSrid: { srid: number; nomSystem: string };
      coordonneeXToDisplay: string;
      coordonneeYToDisplay: string;
      voieSaisieLibre: boolean;
    };
  } = useFormikContext();
  const selectDataState = useGet(
    url`/api/pei/referentiel-for-upsert-pei?${{
      coordonneeX: values.coordonneeX,
      coordonneeY: values.coordonneeY,
      peiId: values.peiId,
    }}`,
  );

  // Si les coordonnées changent, il faut recharger les coordonnées dans les différents systèmes
  const geometrieState = useGet(
    url`/api/pei/get-geometrie-by-srid?${{
      coordonneeX: values.coordonneeXToDisplay,
      coordonneeY: values.coordonneeYToDisplay,
      srid: values.typeSystemeSrid,
    }}`,
  );

  const parametreVoieSaisieLibre = PARAMETRE.VOIE_SAISIE_LIBRE;

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(parametreVoieSaisieLibre),
    }}`,
  );

  const isSaisieVoieEnabled = useMemo<boolean>(() => {
    if (!listeParametre.isResolved) {
      return false;
    }
    // Le résultat est une String, on le parse pour récupérer le tableau
    return JSON.parse(
      listeParametre?.data[parametreVoieSaisieLibre].parametreValeur,
    );
  }, [listeParametre.isResolved]);

  useEffect(() => {
    if (
      values.coordonneeXToDisplay != null &&
      values.coordonneeYToDisplay != null
    ) {
      geometrieState?.run({
        coordonneeX: values.coordonneeXToDisplay,
        coordonneeY: values.coordonneeYToDisplay,
        srid: values.typeSystemeSrid,
      });
    }

    if (values.coordonneeX != null && values.coordonneeY != null) {
      selectDataState?.run({
        coordonneeX: values.coordonneeX,
        coordonneeY: values.coordonneeY,
        peiId: values.peiId,
      });
    }

    const coordonnees = geometrieState?.data?.find((e) => e.srid === srid);

    if (coordonnees != null) {
      setFieldValue("coordonneeX", coordonnees?.coordonneeX);
      setFieldValue("coordonneeY", coordonnees?.coordonneeY);
    }
  }, [
    setFieldValue,
    values.coordonneeX,
    values.coordonneeY,
    values.coordonneeXToDisplay,
    values.coordonneeYToDisplay,
    values.typeSystemeSrid,
    values.peiId,
    geometrieState,
    selectDataState,
    srid,
  ]);

  // Permet de savoir si les sections de l'accordion sont ouvertes et de les set
  const { handleShowClose, activesKeys, show } = useAccordionState([
    true,
    false,
    false,
    false,
  ]);

  // Correspond à la liste des champs obligatoires avec leur index dans l'accordion
  // Cela nous permettra d'ouvrir une section si un champ obligatoire n'est pas saisi
  const listValuesRequired: ValuesRequired[] = [
    {
      name: "peiAutoriteDeciId",
      accordionIndex: 0,
    },
    {
      name: "peiServicePublicDeciId",
      accordionIndex: 0,
    },
    {
      name: "peiNatureId",
      accordionIndex: 0,
    },
    {
      name: "peiNatureDeciId",
      accordionIndex: 0,
    },
    {
      name: "peiCommuneId",
      accordionIndex: 1,
    },
    {
      name: "peiVoieId",
      accordionIndex: 1,
    },
    {
      name: "peiDomaineId",
      accordionIndex: 1,
    },
  ];

  const { data: selectData }: { data: SelectDataType | undefined } =
    selectDataState;

  const hasDroitCaracteristique =
    isNew || hasDroit(user, TYPE_DROIT.PEI_CARACTERISTIQUES_U);
  return (
    selectData &&
    srid != null && (
      <FormContainer>
        <Container>
          <PageTitle
            icon={isNew ? <IconCreate /> : <IconEdit />}
            title={
              isNew
                ? "Création d'un PEI"
                : "Modification du " +
                  values.peiTypePei +
                  " n°" +
                  values.peiNumeroComplet
            }
          />
          <AccordionCustom
            activesKeys={activesKeys}
            handleShowClose={handleShowClose}
            list={[
              {
                header: "Informations générales",
                content: (
                  <FormEntetePei
                    values={values}
                    selectData={selectData}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                    isNew={isNew}
                    user={user}
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
                    setFieldValue={setFieldValue}
                    geometrieData={geometrieState?.data}
                    srid={parseInt(srid)}
                    isSaisieVoieEnabled={isSaisieVoieEnabled}
                    isNew={isNew}
                    user={user}
                  />
                ),
              },
              {
                header: "Caractéristiques techniques",
                content:
                  values.peiTypePei === TYPE_PEI.PIBI ? (
                    <FormPibi
                      values={values}
                      selectData={selectData}
                      setFieldValue={setFieldValue}
                      setValues={setValues}
                      hasDroitCaracteristique={hasDroitCaracteristique}
                    />
                  ) : values.peiTypePei === TYPE_PEI.PENA ? (
                    <FormPena
                      values={values}
                      setValues={setValues}
                      hasDroitCaracteristique={hasDroitCaracteristique}
                    />
                  ) : (
                    <div>Veuillez renseigner le type de PEI</div>
                  ),
              },
              {
                header: "Documents",
                content: (
                  <FormDocuments
                    documents={values.documents}
                    setFieldValue={setFieldValue}
                    defaultOtherProperties={{
                      isPhotoPei: false,
                    }}
                    otherFormParam={(index: number, listeElements: any[]) => (
                      <>
                        {
                          // Si c'est une image
                          [
                            "png",
                            "svg",
                            "jpeg",
                            "jpg",
                            "bmp",
                            "webp",
                            "gif",
                          ].includes(
                            listeElements[index].documentNomFichier
                              .split(".")
                              .at(-1),
                          ) && (
                            <CheckBoxInput
                              name={`documents[${index}].isPhotoPei`}
                              label="Est la photo du PEI ?"
                            />
                          )
                        }
                      </>
                    )}
                    disabled={!isNew && !hasDroit(user, TYPE_DROIT.PEI_U)}
                  />
                ),
              },
            ]}
          />

          <SubmitFormButtons
            returnLink={URLS.PEI}
            onClick={() => {
              const coordonnees = geometrieState.data?.find(
                (e) => e.srid === parseInt(srid),
              );
              setFieldValue("coordonneeX", coordonnees.coordonneeX);
              setFieldValue("coordonneeY", coordonnees.coordonneeY);
              setFieldValue("typeSystemeSrid", srid);
              checkValidity(values, show, listValuesRequired);
            }}
          />
        </Container>
      </FormContainer>
    )
  );
};

/**
 * Permet de check si le formulaire est conforme. S'il ne l'est pas on ouvre la section qui doit être modifiée
 * @param values : les values de formik
 * @param show : fonction d'ouverture d'une section
 * @param listValuesRequired : liste des valeurs requises avec l'index de l'accordion
 */
function checkValidity(
  values: any,
  show: (e: number) => void,
  listValuesRequired: ValuesRequired[],
) {
  listValuesRequired.map((e: ValuesRequired) => {
    if (
      validationSchema.fields[e.name] != null &&
      (values[e.name] === null || values[e.name] === "")
    ) {
      show(e.accordionIndex);
    }
  });
}

type ValuesRequired = {
  name: string;
  accordionIndex: number;
};

export default Pei;

const FormEntetePei = ({
  values,
  selectData,
  setValues,
  setFieldValue,
  isNew,
  user,
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
  setFieldValue: (champ: string, newValue: any | undefined) => void;
  isNew: boolean;
  user: UtilisateurEntity;
}) => {
  const { data: listNatureDeci }: { data: IdCodeLibelleType[] } = useGet(
    url`/api/nomenclatures/list/` + NOMENCLATURE.NATURE_DECI,
  );

  const codeNatureDeci =
    listNatureDeci &&
    listNatureDeci.find((e) => e.id === values.peiNatureDeciId)?.code;

  const idGestionnaire = values.peiSiteId
    ? selectData.listSite.find((e) => e.id === values.peiSiteId)?.gestionnaireId
    : null;

  const listTypePei = Object.values(TYPE_PEI).map((e) => {
    return { id: e.toString(), code: e.toString(), libelle: e.toString() };
  });

  const hasDroitUpdate = isNew || hasDroit(user, TYPE_DROIT.PEI_U);

  return (
    listNatureDeci && (
      <>
        <Row>
          <Col xs={12} sm={5} lg={2}>
            <PositiveNumberInput
              name="peiNumeroInterne"
              label="Numéro interne"
              required={false}
              disabled={
                !isNew && !hasDroit(user, TYPE_DROIT.PEI_NUMERO_INTERNE_U)
              }
            />
          </Col>
          <Col>
            <SelectForm
              name={"peiTypePei"}
              listIdCodeLibelle={listTypePei}
              label="Type de PEI"
              defaultValue={listTypePei?.find(
                (e) => e.code === values.peiTypePei,
              )}
              required={true}
              disabled={!isNew}
              setValues={setValues}
              setOtherValues={() => {
                setFieldValue("peiNatureId", null);
                setFieldValue("pibiDiametreId", null);
              }}
            />
          </Col>
          <Col>
            {values.peiTypePei && (
              <SelectNomenclaturesForm
                name={"peiNatureId"}
                nomenclature={
                  values.peiTypePei === TYPE_PEI.PIBI
                    ? NOMENCLATURE.NATURE_PIBI
                    : values.peiTypePei === TYPE_PEI.PENA
                      ? NOMENCLATURE.NATURE_PENA
                      : NOMENCLATURE.NATURE
                }
                label="Nature du PEI"
                valueId={values.peiNatureId}
                required={true}
                setValues={setValues}
                setOtherValues={() => {
                  setFieldValue("pibiDiametreId", null);
                }}
                disabled={!hasDroitUpdate}
              />
            )}
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
              disabled={!hasDroitUpdate}
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
              disabled={!hasDroitUpdate}
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
              disabled={!hasDroitUpdate}
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
              disabled={!hasDroitUpdate}
            />
          </Col>
          {(codeNatureDeci === TYPE_NATURE_DECI.PRIVE ||
            codeNatureDeci === TYPE_NATURE_DECI.CONVENTIONNE) && (
            <>
              <Col>
                <SelectForm
                  name={"peiGestionnaireId"}
                  listIdCodeLibelle={selectData.listGestionnaire}
                  label="Gestionnaire"
                  defaultValue={selectData.listGestionnaire.find((e) =>
                    idGestionnaire
                      ? e.id === idGestionnaire
                      : e.id === values.peiGestionnaireId,
                  )}
                  required={false}
                  setValues={setValues}
                  disabled={!hasDroitUpdate}
                />
              </Col>
              <Col>
                <SelectForm
                  name={"peiSiteId"}
                  listIdCodeLibelle={selectData.listSite.filter(
                    (e) =>
                      e.gestionnaireId === values.peiGestionnaireId ||
                      e.gestionnaireId === idGestionnaire,
                  )}
                  label="Site"
                  defaultValue={selectData.listSite.find(
                    (e) => e.id === values.peiSiteId,
                  )}
                  required={false}
                  setValues={setValues}
                  disabled={!hasDroitUpdate}
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
  setFieldValue,
  geometrieData,
  srid,
  isSaisieVoieEnabled,
  isNew,
  user,
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
  setFieldValue: (value: string, newValue: any) => void;
  geometrieData: { coordonneeX: string; coordonneeY: string; srid: number }[];
  srid: number;
  isSaisieVoieEnabled: boolean;
  isNew: boolean;
  user: UtilisateurEntity;
}) => {
  const hasDroitUpdate = isNew || hasDroit(user, TYPE_DROIT.PEI_U);

  return (
    <>
      <Row>
        <TitreSousSection>Coordonnées géographiques</TitreSousSection>
        <Col>
          <Form.Select
            name="typeSystemeSrid"
            onChange={(e) => {
              setValues((prevValues) => ({
                ...prevValues,
                typeSystemeSrid: e.target.value,
              }));
              const sridActif = e.target.value;
              const projetionValeur = geometrieData?.find(
                (e) => e.srid === parseInt(sridActif),
              );
              setFieldValue(
                "coordonneeXToDisplay",
                projetionValeur?.coordonneeX,
              );
              setFieldValue(
                "coordonneeYToDisplay",
                projetionValeur?.coordonneeY,
              );

              // Coordonnées en 2154 ou autres
              const coordonneesToSave = geometrieData?.find(
                (e) => e.srid === srid,
              );
              setFieldValue("coordonneeX", coordonneesToSave?.coordonneeX);
              setFieldValue("coordonneeY", coordonneesToSave?.coordonneeY);
            }}
            disabled={!isNew && !hasDroit(user, TYPE_DROIT.PEI_DEPLACEMENT_U)}
            className={"mt-3"}
          >
            {TypeSystemeSrid.map((e) => {
              if (e.actif || e.srid === srid) {
                return (
                  <option key={e.srid} value={e.srid}>
                    {" "}
                    {e.nomSystem}
                  </option>
                );
              }
            })}
          </Form.Select>
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <TextInput
            label="Coordonnée X"
            name="coordonneeXToDisplay"
            required={true}
            disabled={!isNew && !hasDroit(user, TYPE_DROIT.PEI_DEPLACEMENT_U)}
          />
        </Col>
        <Col>
          <TextInput
            label="Coordonnée Y"
            name="coordonneeYToDisplay"
            required={true}
            disabled={!isNew && !hasDroit(user, TYPE_DROIT.PEI_DEPLACEMENT_U)}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <TitreSousSection>Adresse</TitreSousSection>
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
            disabled={!hasDroitUpdate}
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
              disabled={!hasDroitUpdate}
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
            disabled={!hasDroitUpdate}
          />
        </Col>
        <Col>
          <TextInput
            name="peiSuffixeVoie"
            label="Suffixe"
            required={false}
            disabled={!hasDroitUpdate}
          />
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
            required={!values.voieSaisieLibre} // Requis si la saisie libre n'est pas activée ; si elle l'est, TODO XOR entre les 2 types
            setValues={setValues}
            disabled={
              !hasDroitUpdate ||
              (values.peiVoieTexte != null &&
                values.peiVoieTexte?.trim() !== "")
            }
          />
          {isSaisieVoieEnabled && (
            <>
              <CheckBoxInput
                name="voieSaisieLibre"
                label="Voie non trouvée ?"
                disabled={!hasDroitUpdate}
              />
              {values.voieSaisieLibre && (
                <TextInput
                  name="peiVoieTexte"
                  label="Voie (saisie libre)"
                  required={false} // TODO XOR entre voieId et voieText
                  disabled={
                    !hasDroitUpdate ||
                    (values.peiVoieId != null &&
                      values.peiVoieId?.trim() !== "")
                  }
                />
              )}
            </>
          )}
        </Col>
        <Col>
          <CheckBoxInput
            name="peiEnFace"
            label="Situé en face ?"
            disabled={!hasDroitUpdate}
          />
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <SelectNomenclaturesForm
            name={"peiNiveauId"}
            nomenclature={NOMENCLATURE.NIVEAU}
            label="Niveau"
            valueId={values.peiNiveauId}
            required={false}
            setValues={setValues}
            disabled={!hasDroitUpdate}
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
            disabled={!hasDroitUpdate}
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"peiDomaineId"}
            nomenclature={NOMENCLATURE.DOMAINE}
            label="Domaine"
            valueId={values.peiDomaineId}
            required={true}
            setValues={setValues}
            disabled={!hasDroitUpdate}
          />
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <TextAreaInput
            name="peiComplementAdresse"
            label="Complément d'adresse"
            required={false}
            disabled={!hasDroitUpdate}
          />
        </Col>
      </Row>
    </>
  );
};

const FormPibi = ({
  values,
  selectData,
  setValues,
  setFieldValue,
  hasDroitCaracteristique,
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
  setFieldValue: (champ: string, newValue: any | undefined) => void;
  hasDroitCaracteristique: boolean;
}) => {
  const idMarque = values.pibiModeleId
    ? selectData.listModele.find((e) => e.id === values.pibiModeleId)?.marqueId
    : values.pibiMarqueId;

  const idNature = values.peiNatureId
    ? selectData.listDiametreWithNature.find(
        (e) => e.id === values.pibiDiametreId,
      )?.natureId
    : values.peiNatureId;

  return (
    <>
      <Row>
        <TitreSousSection>Informations PEI</TitreSousSection>
        <Col>
          <SelectForm
            name={"pibiDiametreId"}
            listIdCodeLibelle={selectData.listDiametreWithNature.filter(
              (e) =>
                e.natureId === values.peiNatureId || e.natureId === idNature,
            )}
            label="Diamètre nominal"
            defaultValue={selectData.listDiametreWithNature.find(
              (e) => e.id === values.pibiDiametreId,
            )}
            required={false}
            disabled={!hasDroitCaracteristique}
            setValues={setValues}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiDispositifInviolabilite"
            label="Dispositif d'inviolabilité ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiRenversable"
            label="Renversable ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <TextInput
            name="pibiNumeroScp"
            label="Numéro SCP"
            required={false}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <SelectForm
            name={"pibiJumeleId"}
            listIdCodeLibelle={selectData.listPeiJumelage}
            label="Jumelé avec"
            defaultValue={selectData?.listPeiJumelage?.find(
              (e) => e.id === values.pibiJumeleId,
            )}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiMarqueId"}
            nomenclature={NOMENCLATURE.MARQUE_PIBI}
            label="Marque"
            valueId={idMarque}
            required={false}
            setValues={setValues}
            setOtherValues={() => {
              setFieldValue("pibiModeleId", null);
            }}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <SelectForm
            name={"pibiModeleId"}
            listIdCodeLibelle={selectData.listModele.filter(
              (e) =>
                e.marqueId === values.pibiMarqueId || e.marqueId === idMarque,
            )}
            label="Modèle"
            defaultValue={selectData.listModele.find(
              (e) => e.id === values.pibiModeleId,
            )}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <PositiveNumberInput
            name="peiAnneeFabrication"
            label="Année de fabrication"
            required={false}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <TitreSousSection>Réseau</TitreSousSection>
        <Col>
          <SelectForm
            name={"pibiServiceEauId"}
            listIdCodeLibelle={selectData.listServiceEau}
            label="Service des eaux"
            defaultValue={selectData.listServiceEau.find(
              (e) => e.id === values.pibiServiceEauId,
            )}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"pibiTypeReseauId"}
            nomenclature={NOMENCLATURE.TYPE_RESEAU}
            label="Type de réseau"
            valueId={values.pibiTypeReseauId}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiTypeCanalisationId"}
            nomenclature={NOMENCLATURE.TYPE_CANALISATION}
            label="Type de canalisation"
            valueId={values.pibiTypeCanalisationId}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <NumberInput
            name="pibiDiametreCanalisation"
            label="Diamètre de canalisation"
            required={false}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <CheckBoxInput name="pibiDebitRenforce" label="Débit renforcé ?" />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiReservoirId"}
            nomenclature={NOMENCLATURE.RESERVOIR}
            label="Réservoir"
            valueId={values.pibiReservoirId}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiSurpresse"
            label="Réseau surpressé ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiAdditive"
            label="Réseau additivé ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
    </>
  );
};

const FormPena = ({
  values,
  setValues,
  hasDroitCaracteristique,
}: {
  values: PeiEntity;
  setValues: (e: any) => void;
  hasDroitCaracteristique: boolean;
}) => {
  return (
    <>
      <Row>
        <TitreSousSection>Ressource</TitreSousSection>
        <Col>
          <CheckBoxInput
            name="penaCapaciteIllimitee"
            label="Capacité illimitée ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="penaCapaciteIncertaine"
            label="Capacité incertaine ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="penaEquipeHbe"
            label="Équipé HBE ?"
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <PositiveNumberInput
            name="penaCapacite"
            label="Capacité en m³"
            required={false}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <PositiveNumberInput
            name="penaQuantiteAppoint"
            label="Quantité d'appoint en m³/h"
            required={false}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"penaMateriauId"}
            nomenclature={NOMENCLATURE.MATERIAU}
            label="Matériau de la citerne"
            valueId={values.penaMateriauId}
            required={false}
            setValues={setValues}
            disabled={!hasDroitCaracteristique}
          />
        </Col>
      </Row>
    </>
  );
};

const TitreSousSection = ({ children }: { children: ReactNode }) => {
  return <p className={"h5 mt-3"}>{children}</p>;
};
