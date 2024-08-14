import React, { ReactNode } from "react";
import { useFormikContext } from "formik";
import { object } from "yup";
import { Badge, Row, Col, Container } from "react-bootstrap";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import url from "../../../module/fetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FormContainer,
  TextInput,
} from "../../../components/Form/Form.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { IconQuestionMark } from "../../../components/Icon/Icon.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { URLS } from "../../../routes.tsx";
import TYPE_PARAMETRE from "../../../enums/TypesParametres.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

type ParametresSectionGeneral = {
  mentionCnil: string;
  messageEntete: string;
  titrePage: string;
  toleranceVoiesMetres: number;
};

type ParametresSectionMobile = {
  affichageIndispo: boolean;
  affichageSymbolesNormalises: boolean;
  caracteristiquesPena: string[];
  caracteristiquesPibi: string[];
  dureeValiditeToken: number;
  gestionAgent: string;
};

type ParametresSectionCartographie = {
  coordonneesFormatAffichage: string;
};

type ParametresSectionCouvertureHydraulique = {
  deciDistanceMaxParcours: number;
  deciIsodistances: string;
  profondeurCouverture: number;
};

type ParametresSectionPermis = {
  permisToleranceChargementMetres: number;
};

type ParametresSectionPei = {
  peiColonnes: string[];
  bufferCarte: number;
};

type AdminParametresValue = {
  general: ParametresSectionGeneral;
  mobile: ParametresSectionMobile;
  cartographie: ParametresSectionCartographie;
  couvertureHydraulique: ParametresSectionCouvertureHydraulique;
  permis: ParametresSectionPermis;
  pei: ParametresSectionPei;
};

export const getInitialValues = (
  data: AdminParametresValue,
): {
  general: ParametresSectionGeneral;
  mobile: ParametresSectionMobile;
  cartographie: ParametresSectionCartographie;
  couvertureHydraulique: ParametresSectionCouvertureHydraulique;
  permis: ParametresSectionPermis;
  pei: ParametresSectionPei;
} => ({
  general: data?.general,
  mobile: data?.mobile,
  cartographie: data?.cartographie,
  couvertureHydraulique: data?.couvertureHydraulique,
  permis: data?.permis,
  pei: data?.pei,
});

export const validationSchema = object({});

export const prepareVariables = (values: AdminParametresValue) => ({
  general: values.general,
  mobile: {
    affichageIndispo: values.mobile.affichageIndispo,
    affichageSymbolesNormalises: values.mobile.affichageSymbolesNormalises,
    caracteristiquesPena: values.mobile.caracteristiquesPena ?? [],
    caracteristiquesPibi: values.mobile.caracteristiquesPena ?? [],
    dureeValiditeToken: values.mobile.dureeValiditeToken,
    gestionAgent: values.mobile.gestionAgent,
  },
  cartographie: values.cartographie,
  couvertureHydraulique: values.couvertureHydraulique,
  permis: values.permis,
  pei: values.pei,
});

const AdminParametres = () => {
  const adminParametresState = useGet(url`/api/admin/parametres`);
  const { data } = adminParametresState;

  return (
    <MyFormik
      initialValues={getInitialValues(data)}
      validationSchema={validationSchema}
      isPost={true}
      submitUrl={`/api/admin/parametres`}
      prepareVariables={(values) => prepareVariables(values)}
    >
      <AdminParametresInterne />
    </MyFormik>
  );
};

