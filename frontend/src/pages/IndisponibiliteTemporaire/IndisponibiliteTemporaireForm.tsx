import { useFormikContext } from "formik";
import { useState } from "react";
import Alert from "react-bootstrap/Alert";
import Col from "react-bootstrap/Col";
import Container from "react-bootstrap/Container";
import Row from "react-bootstrap/Row";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  Multiselect,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconIndisponibiliteTemporaire } from "../../components/Icon/Icon.tsx";
import SectionTitle from "../../components/SectionTitle/SectionTitle.tsx";
import url from "../../module/fetch.tsx";
import { requiredArray, requiredString } from "../../module/validators.tsx";
import { formatDateTimeForDateTimeInput } from "../../utils/formatDateUtils.tsx";

export type IndisponibiliteTemporaireFormValues = {
  indisponibiliteTemporaireMotif: string;
  indisponibiliteTemporaireObservation?: string;
  debutImmediat: boolean;
  finInconnue: boolean;
  indisponibiliteTemporaireDateDebut?: string | number | Date;
  indisponibiliteTemporaireDateFin?: string | number | Date;
  listePeiId?: string[];
  indisponibiliteTemporaireMailAvantIndisponibilite?: boolean;
  indisponibiliteTemporaireMailApresIndisponibilite?: boolean;
};

export const getInitialValues = (
  data: Partial<IndisponibiliteTemporaireFormValues>,
) => ({
  indisponibiliteTemporaireMotif: data?.indisponibiliteTemporaireMotif,
  indisponibiliteTemporaireObservation:
    data?.indisponibiliteTemporaireObservation,
  debutImmediat: data?.indisponibiliteTemporaireDateDebut === undefined,
  finInconnue: data?.indisponibiliteTemporaireDateFin === undefined,
  indisponibiliteTemporaireDateDebut: data?.indisponibiliteTemporaireDateDebut,
  indisponibiliteTemporaireDateFin: data?.indisponibiliteTemporaireDateFin,
  listePeiId: data?.listePeiId,
  indisponibiliteTemporaireMailAvantIndisponibilite:
    data?.indisponibiliteTemporaireMailAvantIndisponibilite,
  indisponibiliteTemporaireMailApresIndisponibilite:
    data?.indisponibiliteTemporaireMailApresIndisponibilite,
});
export const validationSchema = object({
  indisponibiliteTemporaireMotif: requiredString,
  listePeiId: requiredArray,
});
export const prepareVariables = (
  values: IndisponibiliteTemporaireFormValues,
) => ({
  indisponibiliteTemporaireMotif: values.indisponibiliteTemporaireMotif,
  indisponibiliteTemporaireObservation:
    values.indisponibiliteTemporaireObservation,
  indisponibiliteTemporaireDateDebut: values.debutImmediat
    ? new Date().toISOString()
    : new Date(values.indisponibiliteTemporaireDateDebut!).toISOString(),
  indisponibiliteTemporaireDateFin: values.finInconnue
    ? null
    : new Date(values.indisponibiliteTemporaireDateFin!).toISOString(),
  listePeiId: values?.listePeiId,
  indisponibiliteTemporaireMailAvantIndisponibilite:
    values?.indisponibiliteTemporaireMailAvantIndisponibilite,
  indisponibiliteTemporaireMailApresIndisponibilite:
    values?.indisponibiliteTemporaireMailApresIndisponibilite,
});

const hasSelectedPeiWithActiveIndispo = (
  selectedPeiIds: string[] | undefined,
  peis: { peiId: string; hasIndispoTempActive: boolean }[] | undefined,
) =>
  (selectedPeiIds?.length ?? 0) > 0 &&
  (selectedPeiIds ?? []).some((selectedPeiId) =>
    (peis ?? []).some(
      (pei) => pei.peiId === selectedPeiId && pei.hasIndispoTempActive,
    ),
  );

