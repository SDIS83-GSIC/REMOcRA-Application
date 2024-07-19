import { useFormikContext } from "formik";
import { useEffect } from "react";
import { Button, Form } from "react-bootstrap";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import { object } from "yup";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FormContainer,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SelectNomenclaturesForm from "../../components/Form/SelectNomenclaturesForm.tsx";
import { IconCreate, IconEdit } from "../../components/Icon/Icon.tsx";
import TYPE_DATA_CACHE from "../../enums/NomenclaturesEnum.tsx";
import TYPE_NATURE_DECI from "../../enums/TypeNatureDeci.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";
import TypeSystemeSrid from "../../enums/TypeSystemeSrid.tsx";
import url from "../../module/fetch.tsx";
import { requiredNumber, requiredString } from "../../module/validators.tsx";
import ensureDataCache, { ensureSrid } from "../../utils/ensureData.tsx";
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

  peiAutoriteDeciId: data?.peiAutoriteDeciId ?? null,
  peiServicePublicDeciId: data?.peiServicePublicDeciId ?? null,
  peiMaintenanceDeciId: data?.peiMaintenanceDeciId ?? null,

  peiCommuneId: data?.peiCommuneId ?? null,
  peiVoieId: data?.peiVoieId ?? null,
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

  // DONNEES PENA
  penaCapacite: data?.penaCapacite ?? null,
  penaCapaciteIllimitee: data?.penaCapaciteIllimitee ?? null,
  penaCapaciteIncertaine: data?.penaCapaciteIncertaine ?? null,
  penaMateriauId: data?.penaMateriauId ?? null,
  penaQuantiteAppoint: data?.penaQuantiteAppoint ?? null,
  penaDisponibiliteHbe: data?.penaDisponibiliteHbe ?? null,
});

export const validationSchema = object({
  peiAutoriteDeciId: requiredString,
  peiServicePublicDeciId: requiredString,
  peiNatureId: requiredString,
  peiNatureDeciId: requiredString,

  peiCommuneId: requiredString,
  peiVoieId: requiredString,
  peiDomaineId: requiredString,
  coordonneeX: requiredNumber,
  coordonneeY: requiredNumber,
});

