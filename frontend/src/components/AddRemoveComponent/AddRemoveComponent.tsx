import { FieldArray, useFormikContext } from "formik";
import { ReactNode } from "react";
import { Col } from "react-bootstrap";
import Row from "react-bootstrap/Row";
import CreateButton from "../Form/CreateButton.tsx";
import DeleteButton from "../Form/DeleteButton.tsx";
import { FormLabel } from "../Form/Form.tsx";

/**
 * ATTENTION : doit être utiliser dans un <MyFormik>
 * Permet de gérer, ajouter et supprimer des élements d'une liste
 * A chaque fois que l'utilisateur cliquera sur ajouter, une nouvelle partie de formulaire sera ajoutée
 * @param name : nom de la propriété de formik, une liste d'objet
 * @param defaultElement : élément par défaut qui sera ajouté si l'utilisateur clique sur "Ajouter"
 * @param listeElements : liste des éléments à afficher
 * @param canAdd : Permet de savoir si c'est le composant qui doit gérer les ajouts
 * @param createComponentToRepeat : Fonction qui doit prendre en paramètre l'index et la liste histoire de mettre un name unique par composant.
 *                                  Elle permettra de répéter le formulaire
 */
const AddRemoveComponent = ({
  name,
  defaultElement,
  protectedProperty,
  listeElements,
  canAdd = true,
  createComponentToRepeat,
  label,
}: AddRemoveComponentType) => {
  const { setFieldValue } = useFormikContext();

  return (
    <>
      <FieldArray
        name={name}
        render={(elements) => (
          <>
            {canAdd && (
              <Row>
                {label && (
                  <Col>
                    <FormLabel name={name} label={label} required={false} />
                  </Col>
                )}
                <Col xs={"auto"} className={"ms-auto"}>
                  <CreateButton
                    title={"Ajouter"}
                    onClick={() => elements.push(defaultElement)}
                  />
                </Col>
              </Row>
            )}
            {listeElements.map((value: any, index: number) => {
              return (
                <span
                  key={index}
                  className="d-flex bg-light m-4 p-3 border rounded-3 align-items-center"
                >
                  {createComponentToRepeat(index, listeElements)}
                  {
                    // On n'affiche le bouton supprimer que si la propriété "protected" ne vaut pas TRUE
                    (!protectedProperty ||
                      value[protectedProperty] !== true) && (
                      <Col
                        key={index}
                        className="p-2 d-flex justify-content-end"
                      >
                        <DeleteButton
                          title={"Supprimer"}
                          onClick={() => {
                            elements.remove(index);
                            setFieldValue(
                              name,
                              elements.form.values[name].filter(
                                (e) => e !== value,
                              ),
                            );
                          }}
                        />
                      </Col>
                    )
                  }
                </span>
              );
            })}
          </>
        )}
      />
    </>
  );
};

type AddRemoveComponentType = {
  name: string;
  createComponentToRepeat: (index: number, listeElements: any[]) => ReactNode;
  protectedProperty?: string;
  defaultElement?: any;
  listeElements: any[];
  canAdd?: boolean;
  label?: string;
};

export default AddRemoveComponent;
