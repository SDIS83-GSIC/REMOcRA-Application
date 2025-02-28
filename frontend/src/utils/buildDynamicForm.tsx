import { useFormikContext } from "formik";
import { Button, Col, Row } from "react-bootstrap";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  NumberInput,
  SelectInput,
  TextInput,
} from "../components/Form/Form.tsx";
import { TYPE_PARAMETRE_RAPPORT_COURRIER } from "../Entities/RapportCourrierEntity.tsx";

export type DynamicFormWithParametre = {
  dynamicFormId: string;
  dynamicFormLibelle: string;
  dynamicFormDescription: string | undefined;
  listeParametre: DynamicFormParametreFront[];
};

export type DynamicFormParametreFront = {
  dynamicFormParametreId: string;
  dynamicFormParametreLibelle: string;
  dynamicFormParametreCode: string;
  listeSelectInput: { id: string; libelle: string }[];
  dynamicFormParametreDescription: string | undefined;
  dynamicFormParametreValeurDefaut: string | undefined;
  dynamicFormParametreIsRequired: boolean;
  dynamicFormParametreType: TYPE_PARAMETRE_RAPPORT_COURRIER;
};

function buildDynamicForm(
  element: DynamicFormParametreFront,
  values: any,
  setFieldValue: (name: string, e: any) => void,
) {
  switch (TYPE_PARAMETRE_RAPPORT_COURRIER[element.dynamicFormParametreType]) {
    case TYPE_PARAMETRE_RAPPORT_COURRIER.CHECKBOX_INPUT:
      return (
        <CheckBoxInput
          name={element.dynamicFormParametreCode}
          label={element.dynamicFormParametreLibelle}
          required={element.dynamicFormParametreIsRequired}
          checked={
            values[element.dynamicFormParametreCode] ??
            element.dynamicFormParametreValeurDefaut === "true"
          }
          tooltipText={element.dynamicFormParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.NUMBER_INPUT:
      return (
        <NumberInput
          name={element.dynamicFormParametreCode}
          label={element.dynamicFormParametreLibelle}
          required={element.dynamicFormParametreIsRequired}
          value={element.dynamicFormParametreValeurDefaut}
          tooltipText={element.dynamicFormParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.TEXT_INPUT:
      return (
        <TextInput
          name={element.dynamicFormParametreCode}
          label={element.dynamicFormParametreLibelle}
          required={element.dynamicFormParametreIsRequired}
          value={element.dynamicFormParametreValeurDefaut}
          tooltipText={element.dynamicFormParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.DATE_INPUT:
      return (
        <DateTimeInput
          name={element.dynamicFormParametreCode}
          label={element.dynamicFormParametreLibelle}
          required={element.dynamicFormParametreIsRequired}
          defaultValue={element.dynamicFormParametreValeurDefaut}
          tooltipText={element.dynamicFormParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.SELECT_INPUT:
      return (
        <SelectInput
          name={element.dynamicFormParametreCode}
          label={element.dynamicFormParametreLibelle}
          required={element.dynamicFormParametreIsRequired}
          options={element.listeSelectInput}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              element.dynamicFormParametreCode,
              element.listeSelectInput?.find((r) => r.id === e.id).id,
            );
          }}
          defaultValue={element.listeSelectInput?.find(
            (r) => r.id === values[element.dynamicFormParametreCode],
          )}
          tooltipText={element.dynamicFormParametreDescription}
        />
      );
    default:
      return;
  }
}

/**
 * Permet d'afficher dynamiquement le formulaire qu'il s'agisse de rapports personnalisé ou de courrier
 * @param listeWithParametre : liste des rapports ou courriers avec leur paramètres
 * @param contexteLibelle : Rapport personnalisé ou Modèle de courrier par exemple
 * @param reference : afficher la référence ? (applicable que dans le cadre des courriers)
 * @returns
 */
const GenererForm = ({
  listeWithParametre,
  contexteLibelle,
  reference = false,
}: {
  listeWithParametre: DynamicFormWithParametre[];
  contexteLibelle: string;
  reference?: boolean;
}) => {
  const { setFieldValue, values } = useFormikContext();

  const listeIdLibelle = listeWithParametre?.map((e) => ({
    id: e.dynamicFormId,
    libelle: e.dynamicFormLibelle,
  }));

  const rapportPersonnaliseCourant = listeWithParametre?.find(
    (e) => e.dynamicFormId === values.dynamicFormId,
  );

  return (
    <FormContainer>
      <Row>
        <SelectInput
          name={`dynamicFormId`}
          label={contexteLibelle}
          options={listeIdLibelle}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              `dynamicFormId`,
              listeIdLibelle?.find((r) => r.id === e.id).id,
            );
          }}
          defaultValue={listeIdLibelle?.find(
            (r) => r.id === values.dynamicFormId,
          )}
          required={true}
        />
      </Row>
      {reference && (
        <Row>
          <TextInput
            name="courrierReference"
            required={true}
            label="Référence"
          />
        </Row>
      )}
      {rapportPersonnaliseCourant?.dynamicFormDescription && (
        <Row className="mt-3">
          <Col>
            <b>Description : </b>
            {rapportPersonnaliseCourant.dynamicFormDescription}
          </Col>
        </Row>
      )}
      <Row className="mt-3">
        {// Pour chacun des paramètres, on affiche le bon composant
        rapportPersonnaliseCourant?.listeParametre?.map((element) => {
          return buildDynamicForm(element, values, setFieldValue);
        })}
      </Row>
      <Row className={"my-3 d-flex justify-content-center"}>
        <Col sm={"auto"}>
          <Button type="submit" variant={"primary"}>
            Exécuter
          </Button>
        </Col>
      </Row>
    </FormContainer>
  );
};

export default GenererForm;
