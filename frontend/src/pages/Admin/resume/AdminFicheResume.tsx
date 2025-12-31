import { useEffect, useState } from "react";
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

export const getInitialValues = (data: any) => ({
  listeFicheResumeElement: data,
});

export const validationSchema = object({});
export const prepareVariables = (values: { listeFicheResumeElement: any }) => ({
  listeFicheResumeElement: values.listeFicheResumeElement,
});

const AdminFicheResume = () => {
  const { data } = useGet(url`/api/fiche-resume/get-blocs`);
  const [isDisabledButton, setIsDisabledButton] = useState(false);

  const availableTypesModulesEmpty = (isEmpty: boolean) => {
    setIsDisabledButton(isEmpty);
  };

  return (
    data && (
      <GridDragAndDrop
        data={data}
        colonneProperty={"ficheResumeBlocColonne"}
        ligneProperty={"ficheResumeBlocLigne"}
        titreProperty={"ficheResumeBlocTitre"}
        pageTitle={"Configurer la fiche de résumé des PEI"}
        urlToSubmit={`/api/fiche-resume/upsert`}
        validationSchema={validationSchema}
        getInitialValues={getInitialValues}
        prepareVariables={prepareVariables}
        createComponentToRepeat={(index, listeElements) =>
          createComponentToRepeat(
            index,
            listeElements,
            availableTypesModulesEmpty,
          )
        }
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
        isDisabledButton={isDisabledButton}
      />
    )
  );
};

export default AdminFicheResume;

const ComposantToRepeat = ({
  index,
  listeElements,
  onAvailableTypesModulesEmpty,
}: {
  index: number;
  listeElements: FicheResumeElementType[];
  onAvailableTypesModulesEmpty: (isEmpty: boolean) => void;
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

  // Tous les modules qui ne sont pas déjà placés
  const availableTypesModules = typesFicheResumeElement.filter(
    (typeModule) =>
      !listeElements
        .map((e) => e.ficheResumeBlocTypeResumeData)
        .includes(TYPE_RESUME_ELEMENT[typeModule.id]),
  );

  useEffect(() => {
    onAvailableTypesModulesEmpty(availableTypesModules.length === 0);
  }, [availableTypesModules, onAvailableTypesModulesEmpty]);

  const { setFieldValue } = useFormikContext();

  return (
    <div>
      <Row className="align-items-center mt-3">
        <Col>
          <SelectInput
            name={`listeFicheResumeElement[${index}].ficheResumeBlocTypeResumeData`}
            label="Type"
            options={availableTypesModules}
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

function createComponentToRepeat(
  index: number,
  listeElements: any[],
  onAvailableTypesModulesEmpty: (isEmpty: boolean) => void,
) {
  return (
    <ComposantToRepeat
      index={index}
      listeElements={listeElements}
      onAvailableTypesModulesEmpty={onAvailableTypesModulesEmpty}
    />
  );
}
