import classNames from "classnames";
import { useFormikContext } from "formik";
import { ReactNode } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { AnomalieCompleteEntity } from "../../Entities/AnomalieEntity.tsx";
import { CtrlDebitPressionEntity } from "../../Entities/CtrlDebitPressionEntity.tsx";
import UtilisateurEntity from "../../Entities/UtilisateurEntity.tsx";
import { VisiteCompleteEntity } from "../../Entities/VisiteEntity.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import { hasDroit } from "../../droits.tsx";
import TYPE_DROIT from "../../enums/DroitEnum.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";
import referenceTypeVisite, {
  TYPE_VISITE,
} from "../../enums/TypeVisiteEnum.tsx";
import url from "../../module/fetch.tsx";

export const getInitialValues = (
  _visitePeiId: string,
  _listeAnomaliesAssignable: AnomalieCompleteEntity[],
  _lastCDP: CtrlDebitPressionEntity,
) => ({
  visiteId: null,
  visitePeiId: _visitePeiId,
  visiteDate: null,
  visiteTypeVisite: null,
  visiteAgent1: null,
  visiteAgent2: null,
  visiteObservation: null,
  listeAnomalie: _listeAnomaliesAssignable,
  isCtrlDebitPression: false,
  ctrlDebitPression: _lastCDP,
});

export const prepareVariables = (values: VisiteCompleteEntity) => ({
  visiteId: values.visiteId ?? null,
  visitePeiId: values.visitePeiId ?? null,
  visiteDate: new Date(values.visiteDate).toISOString() ?? null,
  visiteTypeVisite: values.visiteTypeVisite ?? null,
  visiteAgent1: values.visiteAgent1 ?? null,
  visiteAgent2: values.visiteAgent2 ?? null,
  visiteObservation: values.visiteObservation ?? null,
  listeAnomalie:
    values.listeAnomalie
      .filter((e) => e.isAssigned === true)
      .map((e) => e.anomalieId) ?? [],
  isCtrlDebitPression: values.isCtrlDebitPression ?? false,
  ctrlDebitPression: values.ctrlDebitPression ?? null,
});

