import { useFormikContext } from "formik";
import { ReactNode, useMemo } from "react";
import { Badge, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import SeeMoreButton from "../../../components/Button/SeeMoreButton.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FormContainer,
  Multiselect,
  SelectInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import TransferList, {
  useTransferList,
} from "../../../components/Form/TransferList.tsx";
import { IconParametre } from "../../../components/Icon/Icon.tsx";
import typeAgent from "../../../Entities/TypeAgentEntity.tsx";
import COLUMN_PEI from "../../../enums/ColumnPeiEnum.tsx";
import TYPE_PARAMETRE from "../../../enums/TypesParametres.tsx";
import url from "../../../module/fetch.tsx";
import { hasDroit } from "../../../droits.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import typeAffichageCoordonnees from "../../../enums/TypeAffichageCoordonnees.tsx";

type ParametresSectionGeneral = {
  mentionCnil: string;
  messageEntete: string;
  titrePage: string;
  toleranceVoiesMetres: number;
  accueilPublic: string | undefined;
};

type ParametresSectionMobile = {
  affichageIndispo: boolean;
  modeDeconnecte: boolean;
  creationPeiMobile: boolean;
  affichageSymbolesNormalises: boolean;
  caracteristiquesPena: string[];
  caracteristiquesPibi: string[];
  dureeValiditeToken: number;
  gestionAgent: string;
  bridagePhoto: boolean;
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
  peiColonnes: string[] | undefined;
  peiNombreHistorique: number | undefined;
  bufferCarte: number;
  caracteristiquesPenaTooltipWeb: string[] | undefined;
  caracteristiquesPibiTooltipWeb: string[] | undefined;
  peiFicheResumeStandalone: boolean;
  peiDisplayTypeEngin: boolean;
};

type ParametresSectionPeiLongueIndispo = {
  peiLongueIndisponibiliteMessage: string;
  peiLongueIndisponibiliteJours: number;
  peiLongueIndisponibiliteTypeOrganisme: [];
};

type AdminParametresValue = {
  general: ParametresSectionGeneral;
  mobile: ParametresSectionMobile;
  cartographie: ParametresSectionCartographie;
  couvertureHydraulique: ParametresSectionCouvertureHydraulique;
  permis: ParametresSectionPermis;
  pei: ParametresSectionPei;
  peiLongueIndispo: ParametresSectionPeiLongueIndispo;
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
  mobile: {
    ...data?.mobile,
    caracteristiquesPibi:
      data?.mobile?.caracteristiquesPibi?.map((e) => ({
        id: e,
      })) ?? [],
    caracteristiquesPena:
      data?.mobile?.caracteristiquesPena?.map((e) => ({
        id: e,
      })) ?? [],
  },
  cartographie: data?.cartographie,
  couvertureHydraulique: data?.couvertureHydraulique,
  permis: data?.permis,
  pei: {
    ...data?.pei,
    peiColonnes: data?.pei?.peiColonnes?.map((e) => ({ id: e, libelle: e })),
    caracteristiquesPibiTooltipWeb:
      data?.pei?.caracteristiquesPibiTooltipWeb?.map((e) => ({
        id: e,
      })) ?? [],
    caracteristiquesPenaTooltipWeb:
      data?.pei?.caracteristiquesPenaTooltipWeb?.map((e) => ({
        id: e,
      })) ?? [],
  },
  peiLongueIndispo: data?.peiLongueIndispo,
});

export const validationSchema = object({});

export const prepareVariables = (values: AdminParametresValue) => {
  return {
    general: values?.general,
    mobile: {
      ...values?.mobile,
      caracteristiquesPena:
        values?.mobile?.caracteristiquesPena?.map((e) => e.id) ?? [],
      caracteristiquesPibi:
        values?.mobile?.caracteristiquesPibi?.map((e) => e.id) ?? [],
    },
    cartographie: values?.cartographie,
    couvertureHydraulique: {
      ...values?.couvertureHydraulique,
      deciIsodistances:
        values?.couvertureHydraulique?.deciIsodistances?.toString(),
    },
    permis: values?.permis,
    pei: {
      ...values?.pei,
      peiColonnes: values?.pei?.peiColonnes?.map((e) => e.id) ?? [],
      caracteristiquesPenaTooltipWeb:
        values?.pei?.caracteristiquesPenaTooltipWeb?.map((e) => e.id) ?? [],
      caracteristiquesPibiTooltipWeb:
        values?.pei?.caracteristiquesPibiTooltipWeb?.map((e) => e.id) ?? [],
    },
    peiLongueIndispo: values?.peiLongueIndispo,
  };
};

