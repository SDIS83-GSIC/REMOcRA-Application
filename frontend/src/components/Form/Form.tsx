import { ReactNode } from "react";
import Form from "react-bootstrap/Form";
import { Form as FormikForm, useField } from "formik";

type InputType = {
  name: string;
  label: string;
  required?: boolean;
  readOnly?: boolean;
  value?: string;
};

export const FormContainer = (props: any) => <FormikForm {...props} />;

export const DivWithError = ({
  error,
  innerRef,
  children,
  ...rest
}: DivWithErrorType) => (
  <div {...rest} ref={innerRef}>
    {children}
    <div className="text-danger">{error}</div>
  </div>
);

type DivWithErrorType = {
  name: string;
  className?: string;
  error?: string | null;
  innerRef?: (...args: any[]) => any;
  children: ReactNode;
};

export const FormLabel = ({
  label,
  required = true,
}: {
  label: string;
  required?: boolean;
}) => {
  return (
    <Form.Label className="fw-bold">
      {label} {required === true && <span className="text-danger">*</span>}
    </Form.Label>
  );
};

export const TextInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  value,
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel label={label} required={required} />
      <Form.Control
        required={required}
        type="text"
        readOnly={readOnly}
        defaultValue={value ?? ""}
        {...field}
      />
    </DivWithError>
  );
};

export const TextAreaInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  value,
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel label={label} required={required} />
      <Form.Control
        required={required}
        as="textarea"
        rows={3}
        readOnly={readOnly}
        defaultValue={value ?? ""}
        {...field}
      />
    </DivWithError>
  );
};

type CheckBoxInputType = {
  name: string;
  label: string;
  defaultCheck?: boolean;
};

export const CheckBoxInput = ({
  name,
  label,
  defaultCheck = false,
}: CheckBoxInputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <Form.Check
        type="checkbox"
        label={label}
        defaultChecked={defaultCheck}
        {...field}
      />
    </DivWithError>
  );
};

type FileInputType = InputType & {
  accept: string;
};

export const FileInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  accept,
}: FileInputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel label={label} required={required} />
      <Form.Control
        required={required}
        type="file"
        accept={accept}
        readOnly={readOnly}
        {...field}
      />
    </DivWithError>
  );
};

export const NumberInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  value,
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel label={label} required={required} />
      <Form.Control
        type="number"
        min={1}
        step={0.1}
        readOnly={readOnly}
        defaultValue={value ?? ""}
        {...field}
      />
    </DivWithError>
  );
};

const PositiveNumberInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  value,
}: InputType) => {
  const handleBlur = (e: React.FormEvent<EventTarget>) => {
    if (e.currentTarget.value === "0") {
      e.currentTarget.value = "1";
    }
  };

  const handleKeypress = (e: React.FormEvent<EventTarget>) => {
    const characterCode = e.key;
    if (characterCode === "Backspace") {
      return;
    }

    const characterNumber = Number(characterCode);
    if (characterNumber >= 0 && characterNumber <= 9) {
      if (e.currentTarget.value && e.currentTarget.value.length) {
        return;
      } else if (characterNumber === 0) {
        e.preventDefault();
      }
    } else {
      e.preventDefault();
    }
  };

  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel label={label} required={required} />
      <Form.Control
        type="number"
        onKeyDown={handleKeypress}
        onBlur={handleBlur}
        min={1}
        step={1}
        readOnly={readOnly}
        defaultValue={value ?? ""}
        {...field}
      />
    </DivWithError>
  );
};

export default PositiveNumberInput;