const IndisponibiliteTemporaireForm = ({
  title,
  listePeiId,
  indisponibiliteTemporaireId = null,
}: {
  title: string;
  listePeiId?: string[];
  indisponibiliteTemporaireId?: string | null;
}) => {
  const { values, setFieldValue } =
    useFormikContext<IndisponibiliteTemporaireFormValues>();
  const [shouldDisplayIndispoWarning, setShouldDisplayIndispoWarning] =
    useState(false);
  const peiState = useGet(
    url`/api/pei/get-id-numero-indispos-temp-active?${{
      indisponibiliteTemporaireId,
    }}`,
  );

  const selectedPeiIds = values?.listePeiId ?? listePeiId;
  const hasSelectedPeiWithWarningAtInit = hasSelectedPeiWithActiveIndispo(
    selectedPeiIds,
    peiState?.data,
  );
  const shouldDisplayWarningAtInit =
    indisponibiliteTemporaireId === null && hasSelectedPeiWithWarningAtInit;

  return (
    <>
      <Container>
        <PageTitle
          title={title}
          icon={<IconIndisponibiliteTemporaire />}
          displayReturnButton={!((listePeiId?.length ?? 0) > 0)}
        />
        <FormContainer>
          <SectionTitle>Mise en indisponibilité</SectionTitle>

          <Row>
            <Col>
              <TextInput
                name={"indisponibiliteTemporaireMotif"}
                label={"Motif"}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <TextAreaInput
                name={"indisponibiliteTemporaireObservation"}
                required={false}
                label={"Description"}
              />
            </Col>
          </Row>
          <Row>
            <Col>
              <CheckBoxInput
                name={"debutImmediat"}
                required={false}
                label={"Immédiate"}
                onChange={() => {
                  const newValue = !values.debutImmediat;
                  setFieldValue("debutImmediat", newValue);
                  if (!newValue) {
                    // On décoche : on met la date du moment
                    setFieldValue(
                      "indisponibiliteTemporaireDateDebut",
                      new Date().toISOString().slice(0, 16),
                    );
                  } else {
                    // On coche : on vide la date
                    setFieldValue(
                      "indisponibiliteTemporaireDateDebut",
                      undefined,
                    );
                  }
                }}
              />
            </Col>
            {!values.debutImmediat && (
              <Row className="m-2 p-1 bg-light border border-1 border-secondary">
                <Row>
                  <Col>
                    <DateTimeInput
                      name={"indisponibiliteTemporaireDateDebut"}
                      required={!values.debutImmediat}
                      value={
                        values.indisponibiliteTemporaireDateDebut &&
                        formatDateTimeForDateTimeInput(
                          new Date(values.indisponibiliteTemporaireDateDebut),
                        )
                      }
                      label={"Date de début"}
                    />
                  </Col>
                </Row>

                <Row>
                  <Col>
                    <CheckBoxInput
                      name={"indisponibiliteTemporaireMailAvantIndisponibilite"}
                      required={false}
                      label={"Prévenir par mail avant la date prévue"}
                    />
                  </Col>
                </Row>
              </Row>
            )}
          </Row>
          <SectionTitle>Remise en disponibilité</SectionTitle>
          <Row>
            <Col>
              <CheckBoxInput
                name={"finInconnue"}
                required={false}
                label={"Date de fin inconnue"}
                onChange={() => {
                  const newValue = !values.finInconnue;
                  setFieldValue("finInconnue", newValue);
                  if (!newValue) {
                    // On décoche : on met la date du moment + 1 min pour avoir une date différente de la date de début par défaut
                    setFieldValue(
                      "indisponibiliteTemporaireDateFin",
                      new Date(Date.now() + 1 * 60000)
                        .toISOString()
                        .slice(0, 16),
                    );
                  } else {
                    // On coche : on vide la date
                    setFieldValue(
                      "indisponibiliteTemporaireDateFin",
                      undefined,
                    );
                  }
                }}
              />
            </Col>
            {!values.finInconnue && (
              <Row className="m-2 bg-light p-1 border border-1 border-secondary">
                <Row>
                  <Col>
                    <DateTimeInput
                      name={"indisponibiliteTemporaireDateFin"}
                      required={!values.finInconnue}
                      value={
                        values.indisponibiliteTemporaireDateFin &&
                        formatDateTimeForDateTimeInput(
                          new Date(values.indisponibiliteTemporaireDateFin),
                        )
                      }
                      label={"Date de fin prévue"}
                    />
                  </Col>
                </Row>

                <Row>
                  <Col>
                    <CheckBoxInput
                      name={"indisponibiliteTemporaireMailApresIndisponibilite"}
                      required={false}
                      label={"Prévenir par mail avant la date prévue"}
                    />
                  </Col>
                </Row>
              </Row>
            )}
          </Row>
          <SectionTitle>PEI concernés</SectionTitle>
          <Row>
            <Col>
              <Multiselect
                name={"listePei"}
                label="PEI concernés"
                options={peiState?.data}
                getOptionValue={(t) => t.peiId}
                getOptionLabel={(t) => t.peiNumeroComplet}
                value={
                  selectedPeiIds?.map((e) =>
                    peiState?.data?.find(
                      (pei: { peiId: string }) => pei.peiId === e,
                    ),
                  ) ?? undefined
                }
                onChange={(pei) => {
                  const peiId = pei.map((e: { peiId: string }) => e.peiId);
                  setFieldValue("listePeiId", peiId);
                  setShouldDisplayIndispoWarning(
                    hasSelectedPeiWithActiveIndispo(peiId, peiState?.data),
                  );
                }}
                isClearable={true}
              />
              {(shouldDisplayWarningAtInit || shouldDisplayIndispoWarning) && (
                <Alert variant="warning" className="mt-3">
                  Attention : au moins un PEI sélectionné est déjà concerné par
                  une indisponibilité temporaire en cours ou planifiée dans le
                  futur.
                </Alert>
              )}
            </Col>
          </Row>
          <SubmitFormButtons returnLink={!((listePeiId?.length ?? 0) > 0)} />
        </FormContainer>
      </Container>
    </>
  );
};

export default IndisponibiliteTemporaireForm;