const AdminParametres = () => {
  const adminParametresState = useGet(url`/api/admin/parametres`);
  const { data } = adminParametresState;

  return (
    <MyFormik
      initialValues={getInitialValues(data)}
      validationSchema={validationSchema}
      isPost={false}
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
  const allCaracteristiques = useGet(url`/api/admin/pei-caracteristique`)?.data;
  const { activesKeys, handleShowClose } = useAccordionState(
    Array(6).fill(false),
  );
  const { user } = useAppContext();

  return (
    values && (
      <FormContainer>
        <Container>
          <PageTitle title="Paramètres applicatifs" icon={<IconParametre />} />

          <AccordionCustom
            activesKeys={activesKeys}
            list={[
              ...(hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI)
                ? [
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
                          allCaracteristiques={allCaracteristiques}
                        />
                      ),
                    },
                    {
                      header: "PEI longue indisponibilité",
                      content: (
                        <AdminPeiLongueIndispo
                          values={values.peiLongueIndispo}
                          setFieldValue={setFieldValue}
                        />
                      ),
                    },
                  ]
                : []),
              ...(hasDroit(user, TYPE_DROIT.ADMIN_PARAM_APPLI_MOBILE)
                ? [
                    {
                      header: "Application mobile",
                      content: (
                        <AdminApplicationMobile
                          values={values.mobile}
                          setValues={setValues}
                          setFieldValue={setFieldValue}
                          allCaracteristiques={allCaracteristiques}
                        />
                      ),
                    },
                  ]
                : []),
            ]}
            handleShowClose={handleShowClose}
          />

          <br />
          <SubmitFormButtons returnLink={true} />
        </Container>
      </FormContainer>
    )
  );
};

export const AdminParametre = ({
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
    <>
      <Row>
        <Col xs={2} className="text-end">
          <Badge bg={bg} text={text} pill className={"mt-3 ms-auto"}>
            {type}
          </Badge>
        </Col>
        <Col xs={10}>{children}</Col>
      </Row>
    </>
  );
};

type AdminParametreTypes = {
  type: ReactNode;
  children: ReactNode;
};

export default AdminParametres;

