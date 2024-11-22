import { useFormikContext } from "formik";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import AddRemoveComponent from "../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FileInput,
  FormContainer,
  Multiselect,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../components/Form/Form.tsx";
import SelectForm from "../../components/Form/SelectForm.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconDebitSimultane, IconInfo } from "../../components/Icon/Icon.tsx";
import url from "../../module/fetch.tsx";
import { formatDateTimeForDateTimeInput } from "../../utils/formatDateUtils.tsx";
import { IdCodeLibelleType } from "../../utils/typeUtils.tsx";

type DebitSimultaneType = {
  debitSimultaneNumeroDossier: string;
  siteLibelle: string | undefined;
  maxDiametreCanalisation: number | undefined;
  typeReseauLibelle: string | undefined;
  listeDebitSimultaneMesure: DebitSimultaneMesureType[];
  vitesseEau: number;
};

type DebitSimultaneMesureType = {
  debitSimultaneMesureId: string;
  debitSimultaneMesureDebitRequis: number;
  debitSimultaneMesureDebitMesure: number;
  debitSimultaneMesureDebitRetenu: number;
  debitSimultaneMesureDateMesure: Date;
  debitSimultaneMesureCommentaire: string;
  debitSimultaneMesureIdentiqueReseauVille: boolean;
  listePeiId: string[];
  documentNomFichier: string;
  document: Blob;
  documentId: string | null;
};

export const getInitialValues = (
  data: DebitSimultaneType | null,
  listePeiSelectionnable: { id: string; code: string; libelle: string }[],
  vitesseEau: number | undefined,
  siteLibelle: string | undefined,
  typeReseauLibelle: string | undefined,
  maxDiametreCanalisation: number | undefined,
) => ({
  debitSimultaneNumeroDossier: data?.debitSimultaneNumeroDossier ?? null,
  siteLibelle: siteLibelle ?? null,
  maxDiametreCanalisation: maxDiametreCanalisation ?? null,
  typeReseauLibelle: typeReseauLibelle ?? null,
  listeDebitSimultaneMesure: data?.listeDebitSimultaneMesure ?? [],
  listePeiSelectionnable: listePeiSelectionnable,
  vitesseEau: vitesseEau,
});

export const validationSchema = object({});
export const prepareVariables = (values: DebitSimultaneType) => {
  const formData = new FormData();

  formData.append(
    "debitSimultaneNumeroDossier",
    values.debitSimultaneNumeroDossier,
  );
  formData.append(
    "listeDebitSimultaneMesure",
    JSON.stringify(
      values.listeDebitSimultaneMesure.map((e) => ({
        debitSimultaneMesureId: e.debitSimultaneMesureId,
        debitSimultaneMesureDebitRequis: e.debitSimultaneMesureDebitRequis,
        debitSimultaneMesureDebitMesure: e.debitSimultaneMesureDebitMesure,
        debitSimultaneMesureDebitRetenu: e.debitSimultaneMesureDebitRetenu,
        debitSimultaneMesureDateMesure: e.debitSimultaneMesureDateMesure,
        debitSimultaneMesureCommentaire: e.debitSimultaneMesureCommentaire,
        listePeiId: e.listePeiId,
        documentNomFichier: e.documentNomFichier,
      })),
    ),
  );

  values.listeDebitSimultaneMesure.forEach((e) => {
    formData.append("document_" + e.documentNomFichier, e.document);
  });

  return formData;
};
const DebitSimultane = () => {
  const { values } = useFormikContext<DebitSimultaneType>();

  return (
    <FormContainer>
      <Container>
        <Row>
          <PageTitle
            icon={<IconDebitSimultane />}
            title={"Gestion des débits simultanés"}
          />
        </Row>
        <Row>
          <Col>
            <TextInput
              name={"debitSimultaneNumeroDossier"}
              label="Numéro de dossier"
            />
          </Col>

          <Col className="bg-light p-2 border rounded">
            <div className="fw-bold text-center p-2">
              <IconInfo /> Information
            </div>
            <Row>
              <Col>Site : {values.siteLibelle ?? ""}</Col>
              <Col>Type de réseau : {values.typeReseauLibelle ?? ""}</Col>
            </Row>
            <Row className="mt-2">
              <Col>
                Diamètre maximal de canalisation :{" "}
                {values.maxDiametreCanalisation ?? ""}
              </Col>
            </Row>
          </Col>
        </Row>
        <Row className="mt-3">
          <AddRemoveComponent
            name="listeDebitSimultaneMesure"
            createComponentToRepeat={createComponentToRepeat}
            defaultElement={{
              debitSimultaneMesureId: null,
              debitSimultaneMesureDebitRequis: null,
              debitSimultaneMesureDebitMesure: null,
              debitSimultaneMesureDebitRetenu: null,
              debitSimultaneMesureDateMesure: new Date(),
              debitSimultaneMesureCommentaire: null,
              debitSimultaneMesureIdentiqueReseauVille: false,
              listePeiId: [],
            }}
            listeElements={values.listeDebitSimultaneMesure}
          />
        </Row>
        <SubmitFormButtons />
      </Container>
    </FormContainer>
  );
};

