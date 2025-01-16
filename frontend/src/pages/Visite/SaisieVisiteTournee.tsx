import { useFormikContext } from "formik";
import { useEffect, useMemo, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
import { useAppContext } from "../../components/App/AppProvider.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  TextInput,
} from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { IconTournee } from "../../components/Icon/Icon.tsx";
import { Forbidden, hasDroit, isAuthorized } from "../../droits.tsx";
import { MapAnomalieCompleteByPeiId } from "../../Entities/AnomalieEntity.tsx";
import { PeiVisiteTourneeInformationEntity } from "../../Entities/PeiEntity.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import {
  SimplifiedVisiteEntity,
  VisiteTourneeEntity,
} from "../../Entities/VisiteEntity.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import referenceTypeVisite, {
  TYPE_VISITE,
} from "../../enums/TypeVisiteEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import IterableVisiteForm from "./IterableVisiteForm.tsx";

// Pour respecter la loi des Hooks et empécher de charger des données (useGet) pour un utilisateur qui n'a pas les droits
// Le router appel ce composant qui s'occupe de vérifier les droits et de valider ou non le passage vers le formulaire
const ValidateAccessSaisieVisiteTournee = () => {
  const { user }: { user: UtilisateurEntity } = useAppContext();
  const { tourneeId } = useParams();

  // La vérification suivante s'ajoute aux conditions dans l'appel au composant dans routes.tsx
  // Pour accéder à cet écran il faut :
  // - le droit TOURNEE_R ou TOURNEE_A
  // - au moins un droit de création de visite (parmis les 5 types de visite)
  if (
    !isAuthorized(user, [
      TYPE_DROIT.VISITE_RECEP_C,
      TYPE_DROIT.VISITE_RECO_INIT_C,
      TYPE_DROIT.VISITE_NON_PROGRAMME_C,
      TYPE_DROIT.VISITE_CONTROLE_TECHNIQUE_C,
      TYPE_DROIT.VISITE_RECO_C,
    ])
  ) {
    return <Forbidden />;
  }

  return <SaisieVisiteTournee user={user} tourneeId={tourneeId} />;
};

export default ValidateAccessSaisieVisiteTournee;

