import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import { Button, Col, Row } from "react-bootstrap";
import Loading from "../components/Elements/Loading/Loading.tsx";
import { useGetRun } from "../components/Fetch/useFetch.tsx";
import {
  CheckBoxInput,
  DateTimeInput,
  FormContainer,
  NumberInput,
  SelectInput,
  TextInput,
} from "../components/Form/Form.tsx";
import { TYPE_PARAMETRE_RAPPORT_COURRIER } from "../Entities/RapportCourrierEntity.tsx";
import { IconLine, IconPoint, IconPolygon } from "../components/Icon/Icon.tsx";

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

export type DynamicFormComplementParametreFront = {
  dynamicFormParametreId: string;
  dynamicFormParametreLibelle: string;
  dynamicFormParametreCode: string;
  listeSelectInput: { id: string; libelle: string }[];
  dynamicFormParametreDescription: string | undefined;
  dynamicFormParametreIsRequired: boolean;
  dynamicFormParametreType: string;
  dynamicFormParametreValeurDefaut: string | undefined;
};

function buildDynamicForm(
  element: DynamicFormParametreFront,
  values: any,
  setFieldValue: (name: string, e: any) => void,
  onGeometrySelect?: (geometryType: string, geometryCode: string) => void,
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
          defaultValue={element.listeSelectInput?.find((r) => {
            return values[element.dynamicFormParametreCode]
              ? r.id === values[element.dynamicFormParametreCode]
              : r.id === element?.dynamicFormParametreValeurDefaut;
          })}
          tooltipText={element.dynamicFormParametreDescription}
        />
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.POLYGON:
      return (
        <Button
          className={"mt-2"}
          key={`polygon`}
          onClick={() => {
            if (onGeometrySelect) {
              onGeometrySelect(
                element.dynamicFormParametreType,
                element.dynamicFormParametreCode,
              );
            }
          }}
        >
          <IconPolygon />
          Dessiner un polygone
        </Button>
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.LINESTRING:
      return (
        <Button
          className={"mt-2"}
          key={`linestring`}
          onClick={() => {
            if (onGeometrySelect) {
              onGeometrySelect(
                element.dynamicFormParametreType,
                element.dynamicFormParametreCode,
              );
            }
          }}
        >
          <IconLine />
          Dessiner une ligne
        </Button>
      );
    case TYPE_PARAMETRE_RAPPORT_COURRIER.POINT:
      return (
        <Button
          className={"mt-2"}
          key={`point`}
          onClick={() => {
            if (onGeometrySelect) {
              onGeometrySelect(
                element.dynamicFormParametreType,
                element.dynamicFormParametreCode,
              );
            }
          }}
        >
          <IconPoint />
          Dessiner un point
        </Button>
      );
    default:
      return;
  }
}

/**
 * Permet d'afficher dynamiquement le formulaire qu'il s'agisse de rapports personnalisé ou de courrier
 * @param listeIdLibelleDescription : liste des rapports ou courriers avec leur description
 * @param contexteLibelle : Rapport personnalisé ou Modèle de courrier par exemple
 * @param reference : afficher la référence ? (applicable que dans le cadre des courriers)
 * @returns
 */