const ComposantToRepeat = ({
  index,
  listeElements,
}: {
  index: number;
  listeElements: DebitSimultaneMesureType[];
}) => {
  const { setValues, values, setFieldValue } = useFormikContext();

  const selectDebitRetenu = [];

  for (let i = 60; i <= 2400; i += 60) {
    selectDebitRetenu.push({
      id: i,
      libelle: i,
    });
  }

  return (
    <Row>
      <Row className="align-items-center mt-3">
        <Col>
          <DateTimeInput
            name={`listeDebitSimultaneMesure[${index}].debitSimultaneMesureDateMesure`}
            label="Date de la mesure"
            required={true}
            value={
              listeElements[index].debitSimultaneMesureDateMesure &&
              formatDateTimeForDateTimeInput(
                listeElements[index].debitSimultaneMesureDateMesure,
              )
            }
          />
        </Col>
        <Col>
          <NumberInput
            name={`listeDebitSimultaneMesure[${index}].debitSimultaneMesureDebitRequis`}
            label={"Débit requis (m³/h)"}
            required={false}
          />
        </Col>
      </Row>
      {!listeElements[index].debitSimultaneMesureIdentiqueReseauVille && (
        <Row>
          <Col>
            <NumberInput
              name={`listeDebitSimultaneMesure[${index}].debitSimultaneMesureDebitMesure`}
              label={"Débit mesuré (m³/h)"}
              required={false}
              value={Math.floor(
                values.vitesseEau *
                  Math.pow(Number(values.maxDiametreCanalisation) / 1000, 2) *
                  2826,
              )}
            />
          </Col>
          <Col>
            <SelectForm
              name={`listeDebitSimultaneMesure[${index}].debitSimultaneMesureDebitRetenu`}
              label="Débit retenu (m³/h)"
              listIdCodeLibelle={selectDebitRetenu}
              defaultValue={selectDebitRetenu?.find(
                (e) =>
                  e.id === listeElements[index].debitSimultaneMesureDebitRetenu,
              )}
              onChange={(e) => {
                setFieldValue(
                  `listeDebitSimultaneMesure[${index}].debitSimultaneMesureDebitRetenu`,
                  e.target.value,
                );
              }}
              required={true}
              setValues={setValues}
            />
          </Col>
        </Row>
      )}
      <Row>
        <Col>
          <Multiselect
            name={"listePei"}
            label="Liste des PEI"
            options={values.listePeiSelectionnable}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            value={
              listeElements[index]?.listePeiId?.map((e) =>
                values.listePeiSelectionnable?.find(
                  (c: IdCodeLibelleType) => c.id === e,
                ),
              ) ?? undefined
            }
            onChange={(pei) => {
              const peiId = pei.map((e) => e.id);
              peiId.length > 0
                ? setFieldValue(
                    `listeDebitSimultaneMesure[${index}].listePeiId`,
                    peiId,
                  )
                : setFieldValue(
                    `listeDebitSimultaneMesure[${index}].listePeiId`,
                    undefined,
                  );
            }}
            isClearable={true}
          />
        </Col>
        <Col>
          <CheckBoxInput
            name={`listeDebitSimultaneMesure[${index}].debitSimultaneMesureIdentiqueReseauVille`}
            label="Identique Réseau Ville ?"
            onChange={(e) => {
              setFieldValue(
                `listeDebitSimultaneMesure[${index}].debitSimultaneMesureIdentiqueReseauVille`,
                e.target.checked,
              );
              if (!e.target.checked) {
                // Formule: Q (m3/s) = V * S = Vitesse_eau*(π*D²)/4; Q (m3/h) = Q(m3/s)*3600
                setFieldValue(
                  `listeDebitSimultaneMesure[${index}].debitSimultaneMesureDebitMesure`,
                  Math.floor(
                    values.vitesseEau *
                      Math.pow(
                        Number(values.maxDiametreCanalisation) / 1000,
                        2,
                      ) *
                      2826,
                  ),
                );
              }
            }}
          />
        </Col>
      </Row>
      <Row className="mt-3">
        <Col>
          <TextAreaInput
            name={`listeDebitSimultaneMesure[${index}].debitSimultaneMesureCommentaire`}
            label={"Commentaire"}
            required={false}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <FileInput
            name={`listeDebitSimultaneMesure[${index}].document`}
            accept="*.*"
            label="Attestation"
            required={false}
            onChange={(e) => {
              // Si on modifie le fichier, on supprime le document associé
              setFieldValue(
                `listeDebitSimultaneMesure[${index}].documentId`,
                null,
              );
              setFieldValue(
                `listeDebitSimultaneMesure[${index}].document`,
                e.target.files[0],
              );
              setFieldValue(
                `listeDebitSimultaneMesure[${index}].documentNomFichier`,
                e.target.files[0].name,
              );
            }}
          />
        </Col>
        {listeElements[index].documentId && !listeElements[index].document && (
          <Col>
            <Button
              variant="link"
              href={
                url`/api/documents/telecharger/` +
                listeElements[index].documentId
              }
            >
              {listeElements[index].documentNomFichier}
            </Button>
          </Col>
        )}
      </Row>
    </Row>
  );
};

function createComponentToRepeat(index: number, listeElements: any[]) {
  return <ComposantToRepeat index={index} listeElements={listeElements} />;
}

export default DebitSimultane;