const VisiteForm = ({
  nbVisite,
  typePei,
  listeAnomaliesAssignable,
  user,
}: {
  nbVisite: number;
  typePei: string;
  listeAnomaliesAssignable: AnomalieCompleteEntity[];
  user: UtilisateurEntity;
}) => {
  const { values, setValues }: { values: VisiteCompleteEntity } =
    useFormikContext();

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

  const {
    handleShowClose: handleShowCloseFormulaire,
    activesKeys: activesKeysFormulaire,
    show,
  } = useAccordionState([true, false, false, false]);

  const {
    handleShowClose: handleShowCloseAnomalies,
    activesKeys: activesKeysAnomalies,
  } = useAccordionState([]);

  const listTypeVisite = referenceTypeVisite.map((e) => ({
    id: e.code,
    code: e.code,
    libelle: e.libelle,
  }));

  const dynamicListTypeVisite =
    nbVisite === 0 && hasDroit(user, TYPE_DROIT.VISITE_RECEP_C)
      ? listTypeVisite.filter((e) => e.code === TYPE_VISITE.RECEPTION)
      : nbVisite === 1 && hasDroit(user, TYPE_DROIT.VISITE_RECO_INIT_C)
        ? listTypeVisite.filter((e) => e.code === TYPE_VISITE.RECO_INIT)
        : listTypeVisite.filter(
            (e) =>
              e.code !== TYPE_VISITE.RECEPTION &&
              e.code !== TYPE_VISITE.RECO_INIT &&
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

  let filteredListAnomalie = [];
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

    switch (values.visiteTypeVisite) {
      case TYPE_VISITE.RECEPTION: {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isReceptionAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.RECO_INIT: {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isRecoInitAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.CTP: {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isCTPAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.RECOP: {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isRecopAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.NP: {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isNPAssignable === true,
        );
        break;
      }
      default: {
        break;
      }
    }
  }
  const groupedListeAnomalies = Object.groupBy(
    filteredListAnomalie,
    (item: { anomalieCategorieLibelle: string }) =>
      item.anomalieCategorieLibelle,
  );

  const listeVoletsAccordion: { header: string; content: ReactNode }[] = [];
  Object.entries(groupedListeAnomalies).map(([categorie, listAno]) =>
    listeVoletsAccordion.push({
      header: categorie, // TODO : Ajouter une indication du nombre d'anomalie cochée
      content: listAno?.map((element) => {
        const anomalieIndex = values.listeAnomalie?.findIndex(
          (e) => e.anomalieId === element.anomalieId,
        );
        return (
          <CheckBoxInput
            key={element.anomalieId}
            name={"listeAnomalie[" + anomalieIndex + "].isAssigned"}
            label={
              <span
                className={classNames(
                  element.poidsAnomalieValIndispoTerrestre === 5 && "fw-bold",
                  element.poidsAnomalieValIndispoHbe === 5 &&
                    "text-decoration-underline",
                )}
              >
                {element.anomalieLibelle}
              </span>
            }
          />
        );
      }),
    }),
  );

  return (
    <FormContainer>
      <AccordionCustom
        activesKeys={activesKeysFormulaire}
        handleShowClose={handleShowCloseFormulaire}
        list={[
          {
            header: "Informations générales",
            content: (
              <div>
                <div>
                  <DateTimeInput name={"visiteDate"} label="Date et Heure : " />
                </div>
                <div>
                  <SelectForm
                    name={"visiteTypeVisite"}
                    listIdCodeLibelle={dynamicListTypeVisite}
                    label="Type de visite : "
                    required={true}
                    setValues={setValues}
                  />
                </div>
                {typePei === TYPE_PEI.PIBI && (
                  <div>
                    <CheckBoxInput
                      name="isCtrlDebitPression"
                      label="Contrôle débit et pression (CDP)"
                      disabled={!enableCDP}
                    />
                  </div>
                )}
                <Row>
                  <Col>
                    <TextInput
                      name="visiteAgent1"
                      label="Agent 1 : "
                      required={false}
                    />
                  </Col>
                  <Col>
                    <TextInput
                      name="visiteAgent2"
                      label="Agent 2 : "
                      required={false}
                    />
                  </Col>
                </Row>
              </div>
            ),
          },
          ...(values.isCtrlDebitPression === true
            ? [
                {
                  header: "Mesures",
                  content: (
                    <div>
                      <div>
                        <PositiveNumberInput
                          name="ctrlDebitPression.ctrlDebit"
                          label="Débit à 1 bar (m3/h) : "
                          min={0}
                          required={false}
                        />
                      </div>
                      <div>
                        <PositiveNumberInput
                          name="ctrlDebitPression.ctrlPressionDyn"
                          label="Pression dynamique au débit nominal (bar) : "
                          min={0}
                          step={0.01}
                          required={false}
                        />
                      </div>
                      <div>
                        <PositiveNumberInput
                          name="ctrlDebitPression.ctrlPression"
                          label="Pression statique (bar) : "
                          min={0}
                          step={0.01}
                          required={false}
                        />
                      </div>
                    </div>
                  ),
                },
              ]
            : []),
          {
            header: "Points d'attention",
            content: (
              <div>
                {values.visiteTypeVisite ? (
                  <AccordionCustom
                    activesKeys={activesKeysAnomalies}
                    handleShowClose={handleShowCloseAnomalies}
                    list={listeVoletsAccordion}
                  />
                ) : (
                  <p>
                    Sélectionnez le type de visite pour accéder à la liste des
                    anomalies assignables
                  </p>
                )}
              </div>
            ),
          },
          {
            header: "Observations",
            content: (
              <TextAreaInput name="visiteObservation" required={false} />
            ),
          },
        ]}
      />
      <Button
        type="submit"
        variant="primary"
        onClick={() => checkValidity(values, show)}
      >
        Valider
      </Button>
    </FormContainer>
  );
};

export default VisiteForm;

function checkValidity(values: any, show: (e: number) => void) {
  if (
    values.visiteDate === null ||
    values.visiteDate === "" ||
    values.visiteTypeVisite === null ||
    values.visiteTypeVisite === ""
  ) {
    show(0);
  }
}
