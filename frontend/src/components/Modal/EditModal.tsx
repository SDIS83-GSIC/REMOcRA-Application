import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import Modal from "react-bootstrap/Modal";
import MyFormik from "../Form/MyFormik.tsx";
import { FormContainer } from "../Form/Form.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import ToastAutohide from "../../module/Toast/ToastAutoHide.tsx";
import Loading from "../Elements/Loading/Loading.tsx";

const EditModalBody = ({
  prepareVariables,
  getInitialValues,
  query,
  id,
  validationSchema,
  canModify,
  onSubmit,
  closeModal,
  children,
  submitLabel,
  value,
  isMultipartFormData = false,
}: EditModalBodyType) => {
  const isAdd = id == null;
  const submitUrl = query + "/" + (id ? id : "");
  let initialData = {};

  if (!isAdd) {
    // eslint-disable-next-line react-hooks/rules-of-hooks
    const editState = useGet(submitUrl, {});
    const { isLoading, isResolved, data = {} } = editState;

    if (isLoading) {
      return <Loading />;
    }

    if (!isResolved) {
      closeModal(); // FIXME : mettre un message d'erreur ?
    }

    if (!data) {
      ToastAutohide({
        content: `Une erreur est survenue lors de la récupération des données.`,
        variant: "danger",
      });
      closeModal();
    }
    initialData = data;
  }

  if (isAdd && value) {
    initialData = value;
  }

  return (
    <MyFormik
      validationSchema={validationSchema}
      isPost={isAdd}
      initialValues={
        getInitialValues ? getInitialValues(initialData) : initialData
      }
      submitUrl={submitUrl}
      onSubmit={(values: any) => {
        onSubmit && onSubmit(values);
        closeModal();
      }}
      prepareVariables={(values: any) => prepareVariables(values)}
      isMultipartFormData={isMultipartFormData}
    >
      <FormContainer noValidate>
        <Modal.Body>{children}</Modal.Body>
        <Modal.Footer>
          {canModify ? (
            <>
              <Button
                variant="secondary"
                onClick={(e) => {
                  e.preventDefault();
                  closeModal();
                }}
              >
                Annuler
              </Button>
              <Button variant="primary" type="submit">
                {submitLabel}
              </Button>
            </>
          ) : (
            <Button variant="secondary" onClick={closeModal}>
              Fermer
            </Button>
          )}
        </Modal.Footer>
      </FormContainer>
    </MyFormik>
  );
};

const EditModal = ({
  visible,
  closeModal,
  header,
  query,
  id,
  children,
  validationSchema,
  onSubmit,
  prepareVariables = (values: any) => ({ values }),
  getInitialValues,
  canModify = true,
  submitLabel = "Valider",
  value = {},
  isMultipartFormData = false,
}: EditModalBodyType & {
  visible: boolean;
  header: ReactNode;
}) => {
  return (
    <Modal show={visible} onHide={closeModal} size={"xl"} backdrop={false}>
      <Modal.Header>
        <Modal.Title>{header}</Modal.Title>
      </Modal.Header>
      <EditModalBody
        canModify={canModify}
        query={query}
        id={id}
        validationSchema={validationSchema}
        onSubmit={onSubmit}
        closeModal={closeModal}
        prepareVariables={prepareVariables}
        getInitialValues={getInitialValues}
        submitLabel={submitLabel}
        value={value}
        isMultipartFormData={isMultipartFormData}
      >
        {children}
      </EditModalBody>
    </Modal>
  );
};

type EditModalBodyType = {
  closeModal: () => void;
  id?: string;
  children: ReactNode;
  onSubmit: (values: any) => void;
  validationSchema: object;
  prepareVariables: (values: any) => any;
  getInitialValues: (values: any) => any;
  canModify: boolean;
  query: string;
  submitLabel: string;
  value?: object;
  isMultipartFormData?: boolean;
};

export default EditModal;
