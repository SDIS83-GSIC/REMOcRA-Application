import { Formik } from "formik";
import { ReactNode, SetStateAction, useState } from "react";
import { Container } from "react-bootstrap";
import { useLocation, useNavigate } from "react-router-dom";
import { useToastContext } from "../../module/Toast/ToastProvider.tsx";
import { navigateGoBack } from "../../utils/fonctionsUtils.tsx";
import { usePost, usePut } from "../Fetch/useFetch.tsx";

const ResolveReject = (
  onSubmit: any,
  setErrorMessage: (value: SetStateAction<null>) => void,
  successToastMessage = "L'élément a bien été enregistré",
  errorToastMessage = "L'élément n'a pas été enregistré",
  warningToastMessage = "Les informations fournies sont incomplètes ou erronées",
  redirectFn?: any,
  isPartialSuccess: (result: any) => boolean = () => false,
) => {
  const {
    success: successToast,
    warning: warningToast,
    error: errorToast,
  } = useToastContext();

  return {
    onResolve: (result: string) => {
      setErrorMessage(null);
      isPartialSuccess(result)
        ? warningToast(warningToastMessage)
        : successToast(successToastMessage);
      onSubmit?.(result);
      redirectFn?.();
    },
    onReject: async (error: {
      text: () => SetStateAction<null> | PromiseLike<SetStateAction<null>>;
    }) => {
      setErrorMessage(await error.text());
      errorToast(errorToastMessage);
    },
  };
};

/**
 * @param isPartialSuccess Méthode qui prend en parametre les résultats du Submit.
 * Intervient lorsque la requete de Submit remonte un code 200.
 * Permet de définir un cas particulier pour surcharger la définition du toast de réponse :
 * - Si méthode non définie => cas nominal = Toast Success
 * - Si méthode return true => toast Warning
 * - Si méthode return false => toast Success
 */
export const useMyFormik = (
  submitUrl: string,
  isPost: boolean,
  onSubmit: any,
  redirectUrl?: string,
  isMultipartFormData: boolean,
  isPartialSuccess?: (result: any) => boolean,
  successToastMessage?: string | undefined,
  errorToastMessage?: string | undefined,
  warningToastMessage?: string | undefined,
) => {
  const [errorMessage, setErrorMessage] = useState(null);
  const navigate = useNavigate();
  const location = useLocation();
  const redirectFn = redirectUrl
    ? () => {
        // Si on a la page retour, on retourne dans l'état précédent
        // Sinon, on va à la page mentionnée
        navigateGoBack(location, navigate, redirectUrl);
      }
    : null;
  const postState = usePost(
    submitUrl,
    ResolveReject(
      onSubmit,
      setErrorMessage,
      successToastMessage,
      errorToastMessage,
      warningToastMessage,
      redirectFn,
      isPartialSuccess,
    ),
    isMultipartFormData,
  );
  const putState = usePut(
    submitUrl,
    ResolveReject(
      onSubmit,
      setErrorMessage,
      successToastMessage,
      errorToastMessage,
      warningToastMessage,
      redirectFn,
      isPartialSuccess,
    ),
    isMultipartFormData,
  );
  return { submitState: isPost ? postState : putState, errorMessage };
};

type MyFormikProps = {
  children?: ReactNode;
  initialStatus?: object;
  initialValues: object;
  prepareVariables: (...args: any[]) => any;
  validationSchema: object;
  submitUrl: string;
  isPost: boolean;
  redirectUrl?: string;
  onSubmit: (...args: any[]) => any;
  isPartialSuccess?: (...args: any[]) => boolean;
  successToastMessage?: string;
  errorToastMessage?: string;
  warningToastMessage?: string;
  isMultipartFormData?: boolean;
  innerRef?: any;
};

const MyFormik = (props: MyFormikProps) => {
  const { submitState, errorMessage } = useMyFormik(
    props.submitUrl,
    props.isPost,
    props.onSubmit,
    props.redirectUrl,
    props.isMultipartFormData ?? false,
    props.isPartialSuccess,
    props.successToastMessage,
    props.errorToastMessage,
    props.warningToastMessage,
  );

  return (
    <Formik
      innerRef={props.innerRef}
      enableReinitialize={true}
      initialValues={props.initialValues}
      initialStatus={props.initialStatus}
      validationSchema={props.validationSchema}
      onSubmit={async (values, { setSubmitting }) => {
        const variables = props.prepareVariables
          ? props.prepareVariables(values)
          : values;
        try {
          await submitState.run(variables);
        } finally {
          setSubmitting(false);
        }
      }}
    >
      <Container>
        {errorMessage && <div className="text-danger">{errorMessage}</div>}
        {props.children}
      </Container>
    </Formik>
  );
};

export default MyFormik;
