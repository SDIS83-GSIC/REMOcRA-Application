import classNames from "classnames";
import { FieldArray } from "formik";
import { Col, Row } from "react-bootstrap";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { MapAnomalieCompleteByPeiId } from "../../Entities/AnomalieEntity.tsx";
import { PeiVisiteTourneeInformationEntity } from "../../Entities/PeiEntity.tsx";
import { SimplifiedVisiteEntity } from "../../Entities/VisiteEntity.tsx";
import DISPONIBILITE_PEI from "../../enums/DisponibiliteEnum.tsx";
import PARAMETRE from "../../enums/ParametreEnum.tsx";
import TYPE_NATURE_DECI from "../../enums/TypeNatureDeci.tsx";
import { TYPE_VISITE } from "../../enums/TypeVisiteEnum.tsx";
import url from "../../module/fetch.tsx";
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
  let libelleNonConforme: string = "";

  const listeParametre = useGet(
    url`/api/parametres?${{
      listeParametreCode: JSON.stringify([PARAMETRE.PEI_LIBELLE_NON_CONFORME]),
    }}`,
  );
  if (listeParametre.isResolved) {
    libelleNonConforme =
      listeParametre?.data?.[
        PARAMETRE.PEI_LIBELLE_NON_CONFORME
      ].parametreValeur.toUpperCase();
  }
  return (
    <FieldArray
      name={name}
      render={() => (
        <>
          {listeElements?.map((value: any, index: number) => {
            const currentPeiId = value.visitePeiId;
            const currentInformation =
              listPeiInformations[
                listPeiInformations.findIndex(
                  (peiInfo) => peiInfo.peiId === currentPeiId,
                )
              ];
            const canHaveGestionnaire: boolean =
              currentInformation.natureDeciCode === TYPE_NATURE_DECI.PRIVE ||
              currentInformation.natureDeciCode ===
                TYPE_NATURE_DECI.CONVENTIONNE ||
              currentInformation.natureDeciCode === TYPE_NATURE_DECI.ICPE ||
              currentInformation.natureDeciCode ===
                TYPE_NATURE_DECI.ICPE_CONVENTIONNE;

            return (
              <>
                <div
                  key={index}
                  className={classNames(
                    "bg-light m-4 p-3 border rounded-3",
                    value.isModified && "border border-success",
                  )}
                >
                  {results && (
                    <Row className="bg-danger">
                      <Col>{results[currentPeiId]?.message}</Col>
                    </Row>
                  )}
                  <Row>
                    <Col className="col-6">
                      <Row>
                        <Col>
                          <b>Numéro complet :</b>{" "}
                          {currentInformation.peiNumeroComplet}
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Nature PEI :</b> {currentInformation.natureLibelle}{" "}
                          ({currentInformation.peiTypePei})
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Adresse :</b> {currentInformation.adresse}{" "}
                          {currentInformation.communeCodePostal}{" "}
                          {currentInformation.communeLibelle} (
                          {currentInformation.communeCodeInsee})
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Nature DECI :</b>{" "}
                          {currentInformation.natureDeciLibelle}
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Domaine :</b> {currentInformation.domaineLibelle}
                        </Col>
                      </Row>
                      {canHaveGestionnaire && (
                        <Row>
                          <Col>
                            <b>Propriétaire :</b>{" "}
                            {currentInformation.gestionnaireLibelle}
                          </Col>
                        </Row>
                      )}
                      <Row>
                        <Col className="d-flex align-items-center gap-2">
                          <b>Etat :</b>
                          {DISPONIBILITE_PEI[
                            currentInformation.peiDisponibiliteTerrestre
                          ] === DISPONIBILITE_PEI.DISPONIBLE ? (
                            <span className="text-white bg-success rounded px-1 text-nowrap">
                              DISPONIBLE
                            </span>
                          ) : DISPONIBILITE_PEI[
                              currentInformation.peiDisponibiliteTerrestre
                            ] === DISPONIBILITE_PEI.INDISPONIBLE ? (
                            <span className="text-white bg-danger rounded px-1 text-nowrap">
                              INDISPONIBLE
                            </span>
                          ) : DISPONIBILITE_PEI[
                              currentInformation.peiDisponibiliteTerrestre
                            ] === DISPONIBILITE_PEI.NON_CONFORME ? (
                            <span className="text-white bg-warning rounded px-1 text-nowrap">
                              {libelleNonConforme.toUpperCase()}
                            </span>
                          ) : (
                            ""
                          )}
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Date dernière ROP :</b>{" "}
                          {currentInformation.peiLastRop
                            ? formatDate(currentInformation.peiLastRop)
                            : ""}
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Date dernier CTP :</b>{" "}
                          {currentInformation.peiLastCtp
                            ? formatDate(currentInformation.peiLastCtp)
                            : ""}
                        </Col>
                      </Row>
                      <Row>
                        <Col>
                          <b>Anomalies :</b> {currentInformation.listeAnomalies}
                        </Col>
                      </Row>
                    </Col>
                    <Col className="col-6">
                      <SimplifiedVisiteForm
                        index={index}
                        typeVisite={typeVisite}
                        listeAnomaliesAssignable={
                          listeAnomaliesAssignable[currentPeiId]
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
