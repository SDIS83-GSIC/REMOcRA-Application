import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useFormikContext } from "formik";
import { FC, useState } from "react";
import { Alert, Col, Row } from "react-bootstrap";
import {
  CheckBoxInput,
  DateTimeInput,
  NumberInput,
  TextAreaInput,
  TextInput,
} from "../../../components/Form/Form.tsx";
import SelectForm from "../../../components/Form/SelectForm.tsx";
import { IconDragNDrop, IconInfo } from "../../../components/Icon/Icon.tsx";
import {
  TYPE_PARAMETRE_RAPPORT_COURRIER,
  userParamRapportCourrier,
} from "../../../Entities/RapportCourrierEntity.tsx";

type SortableParametre = {
  id: string;
  index: number;
  listeElements: any[];
};

const SortableParametreModeleCourrier: FC<SortableParametre> = ({
  id, // La propriété id doit impérativement s'appeler id
  listeElements,
  index,
}) => {
  const { setNodeRef, listeners, transform, transition } = useSortable({ id });

  const styles = {
    transform: CSS.Transform.toString(transform),
    transition,
  };

  const listeTypeParametre = Object.entries(
    TYPE_PARAMETRE_RAPPORT_COURRIER,
  ).map(([key, value]) => {
    return {
      id: key,
      code: value,
      libelle: value,
    };
  });

  const listParamUnavailable = userParamRapportCourrier;
  const { values, setValues, setFieldValue } = useFormikContext();
  const [codeUnavailableIndex, setCodeUnavailableIndex] =
    useState<boolean>(false);

  return (
    <div ref={setNodeRef} style={styles}>
      <Row>
        <Col {...listeners} role="button" className="pe-2">
          <IconDragNDrop />
        </Col>
        <Col>
          <TextInput
            name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreCode`}
            label="Code"
            onChange={(e) => {
              setFieldValue(
                `listeModeleCourrierParametre[${index}].modeleCourrierParametreCode`,
                e.target.value,
              );
              // Stock l'index du composant pour désactiver le bouton suivant le si code correspond à un nom réservé
              if (listParamUnavailable.includes(e.target.value)) {
                setCodeUnavailableIndex(true);
                setFieldValue(
                  "unavailableCode",
                  values && values.unavailableCode
                    ? [...values.unavailableCode, index]
                    : [index],
                );
              } else if (!listParamUnavailable.includes(e.target.value)) {
                setCodeUnavailableIndex(false);
                if (values.unavailableCode) {
                  setFieldValue(
                    "unavailableCode",
                    values.unavailableCode.filter((i) => i !== index),
                  );
                }
              }
            }}
          />
          {codeUnavailableIndex && (
            <Alert className="mt-2" variant="warning">
              Nom de code reservé
            </Alert>
          )}
        </Col>
        <Col>
          <TextInput
            name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreLibelle`}
            label="Libellé"
          />
        </Col>
        <Col>
          <CheckBoxInput
            name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreIsRequired`}
            label="Obligatoire"
          />
        </Col>
      </Row>
      <Row>
        <Col>
          <TextAreaInput
            name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreDescription`}
            label="Description du paramètre"
            required={false}
          />
        </Col>
      </Row>
      <Row className="align-items-end">
        <Col>
          <SelectForm
            name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreType`}
            listIdCodeLibelle={listeTypeParametre}
            label="Type du composant paramètre"
            required={true}
            onChange={(e) => {
              setFieldValue(
                `listeModeleCourrierParametre[${index}].modeleCourrierParametreType`,
                listeTypeParametre.find((type) => type.id === e.target.value)
                  ?.id,
              );

              setFieldValue(
                `listeModeleCourrierParametre[${index}].modeleCourrierParametreValeurDefaut`,
                null,
              );

              // Si c'est un select input et que les champs début de requête et fin ne sont pas initialisés, on les initialise
              if (
                TYPE_PARAMETRE_RAPPORT_COURRIER[e.target.value] ===
                  TYPE_PARAMETRE_RAPPORT_COURRIER.SELECT_INPUT &&
                (listeElements[index].modeleCourrierParametreSourceSqlDebut ==
                  null ||
                  listeElements[index].modeleCourrierParametreSourceSqlFin ==
                    null)
              ) {
                setFieldValue(
                  `listeModeleCourrierParametre[${index}].modeleCourrierParametreSourceSqlDebut`,
                  "SELECT",
                );

                setFieldValue(
                  `listeModeleCourrierParametre[${index}].modeleCourrierParametreSourceSqlFin`,
                  "FROM",
                );
              }
            }}
            defaultValue={listeTypeParametre?.find(
              (e) => e.id === listeElements[index].modeleCourrierParametreType,
            )}
            setValues={setValues}
          />
        </Col>
        <Col>{getValeurDefaut(listeElements, index)}</Col>
      </Row>
      <Row>
        {TYPE_PARAMETRE_RAPPORT_COURRIER[
          listeElements[index].modeleCourrierParametreType
        ] === TYPE_PARAMETRE_RAPPORT_COURRIER.SELECT_INPUT && (
          <>
            <Row className="mt-2">
              <Col>
                <Alert variant="info" key="info" className="border rounded">
                  <IconInfo /> Les codes des paramètres que vous pouvez utiliser
                  dans la requête :
                  <ul>
                    {userParamRapportCourrier.map((param: string) => (
                      <li key={param}>{param}</li>
                    ))}
                  </ul>
                </Alert>
              </Col>
            </Row>
            <Col>
              <TextAreaInput
                name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreSourceSqlDebut`}
                label="Requête SQL pour alimenter le paramètre"
              />
            </Col>
            <Row className="m-2">
              <Col>
                <TextInput
                  name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreSourceSqlId`}
                />
              </Col>
              <Col>as id,</Col>
            </Row>
            <Row className="m-2">
              <Col>
                <TextInput
                  name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreSourceSqlLibelle`}
                />
              </Col>
              <Col>as libelle</Col>
            </Row>

            <Col>
              <TextAreaInput
                name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreSourceSqlFin`}
              />
            </Col>
          </>
        )}
      </Row>
    </div>
  );
};

export function createComponentModeleCourrierToRepeat(
  index: number,
  listeElements: any[],
) {
  return (
    <SortableParametreModeleCourrier
      index={index}
      listeElements={listeElements}
      id={listeElements[index].id}
    />
  );
}

export function getValeurDefaut(listeElements: any, index: number) {
  switch (
    TYPE_PARAMETRE_RAPPORT_COURRIER[
      listeElements[index].modeleCourrierParametreType
    ]
  ) {
    case TYPE_PARAMETRE_RAPPORT_COURRIER.CHECKBOX_INPUT:
      return (
        <CheckBoxInput
          name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.DATE_INPUT:
      return (
        <DateTimeInput
          name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.NUMBER_INPUT:
      return (
        <NumberInput
          name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.TEXT_INPUT:
      return (
        <TextInput
          name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.SELECT_INPUT:
      return (
        <TextInput
          name={`listeModeleCourrierParametre[${index}].modeleCourrierParametreValeurDefaut`}
          label="Valeur par défaut (doit correspondre à l'identifiant technique d'un résultat renvoyé par la requête SQL)"
          required={false}
        />
      );
  }
}
