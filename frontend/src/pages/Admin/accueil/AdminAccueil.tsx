import { useFormikContext } from "formik";
import { Col, Image, Row } from "react-bootstrap";
import { object } from "yup";
import GridDragAndDrop from "../../../components/DragNDrop/GridDragAndDrop.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  FileInput,
  Multiselect,
  NumberInput,
  SelectInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import { TypeModuleRemocra } from "../../../components/ModuleRemocra/ModuleRemocra.tsx";
import url from "../../../module/fetch.tsx";
import { IdCodeLibelleType } from "../../../utils/typeUtils.tsx";

type ModuleType = {
  moduleId: string;
  moduleType: string;
  moduleTitre: string;
  moduleLinkImage: string;
  moduleImage: File;
  moduleColonne: number;
  moduleLigne: number;
  moduleContenuHtml: string;
  listeThematiqueId: string[];
  moduleNbDocument: number;
};

export const getInitialValues = (data) => ({
  listeModule: data,
});

export const validationSchema = object({});

export const prepareVariables = (values) => {
  const formData = new FormData();
  formData.append(
    "listeModule",
    JSON.stringify(
      values.listeModule.map((e: ModuleType) => ({
        moduleId: e.moduleId,
        moduleType: e.moduleType,
        moduleTitre: e.moduleTitre,
        imageName: e.moduleImage?.name,
        moduleColonne: e.moduleColonne,
        moduleLigne: e.moduleLigne,
        moduleContenuHtml: e.moduleContenuHtml,
        moduleNbDocument: e.moduleNbDocument,
        listeThematiqueId: e.listeThematiqueId,
      })),
    ),
  );
  values.listeModule.map(
    (e: ModuleType) =>
      e.moduleImage &&
      formData.append("image_" + e.moduleImage?.name, e.moduleImage),
  );

  return formData;
};

const AdminAccueil = () => {
  const { data } = useGet(url`/api/modules`);

  return (
    data && (
      <GridDragAndDrop
        isMultipartFormData={true}
        data={data}
        colonneProperty={"moduleColonne"}
        ligneProperty={"moduleLigne"}
        titreProperty={"moduleTitre"}
        pageTitle={"Paramétrer la page d'accueil"}
        urlToSubmit={`/api/modules/upsert`}
        validationSchema={validationSchema}
        getInitialValues={getInitialValues}
        prepareVariables={prepareVariables}
        createComponentToRepeat={createComponentToRepeat}
        name={"listeModule"}
        defaultElement={{
          moduleType: null,
          moduleTitre: null,
          moduleImage: null,
          moduleLinkImage: null,
          moduleContenuHtml: null,
          moduleColonne: null,
          moduleLigne: null,
          imageRetiree: false,
        }}
        disabledSuivant={(e: ModuleType) =>
          e.moduleType == null ||
          e.moduleTitre == null ||
          ((e.moduleType === TypeModuleRemocra.COURRIER ||
            e.moduleType === TypeModuleRemocra.DOCUMENT) &&
            e.moduleNbDocument == null)
        }
      />
    )
  );
};

export default AdminAccueil;

const ComposantToRepeat = ({
  index,
  listeElements,
}: {
  index: number;
  listeElements: ModuleType[];
}) => {
  const thematiqueState = useGet(url`/api/thematique/actif`);
  const typesFicheResumeElement = Object.entries(TypeModuleRemocra).map(
    ([key, value]) => {
      return {
        id: key,
        code: key,
        libelle: value,
      };
    },
  );
  const { setFieldValue } = useFormikContext();
  return (
    <Row xs={10}>
      <Row className="align-items-center mt-3">
        <Col>
          <SelectInput
            name={`listeModule[${index}].moduleType`}
            label="Type"
            options={typesFicheResumeElement}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            onChange={(e) => {
              setFieldValue(
                `listeModule[${index}].moduleType`,
                typesFicheResumeElement.find((type) => type.id === e.id)?.id,
              );
            }}
            defaultValue={typesFicheResumeElement.find(
              (type) => type.id === listeElements[index].moduleType,
            )}
            required={true}
          />
        </Col>
        <Col>
          <TextInput
            name={`listeModule[${index}].moduleTitre`}
            label="Titre"
            required={true}
          />
        </Col>
        {listeElements[index].moduleType === TypeModuleRemocra.PERSONNALISE && (
          <Col>
            <TextInput
              name={`listeModule[${index}].moduleContenuHtml`}
              label="Contenu HTML"
              required={true}
            />
          </Col>
        )}
      </Row>
      <Row xs={10}>
        <Col>
          <FileInput
            name={`listeModule[${index}].moduleImage`}
            accept="image/*"
            label="Image"
            required={false}
            onChange={(e) =>
              setFieldValue(
                `listeModule[${index}].moduleImage`,
                e.target.files[0],
              )
            }
          />
        </Col>
        <Col>
          {listeElements[index].moduleLinkImage &&
          listeElements[index].moduleImage === null ? (
            <Image
              width={100}
              fluid
              src={listeElements[index].moduleLinkImage}
            />
          ) : (
            listeElements[index].moduleImage != null && (
              <Image
                width={100}
                fluid
                src={URL.createObjectURL(listeElements[index].moduleImage)}
              />
            )
          )}
        </Col>
      </Row>
      {(listeElements[index].moduleType === TypeModuleRemocra.DOCUMENT ||
        listeElements[index].moduleType === TypeModuleRemocra.COURRIER) && (
        <Row>
          <Col>
            <Multiselect
              name={`listeModule[${index}].listeThematiqueId`}
              label="Thématiques à afficher"
              options={thematiqueState?.data}
              getOptionValue={(t) => t.id}
              getOptionLabel={(t) => t.libelle}
              defaultValue={
                listeElements[index]?.listeThematiqueId?.map((e) =>
                  thematiqueState?.data?.find(
                    (r: IdCodeLibelleType) => r.id === e,
                  ),
                ) ?? undefined
              }
              onChange={(thematique) => {
                const thematiqueId = thematique.map((e) => e.id);
                thematiqueId.length > 0
                  ? setFieldValue(
                      `listeModule[${index}].listeThematiqueId`,
                      thematiqueId,
                    )
                  : setFieldValue(
                      `listeModule[${index}].listeThematiqueId`,
                      undefined,
                    );
              }}
              isClearable={true}
              required={false}
            />
          </Col>
          <Col>
            <NumberInput
              name={`listeModule[${index}].moduleNbDocument`}
              label="Nombre à afficher"
              required={true}
              min={0}
              step={1}
            />
          </Col>
        </Row>
      )}
    </Row>
  );
};

function createComponentToRepeat(index: number, listeElements: any[]) {
  return <ComposantToRepeat index={index} listeElements={listeElements} />;
}