export const AdminParametresInterne = () => {
  const { values, setValues, setFieldValue }: { values: AdminParametresValue } =
    useFormikContext();
  const { activesKeys, handleShowClose } = useAccordionState([
    false,
    false,
    false,
    false,
    false,
    false,
  ]);

  return (
    values && (
      <FormContainer>
        <Container>
          <h1>Paramètres de l&apos;application</h1>

          <AccordionCustom
            activesKeys={activesKeys}
            list={[
              {
                header: "Général",
                content: (
                  <AdminGeneral
                    values={values.general}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
              {
                header: "Application mobile",
                content: (
                  <AdminApplicationMobile
                    values={values.mobile}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
              {
                header: "Cartographie",
                content: (
                  <AdminCartographie
                    values={values.cartographie}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
              {
                header: "Couverture hydraulique",
                content: (
                  <AdminCouvertureHydraulique
                    values={values.couvertureHydraulique}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
              {
                header: "Permis",
                content: (
                  <AdminPermis
                    values={values.permis}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
              {
                header: "PEI",
                content: (
                  <AdminPei
                    values={values.pei}
                    setValues={setValues}
                    setFieldValue={setFieldValue}
                  />
                ),
              },
            ]}
            handleShowClose={handleShowClose}
          />

          <br />
          <SubmitFormButtons returnLink={URLS.MODULE_ADMIN} />
        </Container>
      </FormContainer>
    )
  );
};

export const AdminParametre = ({
  explication,
  type = TYPE_PARAMETRE.STRING,
  children,
}: AdminParametreTypes) => {
  let bg = "primary";
  let text = "light";
  switch (type) {
    case TYPE_PARAMETRE.INTEGER:
      bg = "success";
      text = "dark";
      break;
    case TYPE_PARAMETRE.BOOLEAN:
      bg = "info";
      text = "light";
      break;
    case TYPE_PARAMETRE.BINARY:
      bg = "dark";
      text = "light";
      break;
    case TYPE_PARAMETRE.DOUBLE:
      bg = "warning";
      text = "dark";
      break;
    case TYPE_PARAMETRE.GEOMETRY:
      bg = "danger";
      text = "light";
      break;
    case TYPE_PARAMETRE.MULTI_STRING:
    case TYPE_PARAMETRE.MULTI_INT:
      bg = "secondary";
      text = "dark";
      break;
    case TYPE_PARAMETRE.SELECT:
      bg = "light";
      text = "dark";
      break;
  }
  return (
    <Row>
      <Col xs={12} lg={6} xl={3}>
        {children}
      </Col>
      <Col xs={"auto"}>
        <Badge bg={bg} text={text} pill>
          {type}
        </Badge>
      </Col>
      {explication && (
        <Col xs={"auto"}>
          <TooltipCustom tooltipId={"superAdmin"} tooltipText={explication}>
            <IconQuestionMark />
          </TooltipCustom>
        </Col>
      )}
    </Row>
  );
};

type AdminParametreTypes = {
  explication?: ReactNode;
  type: ReactNode;
  children: ReactNode;
};

export default AdminParametres;

const AdminGeneral = ({ values }: { values: ParametresSectionGeneral }) => {
  return (
    values && (
      <>
        <AdminParametre
          explication={
            <span>Mention CNIL affichée en bas de page (peut être vide)</span>
          }
          type={TYPE_PARAMETRE.STRING}
        >
          <TextInput name="general.mentionCnil" label="Mention CNIL" />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>Message affiché dans le cartouche d&apos;entête</span>
          }
          type={TYPE_PARAMETRE.STRING}
        >
          <TextInput name="general.messageEntete" label="Message d'entête" />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>Titre affiché dans l&apos;entếte du navigateur</span>
          }
          type={TYPE_PARAMETRE.STRING}
        >
          <TextInput name="general.titrePage" label="Titre de la page" />
        </AdminParametre>
        <AdminParametre explication="" type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="general.toleranceVoiesMetres"
            label="Tolérance (en m) de chargement des voies"
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminApplicationMobile = ({
  values,
}: {
  values: ParametresSectionMobile;
}) => {
  return (
    values && (
      <>
        <AdminParametre
          explication={
            <span>
              Ajout d&apos;une croix rouge sur le symbole du PEI pour signifier
              l&apos;indisponibilité
            </span>
          }
          type={TYPE_PARAMETRE.BOOLEAN}
        >
          <CheckBoxInput
            name="mobile.affichageIndispo"
            label="Afficher l'état de disponibilité du PEI"
            defaultCheck={values?.affichageIndispo}
          />
        </AdminParametre>
        <AdminParametre explication="" type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="mobile.affichageSymbolesNormalises"
            label="Utiliser la symbologie du RNDECI ?"
            defaultCheck={values?.affichageSymbolesNormalises}
          />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>
              Dans l&apos;infobulle d&apos;un PEI sur l&apos;application mobile,
              on affiche des caractéristiques. Ce paramètre permet de définir
              lesquels, et dans quel ordre ils se présentent
            </span>
          }
          type={TYPE_PARAMETRE.MULTI_STRING}
        >
          <TextInput
            name="mobile.caracteristiquesPena"
            label="Caractéristiques à afficher pour les PENA"
          />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>
              Dans l&apos;infobulle d&apos;un PEI sur l&apos;application mobile,
              on affiche des caractéristiques. Ce paramètre permet de définir
              lesquels, et dans quel ordre ils se présentent
            </span>
          }
          type={TYPE_PARAMETRE.MULTI_STRING}
        >
          <TextInput
            name="mobile.caracteristiquesPibi"
            label="Caractéristiques à afficher pour les PIBI"
          />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>
              Durée (en heures) de validité du jeton de connexion à
              l&apos;application mobile.
            </span>
          }
          type={TYPE_PARAMETRE.INTEGER}
        >
          <PositiveNumberInput
            name="mobile.dureeValiditeToken"
            label="Durée de validité du token"
          />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>
              Ce paramètre permet de définir le comportement des composants
              &quot;Agent 1&quot; et &quot;Agent 2&quot; présents sur le premier
              onglet de la visite d&apos;un point d&apos;eau sur
              l&apos;application mobile. Ces deux derniers s&apos;appuient sur
              ce qu&apos;on appelera le &quot;Composant agent&quot; qui sera
              utile ou non selon les cas, mais s&apos;il est utilisé, il
              fonctionnera comme suit : TODO
            </span>
          }
          type={TYPE_PARAMETRE.SELECT}
        >
          <TextInput
            name="mobile.gestionAgent"
            label="Type d'agent sélectionné"
          />
        </AdminParametre>
        <AdminParametre
          explication={
            <span>
              {" "}
              Mot de passe pour l&apos;administration de l&apos;appli mobile{" "}
            </span>
          }
          type={TYPE_PARAMETRE.STRING}
        >
          <TextInput
            name="mobile.mdpAdministrateur"
            label="Mot de passe administrateur"
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminCartographie = ({
  values,
}: {
  values: ParametresSectionCartographie;
}) => {
  return (
    values && (
      <>
        <AdminParametre explication="" type={TYPE_PARAMETRE.STRING}>
          <TextInput
            name="cartographie.coordonneesFormatAffichage"
            label="Format d'affichage des coordonnées"
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminCouvertureHydraulique = ({
  values,
}: {
  values: ParametresSectionCouvertureHydraulique;
}) => {
  return (
    values && (
      <>
        <AdminParametre explication="" type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="couvertureHydraulique.deciDistanceMaxParcours"
            label="Distance max de parcours (en m)"
          />
        </AdminParametre>
        <AdminParametre explication="" type={TYPE_PARAMETRE.MULTI_INT}>
          <TextInput
            name="couvertureHydraulique.deciIsodistances"
            label="Isodistances à prendre en compte (en m)"
          />
        </AdminParametre>
        <AdminParametre explication="" type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="couvertureHydraulique.profondeurCouverture"
            label="Profondeur de couverture (en m)"
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminPei = ({ values }: { values: ParametresSectionPei }) => {
  return (
    values && (
      <>
        <AdminParametre explication="" type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.bufferCarte"
            label="Espace tampon pour la génération de carte (carte des tournées)"
          />
        </AdminParametre>
        <AdminParametre explication="" type={TYPE_PARAMETRE.MULTI_STRING}>
          <TextInput
            name="pei.peiColonnes"
            label="Colonnes affichées dans la fiche PEI"
          />
        </AdminParametre>
      </>
    )
  );
};
const AdminPermis = ({ values }: { values: ParametresSectionPermis }) => {
  return (
    values && (
      <>
        <AdminParametre explication="" type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="permis.permisToleranceChargementMetres"
            label="Tolérance de chargement (en m)"
          />
        </AdminParametre>
      </>
    )
  );
};
