import { useFormikContext } from "formik";
import { ReactNode, useEffect, useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { URLS } from "../../routes.tsx";
import AddRemoveComponent from "../AddRemoveComponent/AddRemoveComponent.tsx";
import PageTitle from "../Elements/PageTitle/PageTitle.tsx";
import { FormContainer } from "../Form/Form.tsx";
import MyFormik from "../Form/MyFormik.tsx";
import SubmitFormButtons from "../Form/SubmitFormButtons.tsx";
import {
  IconDragNDrop,
  IconNextPage,
  IconPreviousPage,
} from "../Icon/Icon.tsx";
import MoveGridComponent from "./MoveGridComponent.tsx";

const GridDragAndDrop = ({
  data,
  colonneProperty,
  ligneProperty,
  titreProperty,
  protectedProperty = false,
  pageTitle,
  urlToSubmit,
  validationSchema,
  getInitialValues,
  prepareVariables,
  createComponentToRepeat,
  name,
  defaultElement,
  disabledSuivant,
  isMultipartFormData = false,
}: {
  data: any[];
  colonneProperty: string;
  ligneProperty: string;
  titreProperty: string;
  protectedProperty: string;
  pageTitle: string;
  urlToSubmit: string;
  validationSchema: any;
  getInitialValues: (e: any) => any;
  prepareVariables: (e: any) => any;
  createComponentToRepeat: (index: number, listeElements: any[]) => ReactNode;
  name: string;
  defaultElement: any;
  disabledSuivant: (element: any) => boolean;
  isMultipartFormData: boolean;
}) => {
  const [items, setItems] = useState<any[]>();

  useEffect(() => {
    if (data) {
      setItems(
        Object.entries(
          Object.groupBy(data, (element) => element[colonneProperty]),
        )?.map(([, value]) =>
          Object.values(value).map((t) => t[titreProperty]),
        ),
      );
    }
  }, [data, setItems, titreProperty, colonneProperty, ligneProperty]);

  return (
    items && (
      <Container>
        <Row>
          <PageTitle icon={<IconDragNDrop />} title={pageTitle} />
        </Row>
        <MyFormik
          initialValues={getInitialValues(data)}
          validationSchema={validationSchema}
          isPost={false}
          isMultipartFormData={isMultipartFormData}
          submitUrl={urlToSubmit}
          prepareVariables={(values) => prepareVariables(values)}
          redirectUrl={URLS.MODULE_ADMIN}
        >
          <Form
            items={items}
            setItems={setItems}
            colonneProperty={colonneProperty}
            createComponentToRepeat={createComponentToRepeat}
            name={name}
            defaultElement={defaultElement}
            ligneProperty={ligneProperty}
            titreProperty={titreProperty}
            protectedProperty={protectedProperty}
            disabledSuivant={disabledSuivant}
          />
        </MyFormik>
      </Container>
    )
  );
};

export default GridDragAndDrop;

const Form = ({
  items,
  setItems,
  name,
  defaultElement,
  createComponentToRepeat,
  colonneProperty,
  ligneProperty,
  titreProperty,
  protectedProperty,
  disabledSuivant,
}: {
  items: any;
  setItems: (e: any) => void;
  name: string;
  defaultElement: any;
  createComponentToRepeat: (index: number, listeElements: any[]) => ReactNode;
  colonneProperty: string;
  ligneProperty: string;
  titreProperty: string;
  protectedProperty: string;
  disabledSuivant: (element: any) => boolean;
}) => {
  const { values, setFieldValue } = useFormikContext();
  const [possibilites, setPossibilites] = useState();
  const [show, setShow] = useState(false);

  return (
    <FormContainer>
      {!show ? (
        <>
          <Row>
            <h3>1 - Choisissez les blocs qui apparaîtront</h3>
            <AddRemoveComponent
              name={name}
              createComponentToRepeat={createComponentToRepeat}
              defaultElement={defaultElement}
              protectedProperty={protectedProperty}
              listeElements={values[name]}
            />
          </Row>
          <Row>
            <Col className="my-3 d-flex justify-content-center">
              <Button
                onClick={() => {
                  setShow(true);

                  // Si une valeur a été supprimée, on l'enlève de la liste
                  let p = values[name]
                    .filter((e) => e[colonneProperty] == null)
                    .map((e) => e[titreProperty]);
                  let itemsTemp = {};
                  Object.entries(items).map(([key, value]) => {
                    if (key === "possibilites") {
                      p = new Set(
                        value
                          .filter((e) =>
                            values[name]
                              .map((t) => t[titreProperty])
                              .includes(e),
                          )
                          .concat(p),
                      );
                    } else {
                      itemsTemp[key] = value.filter((e) =>
                        values[name].map((t) => t[titreProperty]).includes(e),
                      );
                    }
                  });

                  // Si on a changé le titre d'un bloc, on remet à jour la liste
                  if (
                    Object.values(itemsTemp).flat().length !==
                    values[name].length
                  ) {
                    itemsTemp = Object.entries(
                      Object.groupBy(
                        values[name].filter((e) => e[colonneProperty] != null),
                        (element) => element[colonneProperty],
                      ),
                    )?.map(([, value]) =>
                      Object.values(value).map((t) => t[titreProperty]),
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
                disabled={values[name].find((e) => disabledSuivant(e))}
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
              const listeWithColRow = [];
              // On récupère les colonnes et les lignes
              let indexI = 0;
              for (const i in items) {
                indexI++;
                let indexJ = 0;
                for (const j in items[i]) {
                  indexJ++;
                  const bloc = values[name].find(
                    (e) => e[titreProperty] === items[i][j],
                  );

                  bloc[colonneProperty] = indexI;
                  bloc[ligneProperty] = indexJ;

                  listeWithColRow.push(bloc);
                }
              }
              setFieldValue(name, listeWithColRow);
            }}
          />
        </>
      )}
    </FormContainer>
  );
};
