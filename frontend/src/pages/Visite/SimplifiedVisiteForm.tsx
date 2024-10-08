import classNames from "classnames";
import { useFormikContext } from "formik";
import React, { ReactNode } from "react";
import { AnomalieCompleteEntity } from "../../Entities/AnomalieEntity.tsx";
import { VisiteTourneeEntity } from "../../Entities/VisiteEntity.tsx";
import AccordionCustom, {
  useAccordionState,
} from "../../components/Accordion/Accordion.tsx";
import PositiveNumberInput, {
  CheckBoxInput,
  TextAreaInput,
} from "../../components/Form/Form.tsx";
import { TYPE_VISITE } from "../../enums/TypeVisiteEnum.tsx";
import TYPE_PEI from "../../enums/TypePeiEnum.tsx";

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
  const { values } = useFormikContext<VisiteTourneeEntity>();

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
          />
        );
      }),
    }),
  );

  return (
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
  );
};

export default SimplifiedVisiteForm;
