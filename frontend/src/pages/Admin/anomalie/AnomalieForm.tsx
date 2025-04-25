import { FieldArray, useFormikContext } from "formik";
import {
  Button,
  ButtonGroup,
  Card,
  Col,
  Dropdown,
  ListGroup,
  Row,
  ToggleButton,
} from "react-bootstrap";
import { array, object } from "yup";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  FormContainer,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconCreate, IconDelete } from "../../../components/Icon/Icon.tsx";
import url from "../../../module/fetch.tsx";
import {
  requiredBoolean,
  requiredString,
} from "../../../module/validators.tsx";

type AnomalieType = {
  anomalieCode: string;
  anomalieLibelle: string;
  anomalieCommentaire: string;
  anomalieAnomalieCategorieId: string;
  anomalieActif: boolean;
  anomalieProtected: boolean;
  anomalieRendNonConforme: boolean;
  poidsAnomalieList: PoidsAnomalieType[];
};

type PoidsAnomalieType = {
  poidsAnomalieAnomalieId: string;
  poidsAnomalieNatureId: string;
  poidsAnomalieTypeVisite: TypeVisiteEnum[];
  poidsAnomalieValIndispoHbe: number;
  poidsAnomalieValIndispoTerrestre: number;
};

export const getInitialValues = (data?: any) => ({
  anomalieCode: data?.anomalieCode ?? null,
  anomalieLibelle: data?.anomalieLibelle ?? null,
  anomalieCommentaire: data?.anomalieCommentaire ?? null,
  anomalieAnomalieCategorieId: data?.anomalieAnomalieCategorieId ?? null,
  anomalieActif: data?.anomalieActif ?? false,
  anomalieProtected: data?.anomalieProtected ?? false,
  anomalieRendNonConforme: data?.anomalieRendNonConforme ?? false,
  poidsAnomalieList: data?.poidsAnomalieList ?? [],
});

export const prepareValues = (values: any) => ({
  anomalieCode: values.anomalieCode,
  anomalieLibelle: values.anomalieLibelle,
  anomalieCommentaire: values.anomalieCommentaire,
  anomalieAnomalieCategorieId: values.anomalieAnomalieCategorieId,
  anomalieActif: values.anomalieActif,
  anomalieProtected: values.anomalieProtected ?? false,
  anomalieRendNonConforme: values.anomalieRendNonConforme,
  poidsAnomalieList: values.poidsAnomalieList ?? [],
});

export const validationSchema = object({
  anomalieCode: requiredString,
  anomalieLibelle: requiredString,
  anomalieAnomalieCategorieId: requiredString,
  anomalieActif: requiredBoolean,
  anomalieProtected: requiredBoolean,
  anomalieRendNonConforme: requiredBoolean,
  poidsAnomalieList: array(),
});