export const prepareVariables = (values: PeiEntity, data?: PeiEntity) => ({
  peiId: data?.peiId ?? null,
  peiNumeroComplet: values.peiNumeroComplet ?? null,
  peiNumeroInterne: values.peiNumeroInterne ?? null,
  peiTypePei: values.peiTypePei,
  peiDisponibiliteTerrestre: data?.peiDisponibiliteTerrestre ?? null,
  peiAnneeFabrication: values?.peiAnneeFabrication ?? null,

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

  coordonneeX: values?.coordonneeX ?? null,
  coordonneeY: values?.coordonneeY ?? null,

  // DONNEES PIBI
  pibiDiametreId: values.pibiDiametreId ?? null,
  pibiServiceEauId: values.pibiServiceEauId ?? null,
  pibiNumeroScp: values.pibiNumeroScp ?? null,
  pibiRenversable: values.pibiRenversable ?? null,
  pibiDispositifInviolabilite: values.pibiDispositifInviolabilite ?? null,
  pibiModeleId: values.pibiModeleId ?? null,
  pibiMarqueId: values.pibiMarqueId ?? null,
  pibiReservoirId: values.pibiReservoirId ?? null,
  pibiDebitRenforce: values.pibiDebitRenforce ?? null,
  pibiTypeCanalisationId: values.pibiTypeCanalisationId ?? null,
  pibiTypeReseauId: values.pibiTypeReseauId ?? null,
  pibiDiametreCanalisation: values.pibiDiametreCanalisation ?? null,
  pibiSurpresse: values.pibiSurpresse ?? null,
  pibiAdditive: values.pibiAdditive ?? null,

  // DONNEES PENA
  penaCapacite: values.penaCapacite ?? null,
  penaCapaciteIllimitee: values.penaCapaciteIllimitee ?? null,
  penaCapaciteIncertaine: values.penaCapaciteIncertaine ?? null,
  penaMateriauId: values.penaMateriauId ?? null,
  penaQuantiteAppoint: values.penaQuantiteAppoint ?? null,
  penaDisponibiliteHbe: data?.penaDisponibiliteHbe ?? null,

  // DONNEES INITIALES
  peiNumeroInterneInitial: data?.peiNumeroInterne ?? null,
  peiCommuneIdInitial: data?.peiCommuneId ?? null,
  peiZoneSpecialeIdInitial: data?.peiZoneSpecialeId ?? null,
  peiNatureDeciIdInitial: data?.peiNatureDeciId ?? null,
  peiDomaineIdInitial: data?.peiDomaineId ?? null,
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
  listModele: (IdCodeLibelleType & { marqueId: string })[];
  listServiceEau: IdCodeLibelleType[];
};

const Pei = ({ isNew = false }: { isNew?: boolean }) => {
  const {
    values,
    setValues,
    setFieldValue,
  }: {
    values: PeiEntity & {
      typeSystemeSrid: { srid: number; nomSystem: string };
    };
  } = useFormikContext();
  const selectDataState = useGet(url`/api/pei/referentiel-for-update-pei`);

  //eslint-disable-next-line react-hooks/rules-of-hooks
  const srid = ensureSrid();

  // Si les coordonnées changent, il faut recharger les coordonnées dans les différents systèmes
  const geometrieState = useGet(
    !isNew
      ? url`/api/pei/get-geometrie-by-srid?${{
          coordonneeX: values.coordonneeX,
          coordonneeY: values.coordonneeY,
          srid: values.typeSystemeSrid,
        }}`
      : "",
  );

  useEffect(() => {
    geometrieState?.run({
      coordonneeX: values.coordonneeX,
      coordonneeY: values.coordonneeY,
      srid: values.typeSystemeSrid,
    });
  }, [
    values.coordonneeX,
    values.coordonneeY,
    values.typeSystemeSrid,
    geometrieState,
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

  return (
    selectData &&
    srid && (
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
                    />
                  ) : values.peiTypePei === TYPE_PEI.PENA ? (
                    <FormPena values={values} setValues={setValues} />
                  ) : (
                    <div>Veuillez renseigner le type de PEI</div>
                  ),
              },
              {
                header: "Documents",
                content: <>TODO</>,
              },
            ]}
          />

          <Button
            type="submit"
            variant="primary"
            onClick={() => {
              const coordonnees = geometrieState.data?.find(
                (e) => e.srid === parseInt(srid),
              );
              setFieldValue("coordonneeX", coordonnees.coordonneeX);
              setFieldValue("coordonneeY", coordonnees.coordonneeY);
              setFieldValue("typeSystemeSrid", srid);
              checkValidity(values, show, listValuesRequired);
            }}
          >
            Valider
          </Button>
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
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
  setFieldValue: (champ: string, newValue: any | undefined) => void;
  isNew: boolean;
}) => {
  const listNatureDeci: IdCodeLibelleType[] = ensureDataCache(
    TYPE_DATA_CACHE.NATURE_DECI,
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

  return (
    listNatureDeci && (
      <>
        <Row>
          <Col xs={12} sm={5} lg={2}>
            <PositiveNumberInput
              name="peiNumeroInterne"
              label="Numéro interne"
              required={false}
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
              }}
            />
          </Col>
          <Col>
            {values.peiTypePei && (
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
                  defaultValue={selectData.listGestionnaire.find((e) =>
                    idGestionnaire
                      ? e.id === idGestionnaire
                      : e.id === values.peiGestionnaireId,
                  )}
                  required={false}
                  setValues={setValues}
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
}: {
  values: PeiEntity;
  selectData: SelectDataType;
  setValues: (e: any) => void;
  setFieldValue: (value: string, newValue: any) => void;
  geometrieData: { coordonneeX: string; coordonneeY: string; srid: number }[];
  srid: number;
}) => {
  return (
    <>
      <h2>Coordonnées géographique</h2>
      <Row className="mt-3">
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
              setFieldValue("coordonneeX", projetionValeur?.coordonneeX);
              setFieldValue("coordonneeY", projetionValeur?.coordonneeY);
            }}
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
          <TextInput label="Coordonnée X" name="coordonneeX" required={true} />
        </Col>
        <Col>
          <TextInput label="Coordonnée Y" name="coordonneeY" required={true} />
        </Col>
      </Row>
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

const FormPibi = ({
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
  const idMarque = values.pibiModeleId
    ? selectData.listModele.find((e) => e.id === values.pibiModeleId)?.marqueId
    : values.pibiMarqueId;

  return (
    <>
      <h2>Informations PEI</h2>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiDiametreId"}
            nomenclature={TYPE_DATA_CACHE.DIAMETRE}
            label="Diamètre nominal"
            valueId={values.pibiDiametreId}
            required={false}
            setValues={setValues}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiDispositifInviolabilite"
            label="Dispositif d'inviolabilité ?"
            defaultCheck={values.pibiDispositifInviolabilite}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiRenversable"
            label="Renversable ?"
            defaultCheck={values.pibiRenversable}
          />
        </Col>
      </Row>
      <Row className="mt-3 d-flex align-items-center">
        <Col>
          <TextInput name="pibiNumeroScp" label="Numéro SCP" required={false} />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiMarqueId"}
            nomenclature={TYPE_DATA_CACHE.MARQUE_PIBI}
            label="Marque"
            valueId={idMarque}
            required={false}
            setValues={setValues}
            setOtherValues={() => {
              setFieldValue("pibiModeleId", null);
            }}
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
          />
        </Col>
        <Col>
          <PositiveNumberInput
            name="peiAnneeFabrication"
            label="Année de fabrication"
            required={false}
          />
        </Col>
      </Row>
      <h2>Réseau</h2>
      <Row className="mt-3">
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
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"pibiTypeReseauId"}
            nomenclature={TYPE_DATA_CACHE.TYPE_RESEAU}
            label="Type de réseau"
            valueId={values.pibiTypeReseauId}
            required={false}
            setValues={setValues}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiTypeCanalisationId"}
            nomenclature={TYPE_DATA_CACHE.TYPE_CANALISATION}
            label="Type de canalisation"
            valueId={values.pibiTypeCanalisationId}
            required={false}
            setValues={setValues}
          />
        </Col>
        <Col>
          <NumberInput
            name="pibiDiametreCanalisation"
            label="Diamètre de canalisation"
            required={false}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiDebitRenforce"
            label="Débit renforcé ?"
            defaultCheck={values.pibiDebitRenforce}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <SelectNomenclaturesForm
            name={"pibiReservoirId"}
            nomenclature={TYPE_DATA_CACHE.RESERVOIR}
            label="Réservoir"
            valueId={values.pibiReservoirId}
            required={false}
            setValues={setValues}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiSurpresse"
            label="Réseau surpressé ?"
            defaultCheck={values.pibiSurpresse}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="pibiAdditive"
            label="Réseau additivé ?"
            defaultCheck={values.pibiAdditive}
          />
        </Col>
      </Row>
    </>
  );
};

const FormPena = ({
  values,
  setValues,
}: {
  values: PeiEntity;
  setValues: (e: any) => void;
}) => {
  return (
    <>
      <h2>Ressource</h2>
      <Row className="mt-3">
        <Col>
          <CheckBoxInput
            name="penaCapaciteIllimitee"
            label="Capacité illimitée ?"
            defaultCheck={values.penaCapaciteIllimitee}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name="penaCapaciteIncertaine"
            label="Capacité incertaine ?"
            defaultCheck={values.penaCapaciteIncertaine}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <PositiveNumberInput
            name="penaCapacite"
            label="Capacité en m³"
            required={false}
          />
        </Col>
        <Col>
          <PositiveNumberInput
            name="penaQuantiteAppoint"
            label="Quantité d'appoint en m³/h"
            required={false}
          />
        </Col>
        <Col>
          <SelectNomenclaturesForm
            name={"penaMateriauId"}
            nomenclature={TYPE_DATA_CACHE.MATERIAU}
            label="Matériau de la citerne"
            valueId={values.penaMateriauId}
            required={false}
            setValues={setValues}
          />
        </Col>
      </Row>
    </>
  );
};
