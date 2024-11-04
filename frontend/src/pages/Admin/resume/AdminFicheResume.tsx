import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { object } from "yup";
import AddRemoveComponent from "../../../components/AddRemoveComponent/AddRemoveComponent.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import {
  FormContainer,
  SelectInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import {
  IconDragNDrop,
  IconNextPage,
  IconPreviousPage,
} from "../../../components/Icon/Icon.tsx";
import TYPE_RESUME_ELEMENT from "../../../enums/TypeResumeElementEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import MoveGridComponent from "../../../components/DragNDrop/MoveGridComponent.tsx";

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
  const [items, setItems] = useState();

  useEffect(() => {
    if (data) {
      setItems(
        Object.entries(
          Object.groupBy(
            data,
            ({ ficheResumeBlocColonne }) => ficheResumeBlocColonne,
          ),
        )?.map(([, value]) =>
          Object.values(value).map((t) => t.ficheResumeBlocTitre),
        ),
      );
    }
  }, [data, setItems]);

  return (
    data &&
    items && (
      <Container>
        <Row>
          <PageTitle
            icon={<IconDragNDrop />}
            title={"Paramétrage de la fiche résumé"}
          />
        </Row>
        <MyFormik
          initialValues={getInitialValues(data)}
          validationSchema={validationSchema}
          isPost={false}
          submitUrl={`/api/fiche-resume/upsert`}
          prepareVariables={(values) => prepareVariables(values)}
          redirectUrl={URLS.MODULE_ADMIN}
        >
          <FormFicheResume items={items} setItems={setItems} data={data} />
        </MyFormik>
      </Container>
    )
  );
};

export default AdminFicheResume;

const FormFicheResume = ({
  items,
  setItems,
}: {
  items: any;
  setItems: (e: any) => void;
}) => {
  const { values, setFieldValue } = useFormikContext();
  const [possibilites, setPossibilites] = useState();
  const [show, setShow] = useState(false);

  return (
    <FormContainer>
      {!show ? (
        <>
          <Row>
            <h3>1 - Choisissez les blocs qui apparaîteront</h3>
            <AddRemoveComponent
              name="listeFicheResumeElement"
              createComponentToRepeat={createComponentToRepeat}
              defaultElement={{
                ficheResumeBlocTypeResumeData: null,
                ficheResumeBlocTitre: null,
                ficheResumeBlocColonne: null,
                ficheResumeBlocLigne: null,
              }}
              listeElements={values.listeFicheResumeElement}
            />
          </Row>
          <Row>
            <Col className="my-3 d-flex justify-content-center">
              <Button
                onClick={() => {
                  setShow(true);
                  // Si une valeur a été supprimée, on l'enlève de la liste
                  let p = values.listeFicheResumeElement
                    .filter((e) => e.ficheResumeBlocColonne == null)
                    .map((e) => e.ficheResumeBlocTitre);
                  let itemsTemp = {};
                  Object.entries(items).map(([key, value]) => {
                    if (key === "possibilites") {
                      p = new Set(
                        value
                          .filter((e) =>
                            values.listeFicheResumeElement
                              .map((t) => t.ficheResumeBlocTitre)
                              .includes(e),
                          )
                          .concat(p),
                      );
                    } else {
                      itemsTemp[key] = value.filter((e) =>
                        values.listeFicheResumeElement
                          .map((t) => t.ficheResumeBlocTitre)
                          .includes(e),
                      );
                    }
                  });

                  // Si on a changé le titre d'un bloc, on remet à jour la liste
                  if (
                    Object.values(itemsTemp).flat().length !==
                    values.listeFicheResumeElement.length
                  ) {
                    itemsTemp = Object.entries(
                      Object.groupBy(
                        values.listeFicheResumeElement,
                        ({ ficheResumeBlocColonne }) => ficheResumeBlocColonne,
                      ),
                    )?.map(([, value]) =>
                      Object.values(value).map((t) => t.ficheResumeBlocTitre),
                    );
                  }

                  // On enlève les valeurs qui sont déjà placées
                  p = [...p].filter(
                    (e) => !Object.values(itemsTemp).flat().includes(e),
                  );
                  setPossibilites(p);
                  setItems({
                    ...itemsTemp,
                    possibilites: p,
                  });
                }}
                disabled={values.listeFicheResumeElement.find(
                  (e) =>
                    e.ficheResumeBlocTitre == null ||
                    e.ficheResumeBlocTypeResumeData == null,
                )}
              >
                Suivant <IconNextPage />
              </Button>
            </Col>
          </Row>
        </>
      ) : (
        <>
          <Row>
            <h3>2 - Choisissez l&apos;emplacement de ces blocs</h3>
            <MoveGridComponent
              items={{
                ...items,
                possibilites: possibilites,
              }}
              setPossibilites={setPossibilites}
              setItems={setItems}
            />
          </Row>
          <Row>
            <Col className="my-3 d-flex justify-content-center">
              <Button onClick={() => setShow(false)}>
                <IconPreviousPage /> Précédent
              </Button>
            </Col>
          </Row>
          <SubmitFormButtons
            returnLink={URLS.MODULE_ADMIN}
            disabledValide={items.possibilites.length > 0}
            onClick={() => {
              const listeWithColRow: FicheResumeElementType[] = [];
              // On récupère les colonnes et les lignes
              let indexI = 0;
              for (const i in items) {
                indexI++;
                let indexJ = 0;
                for (const j in items[i]) {
                  indexJ++;
                  const bloc: FicheResumeElementType =
                    values.listeFicheResumeElement.find(
                      (e) => e.ficheResumeBlocTitre === items[i][j],
                    );

                  bloc.ficheResumeBlocColonne = indexI;
                  bloc.ficheResumeBlocLigne = indexJ;

                  listeWithColRow.push(bloc);
                }
              }
              setFieldValue("listeFicheResumeElement", listeWithColRow);
            }}
          />
        </>
      )}
    </FormContainer>
  );
};

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
            required={false}
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