const AdminGeneral = ({ values }: { values: ParametresSectionGeneral }) => {
  return (
    values && (
      <>
        <AdminParametre type={TYPE_PARAMETRE.STRING}>
          <TextInput
            name="general.mentionCnil"
            label="Mention CNIL"
            tooltipText={
              "Mention CNIL affichée en bas de page (peut être vide)"
            }
            required={false}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.STRING}>
          <TextInput
            name="general.messageEntete"
            label="Message d'entête"
            defaultValue={values?.messageEntete}
            tooltipText={"Message affiché dans le cartouche d'entête"}
            required={false}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.STRING}>
          <TextInput
            name="general.titrePage"
            label="Titre de la page"
            defaultValue={values?.titrePage}
            tooltipText={"Titre affiché dans l'entête du navigateur"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="general.toleranceVoiesMetres"
            label="Tolérance (en m) de chargement des voies"
            defaultValue={values?.titrePage}
            tooltipText={
              "Utilisé dans le chargement des listes déroulantes des voies lorsque les géométries ne sont pas parfaitement en phase"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.STRING}>
          <TextAreaInput
            name="general.accueilPublic"
            required={false}
            label="Personnaliser la page d'accueil publique"
            tooltipText={
              "Écrivez du HTML pour personnaliser votre page d'accueil publique."
            }
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminApplicationMobile = ({
  values,
  allCaracteristiques,
}: {
  values: ParametresSectionMobile;
  allCaracteristiques: string[];
}) => {
  const { setFieldValue } = useFormikContext();
  // Charger toutes les caractéristiques

  const {
    availableOptions: availablePibiOptions,
    setAvailableOptions: setAvailablePibiOptions,
    selectedOptions: selectedPibiOptions,
    setSelectedOptions: setSelectedPibiOptions,
  } = useTransferList({
    listeDisponible: null,
    listeSelectionne: null,
    nameFormik: "mobile.caracteristiquesPibi",
  });

  const {
    availableOptions: availablePenaOptions,
    setAvailableOptions: setAvailablePenaOptions,
    selectedOptions: selectedPenaOptions,
    setSelectedOptions: setSelectedPenaOptions,
  } = useTransferList({
    listeDisponible: null,
    listeSelectionne: null,
    nameFormik: "mobile.caracteristiquesPena",
  });

  useMemo(() => {
    if (
      !allCaracteristiques ||
      !values?.caracteristiquesPibi ||
      !values?.caracteristiquesPena
    ) {
      return;
    }
    //PIBI
    if (availablePibiOptions == null) {
      setAvailablePibiOptions(
        allCaracteristiques.filter(
          (item: any) => item.type === "PIBI" || item.type === "GENERAL",
        ),
      );
    }
    if (selectedPibiOptions == null) {
      setSelectedPibiOptions(
        allCaracteristiques.filter((item: any) => {
          return values?.caracteristiquesPibi
            ?.map((e) => e.id)
            .includes(item.id);
        }),
      );
    }
    //PENA
    if (availablePenaOptions == null) {
      setAvailablePenaOptions(
        allCaracteristiques.filter(
          (item: any) => item.type === "PENA" || item.type === "GENERAL",
        ),
      );
    }
    if (selectedPenaOptions == null) {
      setSelectedPenaOptions(
        allCaracteristiques.filter((item: any) => {
          return values?.caracteristiquesPena
            ?.map((e) => e.id)
            .includes(item.id);
        }),
      );
    }
  }, [
    values,
    availablePenaOptions,
    availablePibiOptions,
    selectedPenaOptions,
    selectedPibiOptions,
    setAvailablePenaOptions,
    setAvailablePibiOptions,
    setSelectedPenaOptions,
    setSelectedPibiOptions,
    allCaracteristiques,
  ]);

  return (
    values && (
      <>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="mobile.affichageIndispo"
            label="Afficher l'état de disponibilité du PEI"
            checked={values?.affichageIndispo}
            tooltipText={
              "Ajout d'une croix rouge sur le symbole du PEI pour signifier l'indisponibilité"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="mobile.affichageSymbolesNormalises"
            label="Utiliser la symbologie du RNDECI"
            checked={values?.affichageSymbolesNormalises}
            tooltipText={
              "Utiliser la symbologie définie dans le RNDECI plutôt que des cercles"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
          <TransferList
            availableOptions={availablePibiOptions}
            selectedOptions={selectedPibiOptions}
            setAvailableOptions={setAvailablePibiOptions}
            setSelectedOptions={setSelectedPibiOptions}
            label={"Caractéristiques à afficher pour les PIBI"}
            tooltipText={
              "Dans l'infobulle d'un PEI sur l'application mobile, " +
              "on affiche des caractéristiques. Ce paramètre permet de définir lesquelles, " +
              "et dans quel ordre elles se présentent"
            }
            required={false}
            name={"mobile.caracteristiquePibi"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
          <TransferList
            availableOptions={availablePenaOptions}
            selectedOptions={selectedPenaOptions}
            setAvailableOptions={setAvailablePenaOptions}
            setSelectedOptions={setSelectedPenaOptions}
            label={"Caractéristiques à afficher pour les PENA"}
            tooltipText={
              "Dans l'infobulle d'un PEI sur l'application mobile, " +
              "on affiche des caractéristiques. Ce paramètre permet de définir lesquelles, " +
              "et dans quel ordre elles se présentent"
            }
            required={false}
            name={"mobile.caracteristiquePena"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="mobile.dureeValiditeToken"
            label="Durée de validité du token"
            tooltipText={
              "Durée (en heures) de validité du jeton de connexion à " +
              "l'application mobile."
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.SELECT}>
          <SelectInput
            name="mobile.gestionAgent"
            label="Type d'agent sélectionné"
            options={typeAgent}
            getOptionValue={(v) => v.id}
            getOptionLabel={(v) => v.libelle}
            onChange={(e) =>
              setFieldValue(
                `mobile.gestionAgent`,
                typeAgent.find((type) => type.id === e.id)?.id,
              )
            }
            defaultValue={typeAgent.find((e) => e.id === values.gestionAgent)}
          />
          <SeeMoreButton id={"infoAgent"}>
            <>
              <p className="m-2">
                C&apos;est un champ texte mixé avec une liste déroulante, comme
                par exemple les listes déroulantes de l&apos;accès rapide du
                module point d&apos;eau (application web remocra). Par défaut il
                est vide. L&apos;utilisateur saisit ce qu&apos;il veut ( Michel
                Dupont par exemple, ou toto@sdisXX.fr ), on va stocker cette
                valeur dans les propriétés de l&apos;application (commun à tous
                les utilisateurs de cette tablette) A la prochaine saisie,
                l&apos;utilisateur peut soit rajouter un autre agent, soit
                retrouver les valeurs précédemment saisies. La liste des agents
                disponibles pour le composant n&apos;est pas envoyée au serveur,
                c&apos;est un facilitateur de saisie ; la tablette enverra le
                résultat de la saisie pour chaque PEI.
                <br />
                Pour l&apos;instant les scénarios suivants ont été identifiés
              </p>
              <div className="h5">Cas 1 - Utilisateur connecté obligatoire</div>
              <p className="m-2">
                L&apos;agent 1 est toujours l&apos;utilisateur connecté, on
                préremplit donc le champ avec ses informations. On rend le champ
                inaccessible pour éviter une modification manuelle
              </p>
              <p className="m-2">L&apos;agent 2 utilise le composant Agent</p>

              <div className="h5">Cas 2 - Utilisateur connecté</div>
              <p className="m-2">
                L&apos;agent 1 est toujours l&apos;utilisateur connecté, on
                préremplit donc le champ avec ses informations. On laisse
                possible la saisie manuelle
              </p>
              <p className="m-2">L&apos;agent 2 utilise le composant Agent</p>

              <div className="h5">Cas 3 - Liste des agents</div>
              <p className="m-2">
                Les 2 champs de formulaire sont vides par défaut, et on propose
                l&apos;utilisation du composant Agent pour les 2
              </p>

              <div className="h5">Cas 4 - Valeur précédente</div>
              <p className="m-2">
                Idem cas 2, mais les valeurs par défaut correspondent à la
                valeur précédemment sélectionnée
              </p>
            </>
          </SeeMoreButton>
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.STRING}>
          <TextInput
            name="mobile.mdpAdministrateur"
            label="Mot de passe administrateur"
            tooltipText={
              "Mot de passe permettant de déverrouiller l'administration de l'appli mobile, à savoir la configuration du serveur ; inutile lors d'un paramétrage via MDM"
            }
            password
            required={false}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="mobile.modeDeconnecte"
            label="Activer le mode déconnecté"
            checked={values?.modeDeconnecte}
            tooltipText={
              "Si la couverture Internet du département n'est pas satisfaisante, vous pouvez passer par le 'mode déconnecté' pour permettre une connexion d'un utilisateur sur une longue durée."
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="mobile.creationPeiMobile"
            label="Autoriser la création de PEI"
            checked={values?.creationPeiMobile}
            tooltipText={
              "Active ou non la fonctionnalité de création de PEI depuis l'application mobile ; si activée, l'utilisateur devra toujours posséder le droit adéquat."
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="mobile.bridagePhoto"
            label="Brider la taille des photos"
            checked={values?.bridagePhoto}
            tooltipText={
              "Les photos prises par l'application mobile peut être de taille importante, et en fonction de la volumétrie des PEI des tournées, on peut avoir des problèmes de synchronisation. Réduire à environ 1920*1080px les photos limite drastiquement le poids de celles-ci, donc les problèmes liés lors de la synchronisation."
            }
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
  const { setFieldValue } = useFormikContext();

  return (
    values && (
      <>
        <AdminParametre type={TYPE_PARAMETRE.SELECT}>
          <SelectInput
            name="cartographie.coordonneesFormatAffichage"
            label="Format d'affichage des coordonnées"
            options={typeAffichageCoordonnees}
            getOptionValue={(v) => v.id}
            getOptionLabel={(v) => v.libelle}
            onChange={(e) =>
              setFieldValue(
                `cartographie.coordonneesFormatAffichage`,
                typeAffichageCoordonnees.find((type) => type.id === e.id)?.id,
              )
            }
            defaultValue={typeAffichageCoordonnees.find(
              (e) => e.id === values.coordonneesFormatAffichage,
            )}
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
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="couvertureHydraulique.deciDistanceMaxParcours"
            label="Distance max de parcours (en m)"
          />
        </AdminParametre>
        <AdminParametre explication="" type={TYPE_PARAMETRE.MULTI_INT}>
          {/*Séparation par des virgule vérifié en backend*/}
          <TextInput
            name="couvertureHydraulique.deciIsodistances"
            label="Isodistances à prendre en compte (en m)"
            tooltipText={"Plusieurs valeurs séparées par des virgules"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="couvertureHydraulique.profondeurCouverture"
            label="Profondeur de couverture (en m)"
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminPei = ({
  values,
  allCaracteristiques,
}: {
  values: ParametresSectionPei;
  allCaracteristiques: string[];
}) => {
  //Pour les colonnes PEI

  const listPossible = Object.values(COLUMN_PEI).map((option: any) => ({
    id: option,
    libelle: option,
  }));

  const {
    availableOptions,
    setAvailableOptions,
    selectedOptions,
    setSelectedOptions,
  } = useTransferList({
    listeDisponible: null,
    listeSelectionne: null,
    nameFormik: "pei.peiColonnes",
  });

  useMemo(() => {
    if (!values?.peiColonnes) {
      return;
    }
    if (availableOptions == null) {
      setAvailableOptions(
        listPossible.filter((e) => !values.peiColonnes?.includes(e)),
      );
    }
    if (selectedOptions == null) {
      setSelectedOptions(values?.peiColonnes);
    }
  }, [
    values,
    setSelectedOptions,
    selectedOptions,
    availableOptions,
    setAvailableOptions,
    listPossible,
  ]);

  const {
    availableOptions: availablePibiOptions,
    setAvailableOptions: setAvailablePibiOptions,
    selectedOptions: selectedPibiOptions,
    setSelectedOptions: setSelectedPibiOptions,
  } = useTransferList({
    listeDisponible: null,
    listeSelectionne: null,
    nameFormik: "pei.caracteristiquesPibiTooltipWeb",
  });

  const {
    availableOptions: availablePenaOptions,
    setAvailableOptions: setAvailablePenaOptions,
    selectedOptions: selectedPenaOptions,
    setSelectedOptions: setSelectedPenaOptions,
  } = useTransferList({
    listeDisponible: null,
    listeSelectionne: null,
    nameFormik: "pei.caracteristiquesPenaTooltipWeb",
  });

  useMemo(() => {
    if (
      !allCaracteristiques ||
      !values?.caracteristiquesPenaTooltipWeb ||
      !values?.caracteristiquesPibiTooltipWeb
    ) {
      return;
    }
    //PIBI
    if (availablePibiOptions == null) {
      setAvailablePibiOptions(
        allCaracteristiques.filter(
          (item: any) => item.type === "PIBI" || item.type === "GENERAL",
        ),
      );
    }
    if (selectedPibiOptions == null) {
      setSelectedPibiOptions(
        allCaracteristiques.filter((item: any) => {
          return values?.caracteristiquesPibiTooltipWeb
            ?.map((e) => e.id)
            .includes(item.id);
        }),
      );
    }
    //PENA
    if (availablePenaOptions == null) {
      setAvailablePenaOptions(
        allCaracteristiques.filter(
          (item: any) => item.type === "PENA" || item.type === "GENERAL",
        ),
      );
    }
    if (selectedPenaOptions == null) {
      setSelectedPenaOptions(
        allCaracteristiques.filter((item: any) => {
          return values?.caracteristiquesPenaTooltipWeb
            ?.map((e) => e.id)
            .includes(item.id);
        }),
      );
    }
  }, [
    values,
    availablePenaOptions,
    availablePibiOptions,
    selectedPenaOptions,
    selectedPibiOptions,
    setAvailablePenaOptions,
    setAvailablePibiOptions,
    setSelectedPenaOptions,
    setSelectedPibiOptions,
    allCaracteristiques,
  ]);

  return (
    values && (
      <>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="pei.peiFicheResumeStandalone"
            label="Activer la fiche Résumé en affichage autonome"
            tooltipText={
              "La fiche Résumé permet d'afficher de l'information condensée sur le PEI ; outre son accessibilité en 1er accordéon de la fiche PEI, vous pouvez, en activant cette option, la voir apparaître comme action spécifique dans la liste des PEI et l'infobulle du PEI dans la carto. Attention, elle est forcément utilisée pour le module de risque en mode grand public"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="pei.peiDisplayTypeEngin"
            label="Permettre la saisie des types d'engins dans la fiche PEI"
            tooltipText={
              "Permet d'afficher le composant de saisie des types d'engins dans la fiche PEI, sinon il sera masqué"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.bufferCarte"
            label="Espace tampon pour la génération de carte (carte des tournées)"
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
          <TransferList
            availableOptions={availablePibiOptions}
            selectedOptions={selectedPibiOptions}
            setAvailableOptions={setAvailablePibiOptions}
            setSelectedOptions={setSelectedPibiOptions}
            label={"Caractéristiques à afficher pour les PIBI"}
            tooltipText={
              "Dans l'infobulle d'un PEI sur la carte de l'application web, " +
              "on affiche des caractéristiques. Ce paramètre permet de définir lesquelles, " +
              "et dans quel ordre elles se présentent"
            }
            required={false}
            name={"pei.caracteristiquesPibiTooltipWeb"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
          <TransferList
            availableOptions={availablePenaOptions}
            selectedOptions={selectedPenaOptions}
            setAvailableOptions={setAvailablePenaOptions}
            setSelectedOptions={setSelectedPenaOptions}
            label={"Caractéristiques à afficher pour les PENA"}
            tooltipText={
              "Dans l'infobulle d'un PEI sur la carte de l'application web, " +
              "on affiche des caractéristiques. Ce paramètre permet de définir lesquelles, " +
              "et dans quel ordre elles se présentent"
            }
            required={false}
            name={"pei.caracteristiquesPenaTooltipWeb"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiDelaiCtrlUrgent"
            label="Nombre de jours avant échéance où un contrôle est considéré comme urgent"
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiDelaiCtrlWarn"
            label="Nombre de jours avant échéance où un contrôle est considéré comme à faire bientôt"
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiDelaiRecoUrgent"
            label="Nombre de jours avant échéance où une reconnaisance est considérée comme urgente"
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiDelaiRecoWarn"
            label="Nombre de jours avant échéance où une reconnaisance est considérée comme à faire bientôt"
          />
        </AdminParametre>

        <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
          <TransferList
            availableOptions={availableOptions}
            selectedOptions={selectedOptions}
            setAvailableOptions={setAvailableOptions}
            setSelectedOptions={setSelectedOptions}
            required={true}
            label={"Colonnes affichées dans la liste des PEI"}
            name={"pei.peiColonnes"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiDeplacementDistWarn"
            label="Seuil de déplacement d'un PEI import CTP"
            tooltipText={
              "Distance de déplacement du PEI au-delà de laquelle afficher un avertissement lors de " +
              "l'import de Contrôles Techniques Périodiques"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="pei.peiGenerationCarteTournee"
            label="Active le module de génération de carte des tournées dans le module Point d’eau"
            checked={values?.peiGenerationCarteTournee}
            tooltipText={
              <>
                Le module de génération nécessite qu’une aggrégation de couche
                nommée <b>TOURNEE</b> soit configurée sur Geoserver. Il est
                recommandé que l’aggrégation de couche contienne <i>a minima</i>{" "}
                :
                <ul>
                  <li>Un fond de plan</li>
                  <li>La couche des PEI</li>
                </ul>
                Le module enverra l’identifiant de la tournée via le VIEWPARAM
                tournee_id permettant de discriminer les PEI à retourner
              </>
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="pei.peiMethodeTriAlphanumerique"
            label="Activer le tri alphanumérique pour les PEI"
            checked={values?.peiMethodeTriAlphanumerique}
            tooltipText={
              "Un tri alphanumérique compare un à un les caractères de chaque chaîne, alors qu'un tri 'naturel' va d'abord comparer la longueur de chaque chaîne"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiRenouvellementCtrlPrive"
            label="Nombre de jours pour le renouvellement des contrôles privés"
            tooltipText={
              "Permet de calculer la prochaine date du CTP par rapport à la dernière saisie"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiRenouvellementCtrlPublic"
            label="Nombre de jours pour le renouvellement des contrôles publics"
            tooltipText={
              "Permet de calculer la prochaine date du CTP par rapport à la dernière saisie"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiRenouvellementRecoPrive"
            label="Nombre de jours pour le renouvellement des reconnaissances privées"
            tooltipText={
              "Permet de calculer la prochaine date de la ROP par rapport à la dernière saisie"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiRenouvellementRecoPublic"
            label="Nombre de jours pour le renouvellement des reconnaissances publiques"
            tooltipText={
              "Permet de calculer la prochaine date de la ROP par rapport à la dernière saisie"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.vitesseEau"
            label="Vitesse de l'eau en m/s"
            tooltipText={"Utile pour avoir un calcul fin des débits simultanés"}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiToleranceCommuneMetres"
            label="Tolérance en mètres pour les communes"
            tooltipText={
              "Permet de charger dans la liste déroulante des communes certaines dont le polygone n'est pas parfaitement en phase avec l'attendu"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiHighlightDuree"
            label="Durée (en secondes) de mise en surbrillance des PEI"
            tooltipText={
              "Lors d'une sélection multiple de PEI depuis une source extérieure (PEI, IT, ...), on met en évidence brièvement les PEI concernés"
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="pei.peiRenumerotationInterneAuto"
            label="Activer la renumérotation interne automatique des PEI"
            checked={values?.peiRenumerotationInterneAuto}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.BOOLEAN}>
          <CheckBoxInput
            name="pei.voieSaisieLibre"
            label="Autoriser la saisie libre pour les voies"
            checked={values?.voieSaisieLibre}
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="pei.peiNombreHistorique"
            label="Nombre de données à afficher sur le graphique de la fiche résumée"
            required={false}
          />
        </AdminParametre>
      </>
    )
  );
};

const AdminPeiLongueIndispo = ({
  values,
  setFieldValue,
}: {
  values: ParametresSectionPeiLongueIndispo;
  setFieldValue: (name: string, value: any) => void;
}) => {
  const typeOrganismeState = useGet(url`/api/type-organisme/get-active`);
  return (
    values && (
      <>
        <AdminParametre type={TYPE_PARAMETRE.STRING}>
          <TextAreaInput
            name="peiLongueIndispo.peiLongueIndisponibiliteMessage"
            label="Message à afficher en cas de PEI indisponible depuis trop longtemps"
            tooltipText={
              "Vous pouvez utilise #MOIS# et #JOURS# dans votre message. Ces deux valeurs seront remplacées automatiquement."
            }
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="peiLongueIndispo.peiLongueIndisponibiliteJours"
            label="Nombre de jours avant de considérer un PEI comme indisponible depuis trop longtemps"
          />
        </AdminParametre>
        <AdminParametre type={TYPE_PARAMETRE.MULTI_STRING}>
          <Multiselect
            name={"peiLongueIndispo.peiLongueIndisponibiliteTypeOrganisme"}
            label="Types d'organismes concernés par le message à afficher en cas de PEI indisponible depuis trop longtemps"
            options={typeOrganismeState?.data}
            getOptionValue={(t) => t.typeOrganismeCode}
            getOptionLabel={(t) => t.typeOrganismeLibelle}
            value={
              values?.peiLongueIndisponibiliteTypeOrganisme?.map((e) =>
                typeOrganismeState?.data?.find(
                  (r) => r.typeOrganismeCode === e,
                ),
              ) ?? undefined
            }
            onChange={(typeOrganisme) => {
              const typeOrganismeCode = typeOrganisme.map(
                (e) => e.typeOrganismeCode,
              );
              typeOrganismeCode.length > 0
                ? setFieldValue(
                    "peiLongueIndispo.peiLongueIndisponibiliteTypeOrganisme",
                    typeOrganismeCode,
                  )
                : setFieldValue(
                    "peiLongueIndispo.peiLongueIndisponibiliteTypeOrganisme",
                    [],
                  );
            }}
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
        <AdminParametre type={TYPE_PARAMETRE.INTEGER}>
          <PositiveNumberInput
            name="permis.permisToleranceChargementMetres"
            label="Tolérance de chargement (en m)"
            tooltipText={"TODO"}
          />
        </AdminParametre>
      </>
    )
  );
};