const AnomalieForm = () => {
  const { values, setFieldValue, setValues } = useFormikContext<AnomalieType>();

  const anomalieReferentielState = useGet(url`/api/anomalie/referentiel`);

  if (!anomalieReferentielState.isResolved) {
    return <Loading />;
  }

  const { categorieList, natureList, typeVisiteList } =
    anomalieReferentielState.data;

  const isSysteme =
    categorieList.filter((c) => {
      return c.anomalieCategorieCode === "SYSTEME";
    })[0].anomalieCategorieId === values.anomalieAnomalieCategorieId;

  return (
    <FormContainer>
      <TextInput label="Libellé" name="anomalieLibelle" required={true} />
      <TextInput
        label="Code"
        name="anomalieCode"
        required={true}
        disabled={values.anomalieProtected}
      />
      <TextAreaInput
        label="Commentaire"
        name="anomalieCommentaire"
        required={false}
      />
      <SelectForm
        name="anomalieAnomalieCategorieId"
        listIdCodeLibelle={categorieList
          .filter((c) => {
            // On prend les anomalies non système...
            if (c.anomalieCategorieCode !== "SYSTEME") {
              return true;
            } else if (
              // ... sauf si elle l'est déjà
              c.anomalieCategorieId === values.anomalieAnomalieCategorieId
            ) {
              return true;
            }
            return false;
          })
          .map((c) => {
            return {
              id: c.anomalieCategorieId,
              libelle: c.anomalieCategorieLibelle,
            };
          })}
        label={"Catégorie"}
        setValues={setValues}
        required={true}
        defaultValue={{ id: values.anomalieAnomalieCategorieId }}
        disabled={values.anomalieProtected}
      />
      {!values.anomalieProtected && (
        <CheckBoxInput name={"anomalieActif"} label={"Actif"} />
      )}
      {!values.anomalieProtected && (
        <CheckBoxInput
          name={"anomalieRendNonConforme"}
          label={"Rend non conforme"}
        />
      )}
      {!isSysteme && (
        <FieldArray
          name={"poidsAnomalieList"}
          render={(arrayHelpers) => (
            <Card className={"mt-2"}>
              <Card.Header>
                <Row>
                  <Col xs={"auto"} className={"d-flex"}>
                    <div className="fw-bold align-self-center">
                      Nature de points d&apos;eau
                    </div>
                  </Col>
                  <Col xs={"auto"} className={"d-flex"}>
                    <Dropdown className={"align-self-center"}>
                      <Dropdown.Toggle variant="primary" id="dropdown-basic">
                        <IconCreate /> Ajouter une nature
                      </Dropdown.Toggle>
                      <Dropdown.Menu>
                        {natureList
                          .filter(
                            (n) =>
                              !values.poidsAnomalieList.some(
                                (pa) => pa.poidsAnomalieNatureId === n.natureId,
                              ),
                          )
                          .map((nature, idxN) => (
                            <Dropdown.Item
                              key={idxN}
                              variant={"link"}
                              className={"text-info"}
                              onClick={() =>
                                arrayHelpers.push({
                                  poidsAnomalieNatureId: nature.natureId,
                                })
                              }
                            >
                              {nature.natureLibelle}
                            </Dropdown.Item>
                          ))}
                      </Dropdown.Menu>
                    </Dropdown>
                  </Col>
                </Row>
              </Card.Header>
              <Card.Body>
                <ListGroup className="list-group-flush">
                  {values.poidsAnomalieList.map((poidsAnomalie, idxPA) => (
                    <ListGroup.Item key={idxPA}>
                      <Row>
                        <Col xs={"auto"} className={"d-flex"}>
                          <div className="fw-bold align-self-center">
                            {
                              natureList.filter(
                                (n) =>
                                  n.natureId ===
                                  poidsAnomalie.poidsAnomalieNatureId,
                              )[0]?.natureLibelle
                            }
                          </div>
                        </Col>
                        <Col xs={"auto"} className={"d-flex align-self-center"}>
                          <Button
                            variant={"link"}
                            className={"text-danger"}
                            onClick={() => arrayHelpers.remove(idxPA)}
                          >
                            <IconDelete />
                          </Button>
                        </Col>
                      </Row>
                      <Row>
                        <Col xs="3">
                          <NumberInput
                            name={`poidsAnomalieList[${idxPA}].poidsAnomalieValIndispoTerrestre`}
                            label="Indispo terrestre"
                            required={false}
                            min={0}
                            max={5}
                            step={1}
                          />
                        </Col>
                        <Col xs="3">
                          <NumberInput
                            name={`poidsAnomalieList[${idxPA}].poidsAnomalieValIndispoHbe`}
                            label="Indispo HBE"
                            required={false}
                            min={0}
                            max={5}
                            step={1}
                          />
                        </Col>
                        <Col xs="6" className={"d-flex"}>
                          {!values.anomalieProtected && (
                            <ButtonGroup className={"align-self-center"}>
                              {typeVisiteList.map((typeVisite, idxTV) => (
                                <ToggleButton
                                  key={`${idxPA}-${typeVisite}`}
                                  value={typeVisite}
                                  name={`poidsAnomalieList[${idxPA}].poidsAnomalieTypeVisite[${idxTV}]`}
                                  onClick={() => {
                                    const res =
                                      values.poidsAnomalieList[idxPA]
                                        .poidsAnomalieTypeVisite ?? [];
                                    if (res.indexOf(typeVisite) === -1) {
                                      res.push(typeVisite);
                                    } else {
                                      res.splice(res.indexOf(typeVisite), 1);
                                    }
                                    setFieldValue(
                                      `poidsAnomalieList[${idxPA}].poidsAnomalieTypeVisite`,
                                      res,
                                    );
                                  }}
                                  checked={values.poidsAnomalieList[
                                    idxPA
                                  ]?.poidsAnomalieTypeVisite?.some(
                                    (p) => p === typeVisite,
                                  )}
                                  type={"radio"}
                                  variant={"outline-primary"}
                                >
                                  {typeVisite}
                                </ToggleButton>
                              ))}
                            </ButtonGroup>
                          )}
                        </Col>
                      </Row>
                    </ListGroup.Item>
                  ))}
                </ListGroup>
              </Card.Body>
            </Card>
          )}
        />
      )}
      <SubmitFormButtons returnLink={true} />
    </FormContainer>
  );
};

export default AnomalieForm;
