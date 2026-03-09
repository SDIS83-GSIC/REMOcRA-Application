import { object } from "yup";
import { useToastContext } from "../../module/Toast/ToastProvider";
import { DynamicFormParametreFront } from "../../utils/buildDynamicForm";

type ValuesType = {
  modeleCourrierId: string | null;
};

export const getInitialValues = (): ValuesType => ({
  dynamicFormId: null,
});

export const validationSchema = object({});
export const prepareVariables = (
  values: {
    [x: string]: string;
    dynamicFormId: string;
    courrierReference: string;
  },
  listeParametres: DynamicFormParametreFront[],
) => {
  const listeParametre = listeParametres.map((param) => {
    const code = param.dynamicFormParametreCode;
    const value =
      values[code] !== undefined
        ? values[code]
        : (param.dynamicFormParametreValeurDefaut ?? null);

    return {
      nom: code,
      valeur: value,
      estRequis: param.dynamicFormParametreIsRequired,
    };
  });

  return {
    modeleCourrierId: values.dynamicFormId,
    courrierReference: values.courrierReference,
    listParametres: listeParametre,
  };
};
