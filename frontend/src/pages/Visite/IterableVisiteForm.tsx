import { FieldArray } from "formik";
import React from "react";
import { Col, Row } from "react-bootstrap";
import { MapAnomalieCompleteByPeiId } from "../../Entities/AnomalieEntity.tsx";
import { PeiVisiteTourneeInformationEntity } from "../../Entities/PeiEntity.tsx";
import { SimplifiedVisiteEntity } from "../../Entities/VisiteEntity.tsx";
import DISPONIBILITE_PEI from "../../enums/DisponibiliteEnum.tsx";
import TYPE_NATURE_DECI from "../../enums/TypeNatureDeci.tsx";
import { TYPE_VISITE } from "../../enums/TypeVisiteEnum.tsx";
import { formatDate } from "../../utils/formatDateUtils.tsx";
import SimplifiedVisiteForm from "./SimplifiedVisiteForm.tsx";

const IterableVisiteForm = ({
  name,
  listeElements,
  typeVisite,
  listeAnomaliesAssignable,
  listPeiInformations,
  results,
}: IterableVisiteFormType) => {
  // Sauvegarde des anomalies tel qu'avant la saisie de la visite
  const listeAnomalieInitiale: MapAnomalieCompleteByPeiId =
    listeAnomaliesAssignable;
  return (
    <FieldArray
      name={name}
      render={() => (
        <>
          {listeElements?.map((value: any, index: number) => {
            const currentPeiId = listeElements[index].visitePeiId;
            const currentInformation =
              listPeiInformations[
                listPeiInformations.findIndex(
                  (peiInfo) => peiInfo.peiId === currentPeiId,
                )
              ];
            const canHaveGestionnaire: boolean =
              currentInformation.natureDeciCode === TYPE_NATURE_DECI.PRIVE ||
              currentInformation.natureDeciCode ===
                TYPE_NATURE_DECI.CONVENTIONNE;

            return (
              <>
                <div key={index} className="bg-light m-4 p-3 border rounded-3">
                  {results && (
                    <Row className="bg-danger">
                      {results[currentPeiId]?.message}
                    </Row>
                  )}
                  <Row>
                    <Col className="col-6">
                      <Row>
                        Numéro complet : {currentInformation.peiNumeroComplet}
                      </Row>
                      <Row>
                        Nature PEI : {currentInformation.natureLibelle} (
                        {currentInformation.peiTypePei})
                      </Row>
                      <Row>
                        Adresse : {currentInformation.peiNumeroVoie}{" "}
                        {currentInformation.peiSuffixeVoie}{" "}
                        {currentInformation.voieLibelle}
                        {currentInformation.communeCodePostal}{" "}
                        {currentInformation.communeLibelle} (
                        {currentInformation.communeCodeInsee})
                      </Row>
                      <Row>
                        Nature DECI : {currentInformation.natureDeciLibelle}
                      </Row>
                      <Row>Domaine : {currentInformation.domaineLibelle}</Row>
                      {canHaveGestionnaire && (
                        <Row>
                          Propriétaire :{" "}
                          {currentInformation.gestionnaireLibelle}
                        </Row>
                      )}
                      <Row>
                        Etat :{" "}
                        {
                          DISPONIBILITE_PEI[
                            currentInformation.peiDisponibiliteTerrestre
                          ]
                        }
                      </Row>
                      <Row>
                        Date dernière ROP :{" "}
                        {currentInformation.peiNextRecop
                          ? formatDate(currentInformation.peiNextRecop)
                          : ""}
                      </Row>
                      <Row>
                        Date dernier CTP :{" "}
                        {currentInformation.peiNextCtp
                          ? formatDate(currentInformation.peiNextCtp)
                          : ""}
                      </Row>
                      <Row>Anomalies : {currentInformation.listeAnomalies}</Row>
                    </Col>
                    <Col className="col-6">
                      <SimplifiedVisiteForm
                        index={index}
                        typeVisite={typeVisite}
                        listeAnomaliesAssignable={
                          listeAnomaliesAssignable[currentPeiId]
                        }
                        listeAnomalieInitiale={
                          listeAnomalieInitiale[currentPeiId]
                        }
                        typePei={currentInformation.peiTypePei}
                      />
                    </Col>
                  </Row>
                </div>
              </>
            );
          })}
        </>
      )}
    />
  );
};
export default IterableVisiteForm;
type IterableVisiteFormType = {
  name: string;
  listeElements: SimplifiedVisiteEntity[];
  typeVisite?: TYPE_VISITE;
  listeAnomaliesAssignable: MapAnomalieCompleteByPeiId;
  listPeiInformations: PeiVisiteTourneeInformationEntity[];
  results: Map<string, string> | null;
};