const SaisieVisiteTournee = ({
  user,
  tourneeId,
}: {
  user: UtilisateurEntity;
  tourneeId: string;
}) => {
  const navigate = useNavigate();

  const listeAnomaliesAssignable = useGet(
    url`/api/anomalie/getAssignablesAnomaliesByTourneeId/` + tourneeId,
    {},
  );

  const tourneeInformations = useGet(
    url`/api/tournee/fetchTourneeVisiteInfo/` + tourneeId,
    {},
  );

  function onSubmitResult(result: Map<string, string>) {
    // Result est une map <peiId, Erreur> issue de l'appel à CreateMultipleVisiteUseCase
    // Si j'ai des erreurs, je "relance" le formulaire avec uniquement les éléments qui ont causé l'erreur
    if (Object.keys(result).length > 0) {
      setSaveValues({
        tourneeId: tourneeId,
        visiteDate: saveValues!.visiteDate,
        visiteTypeVisite: saveValues!.visiteTypeVisite,
        visiteAgent1: saveValues!.visiteAgent1,
        visiteAgent2: saveValues!.visiteAgent2,
        isCtrlDebitPression: saveValues!.isCtrlDebitPression,
        listeSimplifiedVisite: saveValues!.listeSimplifiedVisite.filter((e) =>
          Object.keys(result).includes(e.visitePeiId),
        ),
      });
      setResult(result);
    }

    // Sinon, toutes mes visites sont insérées, je change d'écran
    else {
      navigate(URLS.LIST_TOURNEE);
    }
  }

  const valuesMemo = useMemo(() => {
    const listeElements: SimplifiedVisiteEntity[] = [];
    tourneeInformations.data?.listCDPByPeiTournee.forEach((cdp) =>
      listeElements.push({
        visitePeiId: cdp.peiId,
        visiteObservation: "",
        listeAnomalie: listeAnomaliesAssignable.data[cdp.peiId],
        ctrlDebitPression: {
          ctrlDebit: cdp.visiteCtrlDebitPressionDebit,
          ctrlPression: cdp.visiteCtrlDebitPressionPression,
          ctrlPressionDyn: cdp.visiteCtrlDebitPressionPressionDyn,
        },
        isNoAnomalieChecked: false,
        isSameAnomalieChecked: false,
        isModified: false,
      }),
    );
    return {
      tourneeId: tourneeId,
      listeSimplifiedVisite: listeElements,
    };
  }, [tourneeInformations, listeAnomaliesAssignable, tourneeId]);

  const [saveValues, setSaveValues] = useState<VisiteTourneeEntity | null>(
    null,
  );
  const [resultState, setResult] = useState<Map<string, string> | null>(null);

  useEffect(() => {
    setSaveValues(valuesMemo);
  }, [valuesMemo]);

  return (
    tourneeInformations.isResolved &&
    saveValues && (
      <MyFormik
        initialValues={getInitialValues(saveValues)}
        isPost={true}
        submitUrl={`/api/visite/createVisiteTournee`}
        prepareVariables={(values) => prepareVariables(values)}
        onSubmit={onSubmitResult} // Méthode appelée à l'obtention d'une réponse au Submit du formulaire
        isPartialSuccess={function (result: Map<string, string>): boolean {
          if (Object.keys(result).length > 0) {
            return true;
          } else {
            return false;
          }
        }}
        successToastMessage="Toutes les visites ont été enregistrées"
        warningToastMessage="Une ou plusieurs visites n'ont pas pu être enregistrées"
      >
        <VisiteTourneeForm
          user={user}
          tourneeLibelle={tourneeInformations.data.tourneeLibelle}
          listPeiInformations={tourneeInformations.data.listPeiInformations}
          listeAnomaliesAssignable={listeAnomaliesAssignable.data}
          setSaveValues={setSaveValues}
          results={resultState}
        />
      </MyFormik>
    )
  );
};

const getInitialValues = (_saveValues: VisiteTourneeEntity) => ({
  tourneeId: _saveValues.tourneeId,
  visiteDate: _saveValues.visiteDate ?? null,
  visiteTypeVisite: _saveValues.visiteTypeVisite ?? null,
  visiteAgent1: _saveValues.visiteAgent1 ?? null,
  visiteAgent2: _saveValues.visiteAgent2 ?? null,
  isCtrlDebitPression: _saveValues.isCtrlDebitPression ?? false,
  listeSimplifiedVisite: _saveValues.listeSimplifiedVisite,
});

export const prepareVariables = (_values: VisiteTourneeEntity) => ({
  tourneeId: _values.tourneeId,
  visiteDate: new Date(_values.visiteDate!).toISOString(),
  visiteTypeVisite: _values.visiteTypeVisite,
  visiteAgent1: _values.visiteAgent1,
  visiteAgent2: _values.visiteAgent2 ?? null,
  isCtrlDebitPression: _values.isCtrlDebitPression ?? false,
  listeSimplifiedVisite: _values.listeSimplifiedVisite.map((visite) => {
    return {
      visitePeiId: visite.visitePeiId,
      visiteObservation:
        visite.visiteObservation?.trim().length > 0
          ? visite.visiteObservation
          : null,
      listeAnomalie:
        visite.listeAnomalie // ne retourne que les anomalieId pour le back
          .filter((e) => e.isAssigned === true)
          .map((e) => e.anomalieId) ?? [],
      ctrlDebitPression: visite.ctrlDebitPression ?? null,
    };
  }),
});

