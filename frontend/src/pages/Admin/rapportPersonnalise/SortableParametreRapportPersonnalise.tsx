import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useFormikContext } from "formik";
import { FC } from "react";
import { Col, Row } from "react-bootstrap";
import {
  CheckBoxInput,
  DateTimeInput,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { IconDragNDrop } from "../../../components/Icon/Icon.tsx";

type SortableParametre = {
  id: string;
  index: number;
  listeElements: any[];
};

export enum TYPE_PARAMETRE_RAPPORT_PERSONNALISE {
  CHECKBOX_INPUT = "Case à cocher",
  DATE_INPUT = "Champ date",
  NUMBER_INPUT = "Champ nombre",
  SELECT_INPUT = "Liste déroulante",
  TEXT_INPUT = "Champ texte",
}

const SortableParametre: FC<SortableParametre> = ({
  id, // La propriété id doit impérativement s'appeler id
  listeElements,
  index,
}) => {
  const { setNodeRef, listeners, transform, transition } = useSortable({ id });

  const styles = {
    transform: CSS.Transform.toString(transform),
    transition,
  };
  // Faire une card avec les différents champs de formulaire

  const listeTypeParametre = Object.entries(
    TYPE_PARAMETRE_RAPPORT_PERSONNALISE,
  ).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });

  const { setValues, setFieldValue } = useFormikContext();

  return (
    <div ref={setNodeRef} style={styles}>
      <Row>
        <Col {...listeners} role="button" className="pe-2">
          <IconDragNDrop />
        </Col>
        <Col>
          <TextInput
            name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreCode`}
            label="Code"
          />
        </Col>
        <Col>
          <TextInput
            name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreLibelle`}
            label="Libellé"
          />
        </Col>
        <Col>
          <CheckBoxInput
            name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreIsRequired`}
            label="Obligatoire ?"
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <TextAreaInput
            name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreDescription`}
            label="Description du paramètre"
            required={false}
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <SelectForm
            name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreType`}
            listIdCodeLibelle={listeTypeParametre}
            label="Type du composant paramètre"
            required={true}
            onChange={(e) => {
              setFieldValue(
                `listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreType`,
                listeTypeParametre.find((type) => type.id === e.target.value)
                  ?.id,
              );

              setFieldValue(
                `listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreValeurDefaut`,
                null,
              );

              // Si c'est un select input et que les champs début de requête et fin ne sont pas initialisés, on les initialise
              if (
                TYPE_PARAMETRE_RAPPORT_PERSONNALISE[e.target.value] ===
                  TYPE_PARAMETRE_RAPPORT_PERSONNALISE.SELECT_INPUT &&
                (listeElements[index]
                  .rapportPersonnaliseParametreSourceSqlDebut == null ||
                  listeElements[index]
                    .rapportPersonnaliseParametreSourceSqlFin == null)
              ) {
                setFieldValue(
                  `listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreSourceSqlDebut`,
                  "SELECT",
                );

                setFieldValue(
                  `listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreSourceSqlFin`,
                  "FROM",
                );
              }
            }}
            defaultValue={listeTypeParametre?.find(
              (e) =>
                e.id === listeElements[index].rapportPersonnaliseParametreType,
            )}
            setValues={setValues}
          />
        </Col>
        <Col>{getValeurDefaut(listeElements, index)}</Col>
      </Row>
      <Row>
        {TYPE_PARAMETRE_RAPPORT_PERSONNALISE[
          listeElements[index].rapportPersonnaliseParametreType
        ] === TYPE_PARAMETRE_RAPPORT_PERSONNALISE.SELECT_INPUT && (
          <>
            <Col>
              <TextAreaInput
                name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreSourceSqlDebut`}
                label="Requête SQL pour alimenter le paramètre"
              />
            </Col>
            <Row className="m-2">
              <Col>
                <TextInput
                  name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreSourceSqlId`}
                />
              </Col>
              <Col>as id,</Col>
            </Row>
            <Row className="m-2">
              <Col>
                <TextInput
                  name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreSourceSqlLibelle`}
                />
              </Col>
              <Col>as value</Col>
            </Row>

            <Col>
              <TextAreaInput
                name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreSourceSqlFin`}
              />
            </Col>
          </>
        )}
      </Row>
    </div>
  );
};

function getValeurDefaut(listeElements: any, index: number) {
  switch (
    TYPE_PARAMETRE_RAPPORT_PERSONNALISE[
      listeElements[index].rapportPersonnaliseParametreType
    ]
  ) {
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.CHECKBOX_INPUT:
      return (
        <CheckBoxInput
          name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.DATE_INPUT:
      return (
        <DateTimeInput
          name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.NUMBER_INPUT:
      return (
        <NumberInput
          name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.TEXT_INPUT:
      return (
        <TextInput
          name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_PERSONNALISE.SELECT_INPUT:
      return (
        <TextInput
          name={`listeRapportPersonnaliseParametre[${index}].rapportPersonnaliseParametreValeurDefaut`}
          label="Valeur par défaut (doit correspondre à l'identifiant technique d'un résultat renvoyé par la requête SQL)"
          required={false}
        />
      );
  }
}

export function createComponentRapportPersoToRepeat(
  index: number,
  listeElements: any[],
) {
  return (
    <SortableParametre
      index={index}
      listeElements={listeElements}
      id={listeElements[index].id}
    />
  );
}
