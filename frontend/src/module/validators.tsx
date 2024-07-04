import {
  string,
  array,
  number as numberYup,
  bool as boolYup,
  mixed as mixedYup,
  date as dateYup,
} from "yup";
import { AnyObject } from "yup/lib/types";

function requiredIf(test: boolean, validator: AnyObject) {
  return test ? validator.required("Ce champ est requis.") : validator;
}

export const requiredString = requiredIf(true, string());
export const requiredEmail = requiredIf(
  true,
  string().email("Format de l'adresse e-mail non valide."),
);

export const number = numberYup().typeError("Ce champ doit être un nombre.");
export const requiredNumber = requiredIf(true, number);

export const boolean = boolYup().typeError("Ce champ doit être un booléen.");
export const requiredBoolean = requiredIf(true, boolean);

export const numberPositif = number.min(
  0,
  "Ce champ doit être supérieur ou égal à 0.",
);

export const intPositif = numberPositif.integer("Ce champ doit être un entier");

export const percentage = numberPositif.max(
  100,
  "Ce champ doit inférieur ou égal à 100.",
);
export const requiredPercentage = requiredIf(true, percentage);

export const requiredArray = requiredIf(
  true,
  array().ensure().min(1, "Ce champ doit comporter au moins 1 valeur."),
);

export const date = dateYup().typeError("Format de date invalide");
export const requiredDate = requiredIf(true, date);

export const requiredFile = mixedYup().required("Fichier requis");