const GenererForm = ({
  listeIdLibelleDescription,
  contexteLibelle,
  reference = false,
  url,
  onGeometrySelect,
  onParametresChange,
}: {
  listeIdLibelleDescription: {
    id: string;
    libelle: string;
    description: string | undefined;
  }[];
  contexteLibelle: string;
  reference?: boolean;
  url: string;
  onGeometrySelect?: (geometryType: string, geometryCode: string) => void;
  onParametresChange?: (parametres: DynamicFormParametreFront[]) => void;
}) => {
  const { setFieldValue, values } = useFormikContext();
  const [objetId, setObjetId] = useState(values.dynamicFormId);

  const {
    data: listeWithParametres,
    run,
    isLoading,
  } = useGetRun(`${url}${objetId}`, {});

  useEffect(() => {
    if (objetId) {
      run();
    }
  }, [objetId, run]);

  // Notifier le parent quand les paramètres changent
  useEffect(() => {
    if (listeWithParametres?.listeParametre && onParametresChange) {
      onParametresChange(listeWithParametres.listeParametre);
    }
  }, [listeWithParametres, onParametresChange]);

  const rapportPersonnaliseCourrierCourant = listeIdLibelleDescription?.find(
    (r) => r.id === objetId,
  );

  return (
    <FormContainer>
      <Row>
        <SelectInput
          name={`dynamicFormId`}
          label={contexteLibelle}
          options={listeIdLibelleDescription}
          getOptionValue={(t) => t.id}
          getOptionLabel={(t) => t.libelle}
          onChange={(e) => {
            setFieldValue(
              `dynamicFormId`,
              listeIdLibelleDescription?.find((r) => r.id === e.id)?.id,
            );
            setFieldValue("courrierReference", "");
            setObjetId(e.id);
          }}
          defaultValue={listeIdLibelleDescription?.find(
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
      {rapportPersonnaliseCourrierCourant?.description && (
        <Row className="mt-3">
          <Col>
            <b>Description : </b>
            {rapportPersonnaliseCourrierCourant.description}
          </Col>
        </Row>
      )}
      <Row className="mt-3">
        {/* Pour chacun des paramètres, on affiche le bon composant */}
        {isLoading ? (
          <Loading />
        ) : (
          listeWithParametres?.listeParametre?.map(
            (element: DynamicFormParametreFront) => {
              return buildDynamicForm(
                element,
                values,
                setFieldValue,
                onGeometrySelect,
              );
            },
          )
        )}
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

export const GenererComplementForm = ({
  listeWithParametre,
  setFieldValue,
  isReadOnly,
  onParamChange,
  values,
}: {
  listeWithParametre: any;
  setFieldValue: any;
  isReadOnly: boolean;
  onParamChange: any;
  values: any;
}) => {
  return (
    <Row className="mt-3">
      {listeWithParametre?.map((element: any, index: number) => {
        return buildDynamicComplement(
          element,
          setFieldValue,
          index,
          isReadOnly,
          onParamChange,
          values,
        );
      })}
    </Row>
  );
};

function buildDynamicComplement(
  element: DynamicFormComplementParametreFront,
  setFieldValue: any,
  index: number,
  isReadOnly: boolean,
  onParamChange: any,
  values: any,
) {
  const updateParams = (valueParam: string) => {
    const baseName = `evenementSousCategorieComplement[${index}]`;
    Object.entries({
      [`${baseName}.valueParam`]: valueParam,
      [`${baseName}.idParam`]: element.dynamicFormParametreId,
    }).forEach(([name, value]) => {
      onParamChange(name, value);
    });
  };

  switch (element.dynamicFormParametreType) {
    case "DATE_INPUT":
      return (
        <Row>
          <DateTimeInput
            readOnly={isReadOnly}
            name={`evenementSousCategorieComplement[${index}].valueParam`}
            label={element.dynamicFormParametreLibelle}
            required={element.dynamicFormParametreIsRequired}
            defaultValue={
              values?.evenementSousCategorieComplement?.[index]?.valueParam
            }
            onChange={(e) => {
              updateParams(e.target.value);
            }}
          />
        </Row>
      );

    case "TEXT_INPUT":
      return (
        <Row>
          <TextInput
            readOnly={isReadOnly}
            name={`evenementSousCategorieComplement[${index}].valueParam`}
            required={element.dynamicFormParametreIsRequired}
            label={element.dynamicFormParametreLibelle}
            onChange={(e) => {
              updateParams(e.target.value);
            }}
          />
        </Row>
      );

    case "NUMBER_INPUT":
      return (
        <Row>
          <NumberInput
            readOnly={isReadOnly}
            name={`evenementSousCategorieComplement[${index}].valueParam`}
            label={element.dynamicFormParametreLibelle}
            required={element.dynamicFormParametreIsRequired}
            onChange={(e) => {
              updateParams(e.target.value);
            }}
          />
        </Row>
      );

    case "SELECT_INPUT":
      return (
        <Row>
          <SelectInput
            name={`evenementSousCategorieComplement[${index}].valueParam`}
            readOnly={isReadOnly}
            label={element.dynamicFormParametreLibelle}
            required={element.dynamicFormParametreIsRequired}
            options={element.listeSelectInput}
            getOptionValue={(t) => t.id}
            getOptionLabel={(t) => t.libelle}
            onChange={(e) => {
              setFieldValue(
                element.dynamicFormParametreId,
                element.listeSelectInput?.find(
                  (r: { id: any }) => r.id === e.id,
                )?.id,
              );

              updateParams(e.id);
            }}
            defaultValue={element.listeSelectInput?.find(
              (r: { id: string }) =>
                r.id ===
                values?.evenementSousCategorieComplement?.[index]?.valueParam,
            )}
          />
        </Row>
      );
    default:
      return;
  }
}

export default GenererForm;
