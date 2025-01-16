import classNames from "classnames";
import { useFormikContext } from "formik";
import { ReactNode } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { AnomalieCompleteEntity } from "../../Entities/AnomalieEntity.tsx";
import { VisiteTourneeEntity } from "../../Entities/VisiteEntity.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  TextAreaInput,
} from "../../components/Form/Form.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";
import { TYPE_VISITE } from "../../enums/TypeVisiteEnum.tsx";
import TooltipCustom from "../../components/Tooltip/Tooltip.tsx";

const SimplifiedVisiteForm = ({
  index,
  typeVisite,
  listeAnomaliesAssignable,
  typePei,
}: {
  index: number;
  typeVisite?: TYPE_VISITE;
  listeAnomaliesAssignable: AnomalieCompleteEntity[];
  typePei: TYPE_PEI;
}) => {
  const { values, setFieldValue } = useFormikContext<VisiteTourneeEntity>();

  const {
    handleShowClose: handleShowCloseFormulaire,
    activesKeys: activesKeysFormulaire,
  } = useAccordionState([false]);

  const {
    handleShowClose: handleShowCloseAnomalies,
    activesKeys: activesKeysAnomalies,
  } = useAccordionState([false]);

  let filteredListAnomalie: AnomalieCompleteEntity[] = [];
  if (typeVisite) {
    switch (typeVisite) {
      case TYPE_VISITE.RECEPTION: {
        filteredListAnomalie =
          listeAnomaliesAssignable?.filter(
            (e) => e.isReceptionAssignable === true,
          ) ?? [];
        break;
      }
      case TYPE_VISITE.RECO_INIT: {
        filteredListAnomalie =
          listeAnomaliesAssignable?.filter(
            (e) => e.isRecoInitAssignable === true,
          ) ?? [];
        break;
      }
      case TYPE_VISITE.CTP: {
        filteredListAnomalie =
          listeAnomaliesAssignable?.filter((e) => e.isCTPAssignable === true) ??
          [];
        break;
      }
      case TYPE_VISITE.RECOP: {
        filteredListAnomalie =
          listeAnomaliesAssignable?.filter(
            (e) => e.isRecopAssignable === true,
          ) ?? [];
        break;
      }
      case TYPE_VISITE.NP: {
        filteredListAnomalie =
          listeAnomaliesAssignable?.filter((e) => e.isNPAssignable === true) ??
          [];
        break;
      }
      default: {
        break;
      }
    }
  }
  const groupedListeAnomalies = Object.groupBy(
    filteredListAnomalie,
    (item: {
      anomalieId: string;
      poidsAnomalieValIndispoTerrestre: number;
      poidsAnomalieValIndispoHbe: number;
      anomalieLibelle: ReactNode;
      anomalieCategorieLibelle: string;
    }) => item.anomalieCategorieLibelle,
  );

  const listeVoletsAccordion: { header: string; content: ReactNode }[] = [];
  Object.entries(groupedListeAnomalies).map(([categorie, listAno]) =>
    listeVoletsAccordion.push({
      header: categorie, // TODO : Ajouter une indication du nombre d'anomalies cochées
      content: listAno?.map((element) => {
        const anomalieIndex = values.listeSimplifiedVisite[
          index
        ].listeAnomalie?.findIndex((e) => e.anomalieId === element.anomalieId);

        return (
          <CheckBoxInput
            key={element.anomalieId}
            name={`listeSimplifiedVisite[${index}].listeAnomalie[${anomalieIndex}].isAssigned`}
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
            disabled={
              values.listeSimplifiedVisite[index].isNoAnomalieChecked ||
              values.listeSimplifiedVisite[index].isSameAnomalieChecked
            }
            onChange={() => {
              setFieldValue(
                `listeSimplifiedVisite[${index}].listeAnomalie[${anomalieIndex}].isAssigned`,
                !values.listeSimplifiedVisite[index].listeAnomalie[
                  anomalieIndex
                ].isAssigned,
              );
              setFieldValue(`listeSimplifiedVisite[${index}].isModified`, true);
            }}
          />
        );
      }),
    }),
  );

  return (
    <>
      <Row>
        <Col>
          <TooltipCustom
            tooltipText={`Le PEI contrôlé ne présente aucune anomalie`}
            tooltipId={`${index}.isNoAnomalieChecked`}
          >
            <CheckBoxInput
              key={`listeSimplifiedVisite[${index}].isNoAnomalieChecked`}
              name={`listeSimplifiedVisite[${index}].isNoAnomalieChecked`}
              label={"Aucune anomalie"}
              onChange={() => {
                setFieldValue(
                  `listeSimplifiedVisite[${index}].isNoAnomalieChecked`,
                  !values.listeSimplifiedVisite[index].isNoAnomalieChecked,
                );
                // Pour l'appli, la valeur isNoAnomalieChecked n'est pas encore update quand on arrive au niveau du if
                // il faut donc mettre à jour la liste d'anomalie en prenant pour référence la valeur avant update
                if (values.listeSimplifiedVisite[index].isNoAnomalieChecked) {
                  setFieldValue(
                    `listeSimplifiedVisite[${index}].listeAnomalie`,
                    listeAnomaliesAssignable,
                  );
                  setFieldValue(
                    `listeSimplifiedVisite[${index}].isModified`,
                    false,
                  );
                } else {
                  const listeAnomalieUnassigned = listeAnomaliesAssignable?.map(
                    (anomalie) => ({
                      ...anomalie,
                      isAssigned: false,
                    }),
                  );
                  setFieldValue(
                    `listeSimplifiedVisite[${index}].listeAnomalie`,
                    listeAnomalieUnassigned,
                  );
                  setFieldValue(
                    `listeSimplifiedVisite[${index}].isModified`,
                    true,
                  );
                }
              }}
              disabled={
                values.listeSimplifiedVisite[index].isSameAnomalieChecked
              }
            />
          </TooltipCustom>
        </Col>
        <Col>
          <TooltipCustom
            tooltipText={`Les anomalies relevées lors du contrôle du PEI correspondent en tout point à celles du précédent contrôle`}
            tooltipId={`${index}.isSameAnomalieChecked`}
          >
            <CheckBoxInput
              key={`listeSimplifiedVisite[${index}].isSameAnomalieChecked`}
              name={`listeSimplifiedVisite[${index}].isSameAnomalieChecked`}
              label={"Rien à modifier"}
              onChange={() => {
                setFieldValue(
                  `listeSimplifiedVisite[${index}].isSameAnomalieChecked`,
                  !values.listeSimplifiedVisite[index].isSameAnomalieChecked,
                );
                setFieldValue(
                  `listeSimplifiedVisite[${index}].listeAnomalie`,
                  listeAnomaliesAssignable,
                );
                if (values.listeSimplifiedVisite[index].isSameAnomalieChecked) {
                  setFieldValue(
                    `listeSimplifiedVisite[${index}].isModified`,
                    false,
                  );
                } else {
                  setFieldValue(
                    `listeSimplifiedVisite[${index}].isModified`,
                    true,
                  );
                }
              }}
              disabled={values.listeSimplifiedVisite[index].isNoAnomalieChecked}
            />
          </TooltipCustom>
        </Col>
        <Col xs={"auto"} className="text-align-center">
          <Button
            onClick={() => {
              setFieldValue(
                `listeSimplifiedVisite[${index}].listeAnomalie`,
                listeAnomaliesAssignable,
              );
              setFieldValue(
                `listeSimplifiedVisite[${index}].isModified`,
                false,
              );
            }}
            disabled={
              values.listeSimplifiedVisite[index].isNoAnomalieChecked ||
              values.listeSimplifiedVisite[index].isSameAnomalieChecked
            }
          >
            Réinitialiser les anomalies à l&apos;état initial
          </Button>
        </Col>
      </Row>
      <Row>
        <AccordionCustom
          activesKeys={activesKeysFormulaire}
          handleShowClose={handleShowCloseFormulaire}
          list={[
            ...(values.isCtrlDebitPression === true && typePei === TYPE_PEI.PIBI
              ? [
                  {
                    header: "Mesures",
                    content: (
                      <div>
                        <div>
                          <PositiveNumberInput
                            name={`listeSimplifiedVisite[${index}].ctrlDebitPression.ctrlDebit`}
                            label="Débit à 1 bar (m3/h) : "
                            min={0}
                            required={false}
                          />
                        </div>
                        <div>
                          <PositiveNumberInput
                            name={`listeSimplifiedVisite[${index}].ctrlDebitPression.ctrlPressionDyn`}
                            label="Pression dynamique au débit nominal (bar) : "
                            min={0}
                            step={0.01}
                            required={false}
                          />
                        </div>
                        <div>
                          <PositiveNumberInput
                            name={`listeSimplifiedVisite[${index}].ctrlDebitPression.ctrlPression`}
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
                <TextAreaInput
                  name={`listeSimplifiedVisite[${index}].visiteObservation`}
                  required={false}
                />
              ),
            },
          ]}
        />
      </Row>
    </>
  );
};
export default SimplifiedVisiteForm;
