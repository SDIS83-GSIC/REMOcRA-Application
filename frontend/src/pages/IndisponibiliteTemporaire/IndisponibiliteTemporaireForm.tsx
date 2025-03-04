import { useFormikContext } from "formik";
import { Button } from "react-bootstrap";
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
import { IconIndisponibiliteTemporaire } from "../../components/Icon/Icon.tsx";
import SectionTitle from "../../components/SectionTitle/SectionTitle.tsx";
import url from "../../module/fetch.tsx";
import { requiredArray, requiredString } from "../../module/validators.tsx";
import { formatDateTimeForDateTimeInput } from "../../utils/formatDateUtils.tsx";
import { URLS } from "../../routes.tsx";

export const getInitialValues = (data) => ({
  indisponibiliteTemporaireMotif: data?.indisponibiliteTemporaireMotif,
  indisponibiliteTemporaireObservation:
    data?.indisponibiliteTemporaireObservation,
  debutImmediat: data?.indisponibiliteTemporaireDateDebut === undefined,
  finInconnue: data?.indisponibiliteTemporaireDateFin === undefined,
  indisponibiliteTemporaireDateDebut: data?.indisponibiliteTemporaireDateDebut,
  indisponibiliteTemporaireDateFin: data?.indisponibiliteTemporaireDateFin,
  listePeiId: data?.indisponibiliteTemporaireListePeiId,
  indisponibiliteTemporaireBasculeAutoIndisponible:
    data?.indisponibiliteTemporaireBasculeAutoIndisponible,
  indisponibiliteTemporaireBasculeAutoDisponible:
    data?.indisponibiliteTemporaireBasculeAutoDisponible,
  indisponibiliteTemporaireMailAvantIndisponibilite:
    data?.indisponibiliteTemporaireMailAvantIndisponibilite,
  indisponibiliteTemporaireMailApresIndisponibilite:
    data?.indisponibiliteTemporaireMailApresIndisponibilite,
});
export const validationSchema = object({
  indisponibiliteTemporaireMotif: requiredString,
  listePeiId: requiredArray,
});
export const prepareVariables = (values) => ({
  indisponibiliteTemporaireMotif: values.indisponibiliteTemporaireMotif,

  indisponibiliteTemporaireObservation:
    values.indisponibiliteTemporaireObservation,

  indisponibiliteTemporaireDateDebut: values.debutImmediat
    ? new Date().toISOString()
    : new Date(values.indisponibiliteTemporaireDateDebut).toISOString(),

  indisponibiliteTemporaireDateFin: values.finInconnue
    ? null
    : new Date(values.indisponibiliteTemporaireDateFin).toISOString(),

  listePeiId: values?.listePeiId,

  indisponibiliteTemporaireBasculeAutoIndisponible:
    values?.indisponibiliteTemporaireBasculeAutoIndisponible,
  indisponibiliteTemporaireBasculeAutoDisponible:
    values?.indisponibiliteTemporaireBasculeAutoDisponible,
  indisponibiliteTemporaireMailAvantIndisponibilite:
    values?.indisponibiliteTemporaireMailAvantIndisponibilite,
  indisponibiliteTemporaireMailApresIndisponibilite:
    values?.indisponibiliteTemporaireMailApresIndisponibilite,
});
const IndisponibiliteTemporaireForm = ({
  title,
  listePeiId,
}: {
  title: string;
  listePeiId?: string[];
}) => {
  const { values, setFieldValue } = useFormikContext();
  const peiState = useGet(url`/api/pei/get-id-numero`);
  return (
    <>
      <Container>
        <PageTitle
          title={title}
          icon={<IconIndisponibiliteTemporaire />}
          displayReturnButton={listePeiId?.length === 0}
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
                          values.indisponibiliteTemporaireDateDebut,
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
                          values.indisponibiliteTemporaireDateFin,
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
          <SectionTitle>PEI Concernés</SectionTitle>
          <Row>
            <Col>
              <Multiselect
                name={"listePei"}
                label="PEI Concernés"
                options={peiState?.data}
                getOptionValue={(t) => t.peiId}
                getOptionLabel={(t) => t.peiNumeroComplet}
                value={
                  values?.listePeiId?.map((e) =>
                    peiState?.data?.find((pei) => pei.peiId === e),
                  ) ?? undefined
                }
                onChange={(pei) => {
                  const peiId = pei.map((e) => e.peiId);
                  peiId.length > 0
                    ? setFieldValue("listePeiId", peiId)
                    : setFieldValue("listePeiId", undefined);
                }}
                isClearable={true}
              />
            </Col>
          </Row>
          <Row className={"text-center mt-3"}>
            <Col>
              <Button
                type={"button"}
                className="mx-2"
                variant={"secondary"}
                href={URLS.LIST_INDISPONIBILITE_TEMPORAIRE}
              >
                Retour
              </Button>
              <Button type={"submit"} className="mx-2">
                Valider
              </Button>
            </Col>
          </Row>
        </FormContainer>
      </Container>
    </>
  );
};

export default IndisponibiliteTemporaireForm;