export const VisiteTourneeForm = ({
  user,
  tourneeLibelle,
  listPeiInformations,
  listeAnomaliesAssignable,
  setSaveValues,
  results,
}: {
  user: UtilisateurEntity;
  tourneeLibelle: string;
  listPeiInformations: PeiVisiteTourneeInformationEntity[];
  listeAnomaliesAssignable: MapAnomalieCompleteByPeiId;
  setSaveValues: (e: VisiteTourneeEntity) => void;
  results: Map<string, string> | null;
}) => {
  const { values, setValues } = useFormikContext<VisiteTourneeEntity>();

  const listTypeVisite = referenceTypeVisite.map((e) => ({
    id: e.code,
    code: e.code,
    libelle: e.libelle,
  }));

  // Retire les types de visite que l'utilisateur n'a pas le droit de sasir
  const dynamicListTypeVisite = listTypeVisite.filter(
    (e) =>
      (!hasDroit(user, TYPE_DROIT.VISITE_RECEP_C)
        ? e.code !== TYPE_VISITE.RECEPTION
        : true) &&
      (!hasDroit(user, TYPE_DROIT.VISITE_RECO_INIT_C)
        ? e.code !== TYPE_VISITE.RECO_INIT
        : true) &&
      (!hasDroit(user, TYPE_DROIT.VISITE_CONTROLE_TECHNIQUE_C)
        ? e.code !== TYPE_VISITE.CTP
        : true) &&
      (!hasDroit(user, TYPE_DROIT.VISITE_RECO_C)
        ? e.code !== TYPE_VISITE.RECOP
        : true) &&
      (!hasDroit(user, TYPE_DROIT.VISITE_NON_PROGRAMME_C)
        ? e.code !== TYPE_VISITE.NP
        : true),
  );

  const parametreVisiteTypeCdp = PARAMETRE.TYPE_VISITE_CDP;

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify(parametreVisiteTypeCdp),
    }}`,
  );

  let listeTypeVisiteCdp: TYPE_VISITE[] = [];

  if (listeParametre.isResolved) {
    listeTypeVisiteCdp = JSON.parse(
      listeParametre?.data[parametreVisiteTypeCdp].parametreValeur,
    );
  }

  let enableCDP = false;
  if (values.visiteTypeVisite) {
    if (
      listeTypeVisiteCdp.some(
        (typeVisite) => values.visiteTypeVisite === typeVisite,
      )
    ) {
      enableCDP = true;
    } else {
      values.isCtrlDebitPression = false;
    }
  }

  return (
    <FormContainer>
      <Container>
        <PageTitle
          icon={<IconTournee />}
          title={`Saisir les visites pour la tournée ${tourneeLibelle}`}
        />
        <Row className="align-items-center">
          <Col>
            <DateTimeInput name={"visiteDate"} label="Date et Heure : " />
          </Col>
          <Col>
            <SelectForm
              name={"visiteTypeVisite"}
              listIdCodeLibelle={dynamicListTypeVisite}
              label="Type de visite : "
              required={true}
              setValues={setValues}
            />
          </Col>
        </Row>
        <Row className="align-items-center">
          <Col>
            <CheckBoxInput
              name="isCtrlDebitPression"
              label="Contrôle débit et pression (CDP)"
              disabled={!enableCDP}
            />
          </Col>
          <Col>
            <TextInput name="visiteAgent1" label="Agent 1 : " required={true} />
          </Col>
          <Col>
            <TextInput
              name="visiteAgent2"
              label="Agent 2 : "
              required={false}
            />
          </Col>
        </Row>
      </Container>
      <Container fluid className={"px-5"}>
        <IterableVisiteForm
          name="listeSimplifiedVisite"
          listeElements={values.listeSimplifiedVisite}
          typeVisite={values.visiteTypeVisite}
          listeAnomaliesAssignable={listeAnomaliesAssignable}
          listPeiInformations={listPeiInformations}
          results={results}
        />
        <Button
          type="submit"
          variant="primary"
          onClick={() => setSaveValues(values)}
          disabled={values.listeSimplifiedVisite.some(
            (visite) => visite.isModified === false,
          )}
        >
          Valider
        </Button>
      </Container>
    </FormContainer>
  );
};
