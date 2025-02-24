import { object } from "yup";

type ValuesType = {
  modeleCourrierId: string | null;
};

export const getInitialValues = (): ValuesType => ({
  dynamicFormId: null,
});

export const validationSchema = object({});
export const prepareVariables = (values) => ({
  modeleCourrierId: values.dynamicFormId,
  listParametres: Object.entries(values).map((e) => {
    return { nom: e[0], valeur: e[1] };
  }),
});
