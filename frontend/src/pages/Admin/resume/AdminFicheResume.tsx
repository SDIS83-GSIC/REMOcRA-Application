import { useFormikContext } from "formik";
import { Col, Row } from "react-bootstrap";
import { object } from "yup";
import GridDragAndDrop from "../../../components/DragNDrop/GridDragAndDrop.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { SelectInput, TextInput } from "../../../components/Form/Form.tsx";
import TYPE_RESUME_ELEMENT from "../../../enums/TypeResumeElementEnum.tsx";
import url from "../../../module/fetch.tsx";

type FicheResumeElementType = {
  ficheResumeBlocTypeResumeData: TYPE_RESUME_ELEMENT;
  ficheResumeBlocTitre: string | null;
  ficheResumeBlocColonne?: number | null;
  ficheResumeBlocLigne?: number | null;
};

export const getInitialValues = (data) => ({
  listeFicheResumeElement: data,
});

export const validationSchema = object({});
export const prepareVariables = (values) => ({
  listeFicheResumeElement: values.listeFicheResumeElement,
});

const AdminFicheResume = () => {
  const { data } = useGet(url`/api/fiche-resume/get-blocs`);

  return (
    data && (
      <GridDragAndDrop
        data={data}
        colonneProperty={"ficheResumeBlocColonne"}
        ligneProperty={"ficheResumeBlocLigne"}
        titreProperty={"ficheResumeBlocTitre"}
        pageTitle={"Paramétrer la fiche résumé"}
        urlToSubmit={`/api/fiche-resume/upsert`}
        validationSchema={validationSchema}
        getInitialValues={getInitialValues}
        prepareVariables={prepareVariables}
        createComponentToRepeat={createComponentToRepeat}
        name={"listeFicheResumeElement"}
        defaultElement={{
          ficheResumeBlocTypeResumeData: null,
          ficheResumeBlocTitre: null,
          ficheResumeBlocColonne: null,
          ficheResumeBlocLigne: null,
        }}
        disabledSuivant={(e: FicheResumeElementType) =>
          e.ficheResumeBlocTypeResumeData == null ||
          e.ficheResumeBlocTitre == null
        }
      />
    )
  );
};

export default AdminFicheResume;

const ComposantToRepeat = ({
  index,
  listeElements,
}: {
  index: number;
  listeElements: FicheResumeElementType[];
}) => {
  const typesFicheResumeElement = Object.values(TYPE_RESUME_ELEMENT).map(
    (key) => {
      return {
        id: key,
        code: key,
        libelle: key,
      };
    },
  );
  const { setFieldValue } = useFormikContext();

  return (
    <div>
      <Row className="align-items-center mt-3">
        <Col>
          <SelectInput
            name={`listeFicheResumeElement[${index}].ficheResumeBlocTypeResumeData`}
            label="Type"
            options={typesFicheResumeElement}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            onChange={(e) => {
              setFieldValue(
                `listeFicheResumeElement[${index}].ficheResumeBlocTypeResumeData`,
                typesFicheResumeElement.find((type) => type.id === e.id)?.id,
              );
            }}
            defaultValue={typesFicheResumeElement.find(
              (type) =>
                type.id === listeElements[index].ficheResumeBlocTypeResumeData,
            )}
            required={true}
          />
        </Col>
        <Col>
          <TextInput
            name={`listeFicheResumeElement[${index}].ficheResumeBlocTitre`}
            label="Titre"
            required={true}
          />
        </Col>
      </Row>
    </div>
  );
};

function createComponentToRepeat(index: number, listeElements: any[]) {
  return <ComposantToRepeat index={index} listeElements={listeElements} />;
}
