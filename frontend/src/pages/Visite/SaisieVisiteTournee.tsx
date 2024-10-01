import { useFormikContext } from "formik";
import React, { useEffect, useMemo, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { useNavigate, useParams } from "react-router-dom";
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
import { MapAnomalieCompleteByPeiId } from "../../Entities/AnomalieEntity.tsx";
import { PeiVisiteTourneeInformationEntity } from "../../Entities/PeiEntity.tsx";
import {
  SimplifiedVisiteEntity,
  VisiteTourneeEntity,
} from "../../Entities/VisiteEntity.tsx";
import referenceTypeVisite, {
  TYPE_VISITE,
} from "../../enums/TypeVisiteEnum.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import IterableVisiteForm from "./IterableVisiteForm.tsx";

const SaisieVisiteTournee = () => {
  const { tourneeId } = useParams();
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
        listeSimplifiedVisite: valuesMemo.listeSimplifiedVisite.filter((e) =>
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
      >
        <VisiteTourneeForm
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

export default SaisieVisiteTournee;

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
      ...visite, // Fait une copie à l'identique de l'élément visite
      listeAnomalie:
        visite.listeAnomalie // Override la valeur listeAnomalie de l'objet visite
          .filter((e) => e.isAssigned === true)
          .map((e) => e.anomalieId) ?? [],
    };
  }),
});

export const VisiteTourneeForm = ({
  tourneeLibelle,
  listPeiInformations,
  listeAnomaliesAssignable,
  setSaveValues,
  results,
}: {
  tourneeLibelle: string;
  listPeiInformations: PeiVisiteTourneeInformationEntity[];
  listeAnomaliesAssignable: MapAnomalieCompleteByPeiId;
  setSaveValues: (e: VisiteTourneeEntity) => void;
  results: Map<string, string> | null;
}) => {
  const { values, setValues } = useFormikContext<VisiteTourneeEntity>();

  const dynamicListTypeVisite = referenceTypeVisite.map((e) => ({
    id: e.code,
    code: e.code,
    libelle: e.libelle,
  }));

  let enableCDP = false;
  if (values.visiteTypeVisite) {
    switch (values.visiteTypeVisite) {
      case TYPE_VISITE.RECEPTION.toString(): {
        enableCDP = true;
        break;
      }
      case TYPE_VISITE.RECO_INIT.toString(): {
        values.isCtrlDebitPression = false;
        break;
      }
      case TYPE_VISITE.CTP.toString(): {
        enableCDP = true;
        break;
      }
      case TYPE_VISITE.RECOP.toString(): {
        values.isCtrlDebitPression = false;
        break;
      }
      case TYPE_VISITE.NP.toString(): {
        values.isCtrlDebitPression = false;
        break;
      }
      default: {
        break;
      }
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
              label="TypeVisite : "
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
        >
          Valider
        </Button>
      </Container>
    </FormContainer>
  );
};
