import { FieldArray, useFormikContext } from "formik";
import { ReactNode } from "react";
import { Button, Col } from "react-bootstrap";

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
  listeElements,
  canAdd = true,
  createComponentToRepeat,
}: AddRemoveComponentType) => {
  const { setFieldValue } = useFormikContext();

  return (
    <>
      <FieldArray
        name={name}
        render={(elements) => (
          <>
            {canAdd && (
              <Button
                variant="primary"
                onClick={() => elements.push(defaultElement)}
              >
                Ajouter
              </Button>
            )}
            {listeElements.map((value: any, index: number) => {
              return (
                <span
                  key={index}
                  className="d-flex bg-light m-4 p-3 border rounded-3 align-items-center"
                >
                  {createComponentToRepeat(index, listeElements)}
                  <Col key={index} className="p-2 d-flex justify-content-end">
                    <Button
                      variant="danger"
                      onClick={() => {
                        elements.remove(index);
                        setFieldValue(
                          name,
                          elements.form.values[name].filter((e) => e !== value),
                        );
                      }}
                    >
                      Supprimer
                    </Button>
                  </Col>
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
  defaultElement?: any;
  listeElements: any[];
  canAdd?: boolean;
};

export default AddRemoveComponent;
