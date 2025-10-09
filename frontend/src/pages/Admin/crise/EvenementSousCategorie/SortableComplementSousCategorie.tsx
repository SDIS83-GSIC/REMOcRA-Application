import { useSortable } from "@dnd-kit/sortable";
import { CSS } from "@dnd-kit/utilities";
import { useFormikContext } from "formik";
import { FC } from "react";
import { Col, Row } from "react-bootstrap";
import {
  TextInput,
  TextAreaInput,
  DateTimeInput,
  NumberInput,
} from "../../../../components/Form/Form.tsx";
import SelectForm from "../../../../components/Form/SelectForm.tsx";
import { TYPE_PARAMETRE_COMPOSANT } from "../../../../Entities/SousTypesEvenementsEntity.tsx";
import { IconDragNDrop } from "../../../../components/Icon/Icon.tsx";

type SortableComplementSousCategorie = {
  id: string;
  index: number;
  listeElements: any[];
};

const SortableParametreSousCategory: FC<SortableComplementSousCategorie> = ({
  id,
  listeElements,
  index,
}) => {
  const { setNodeRef, listeners, transform, transition } = useSortable({ id });
  const listeTypeComplementetre = Object.entries(TYPE_PARAMETRE_COMPOSANT).map(
    ([key, value]) => {
      return {
        id: key,
        code: value,
        libelle: value,
      };
    },
  );

  const { setValues, setFieldValue } = useFormikContext();

  return (
    <div
      ref={setNodeRef}
      style={{
        transform: CSS.Transform.toString(transform),
        transition,
      }}
    >
      <Row>
        <Col {...listeners} role="button" className="pe-2">
          <IconDragNDrop />
        </Col>
      </Row>

      <Row className="align-items-end">
        <Col>
          <TextInput
            name={`evenementSousCategorieComplement[${index}].sousCategorieComplementLibelle`}
            label="Libelle du paramètre"
            required={true}
          />
        </Col>
        <Col>
          <SelectForm
            name={`evenementSousCategorieComplement[${index}].sousCategorieComplementType`}
            listIdCodeLibelle={listeTypeComplementetre}
            label="Type du composant paramètre"
            required={true}
            onChange={(e: any) => {
              setFieldValue(
                `evenementSousCategorieComplement[${index}].sousCategorieComplementType`,
                listeTypeComplementetre.find((type) => type.id === e?.id)?.id,
              );

              setFieldValue(
                `evenementSousCategorieComplement[${index}].sousCategorieComplementValeurDefaut`,
                null,
              );

              setFieldValue(
                `evenementSousCategorieComplement[${index}].sousCategorieComplementSqlDebut`,
                "SELECT",
              );

              setFieldValue(
                `evenementSousCategorieComplement[${index}].sousCategorieComplementSqlFin`,
                "FROM",
              );
            }}
            defaultValue={listeTypeComplementetre?.find(
              (e) => e.id === listeElements[index].sousCategorieComplementType,
            )}
            setValues={setValues}
          />
        </Col>
        <Col>{getValeurDefaut(listeElements, index)}</Col>
      </Row>
      <Row>
        {TYPE_PARAMETRE_COMPOSANT[
          listeElements[index].sousCategorieComplementType
        ] === TYPE_PARAMETRE_COMPOSANT.SELECT_INPUT && (
          <>
            <Col>
              <TextAreaInput
                name={`evenementSousCategorieComplement[${index}].sousCategorieComplementSqlDebut`}
                label="Requête SQL pour alimenter le paramètre"
              />
            </Col>
            <Row className="m-2">
              <Col>
                <TextInput
                  name={`evenementSousCategorieComplement[${index}].sousCategorieComplementSqlId`}
                />
              </Col>
              <Col>as id</Col>
            </Row>
            <Row className="m-2">
              <Col>
                <TextInput
                  name={`evenementSousCategorieComplement[${index}].sousCategorieComplementSqlLibelle`}
                />
              </Col>
              <Col>as libelle</Col>
            </Row>

            <Col>
              <TextAreaInput
                name={`evenementSousCategorieComplement[${index}].sousCategorieComplementSqlFin`}
              />
            </Col>
          </>
        )}
      </Row>
    </div>
  );
};

export function createComponentSousTypeEvenementToRepeat(
  index: number,
  listeElements: any[],
) {
  return (
    <SortableParametreSousCategory
      index={index}
      listeElements={listeElements}
      id={listeElements[index].id}
    />
  );
}

export function getValeurDefaut(listeElements: any, index: number) {
  switch (
    TYPE_PARAMETRE_COMPOSANT[listeElements[index].sousCategorieComplementType]
  ) {
    case TYPE_PARAMETRE_COMPOSANT.DATE_INPUT:
      return (
        <DateTimeInput
          name={`evenementSousCategorieComplement[${index}].sousCategorieComplementValeurDefaut`}
          label="Valeur par défaut"
          defaultValue={
            listeElements[index].sousCategorieComplementValeurDefaut
          }
          required={false}
        />
      );
    case TYPE_PARAMETRE_COMPOSANT.NUMBER_INPUT:
      return (
        <NumberInput
          name={`evenementSousCategorieComplement[${index}].sousCategorieComplementValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_COMPOSANT.TEXT_INPUT:
      return (
        <TextInput
          name={`evenementSousCategorieComplement[${index}].sousCategorieComplementValeurDefaut`}
          label="Valeur par défaut"
          required={false}
        />
      );
    case TYPE_PARAMETRE_COMPOSANT.SELECT_INPUT:
      return (
        <TextInput
          name={`evenementSousCategorieComplement[${index}].sousCategorieComplementValeurDefaut`}
          label="Valeur par défaut (doit correspondre à l'identifiant technique d'un résultat renvoyé par la requête SQL)"
          required={false}
        />
      );
  }
}
