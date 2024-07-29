import { useFormikContext } from "formik";
import { object } from "yup";
import { Button, Col, Row } from "react-bootstrap";
import { ReactNode } from "react";
import classNames from "classnames";
import { VisiteCompleteEntity } from "../../Entities/VisiteEntity.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  FormContainer,
  TextAreaInput,
  TextInput,
  DateTimeInput,
} from "../../components/Form/Form.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import referenceTypeVisite, {
  TYPE_VISITE,
} from "../../enums/TypeVisiteEnum.tsx";
import { AnomalieCompleteEntity } from "../../Entities/AnomalieEntity.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";

export const getInitialValues = (
  _visitePeiId: string,
  _listeAnomaliesAssignable: AnomalieCompleteEntity[],
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
  ctrlDebitPression: null, // TODO : Ajouter la remonté du dernier CDP pour aider la saisie
});

export const validationSchema = object({});

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
}: {
  nbVisite: number;
  typePei: string;
  listeAnomaliesAssignable: AnomalieCompleteEntity[];
}) => {
  const { values, setValues }: { values: VisiteCompleteEntity } =
    useFormikContext();

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

  const dynamicListTypeVistie =
    nbVisite === 0
      ? listTypeVisite.filter((e) => e.code === TYPE_VISITE.RECEPTION)
      : nbVisite === 1
        ? listTypeVisite.filter((e) => e.code === TYPE_VISITE.RECO_INIT)
        : listTypeVisite.filter(
            (e) =>
              e.code !== TYPE_VISITE.RECEPTION &&
              e.code !== TYPE_VISITE.RECO_INIT,
          );

  let filteredListAnomalie = [];
  if (values.visiteTypeVisite) {
    switch (values.visiteTypeVisite) {
      case TYPE_VISITE.RECEPTION.toString(): {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isReceptionAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.RECO_INIT.toString(): {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isRecoInitAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.CTP.toString(): {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isCTPAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.RECOP.toString(): {
        filteredListAnomalie = listeAnomaliesAssignable.filter(
          (e) => e.isRecopAssignable === true,
        );
        break;
      }
      case TYPE_VISITE.NP.toString(): {
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
                    listIdCodeLibelle={dynamicListTypeVistie}
                    label="TypeVisite : "
                    required={true}
                    setValues={setValues}
                  />
                </div>
                {typePei === TYPE_PEI.PIBI && (
                  <div>
                    <CheckBoxInput
                      name="isCtrlDebitPression"
                      label="Contrôle débit et pression (CDP)"
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
