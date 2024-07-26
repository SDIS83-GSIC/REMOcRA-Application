import { ReactNode, SetStateAction, useState } from "react";
import { Formik } from "formik";
import { useNavigate } from "react-router-dom";
import { usePost, usePut } from "../Fetch/useFetch.tsx";
import ToastAutohide from "../../module/Toast/toast.tsx";

const resolveReject = (
  onSubmit: any,
  setErrorMessage: (value: SetStateAction<null>) => void,
  successToastMessage = "L'élément a bien été enregistré",
  errorToastMessage = "L'élément n'a pas été enregistré",
  redirectFn?: any,
) => {
  return {
    onResolve: (result: string) => {
      setErrorMessage(null);
      <ToastAutohide content={successToastMessage} />;
      onSubmit?.(result);
      redirectFn?.();
    },
    onReject: async (error: {
      text: () => SetStateAction<null> | PromiseLike<SetStateAction<null>>;
    }) => {
      setErrorMessage(await error.text());
      <ToastAutohide content={errorToastMessage} />;
    },
  };
};

const useMyFormik = (
  submitUrl: string,
  isPost: boolean,
  onSubmit: any,
  successToastMessage: string | undefined,
  errorToastMessage: string | undefined,
  redirectUrl: string,
  isMultipartFormData: boolean,
) => {
  const [errorMessage, setErrorMessage] = useState(null);
  const navigate = useNavigate();
  const redirectFn = redirectUrl
    ? () => {
        navigate(redirectUrl);
      }
    : null;
  const postState = usePost(
    submitUrl,
    resolveReject(
      onSubmit,
      setErrorMessage,
      successToastMessage,
      errorToastMessage,
      redirectFn,
    ),
    isMultipartFormData,
  );
  const putState = usePut(
    submitUrl,
    resolveReject(
      onSubmit,
      setErrorMessage,
      successToastMessage,
      errorToastMessage,
      redirectFn,
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
  redirectUrl: string;
  onSubmit: (...args: any[]) => any;
  successToastMessage?: string;
  errorToastMessage?: string;
  isMultipartFormData?: boolean;
};

const MyFormik = (props: MyFormikProps) => {
  const { submitState, errorMessage } = useMyFormik(
    props.submitUrl,
    props.isPost,
    props.onSubmit,
    props.successToastMessage,
    props.errorToastMessage,
    props.redirectUrl,
    props.isMultipartFormData ?? false,
  );

  return (
    <Formik
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
      <>
        {errorMessage && errorMessage}
        {props.children}
      </>
    </Formik>
  );
};

export default MyFormik;
