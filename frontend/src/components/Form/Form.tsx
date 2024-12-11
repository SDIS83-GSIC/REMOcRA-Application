import { Field, Form as FormikForm, useField } from "formik";
import { ReactNode } from "react";
import { Row } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import ReactSelect from "react-select";
import { IconInfo } from "../Icon/Icon.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";

type InputType = {
  name: string;
  label?: string;
  required?: boolean;
  readOnly?: boolean;
  value?: string;
  disabled?: boolean;
  placeholder?: string;
  tooltipText?: string;
  password?: boolean;
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
  name,
  label,
  className,
  required = true,
  disabled = false,
  tooltipText = undefined,
}: {
  name: string;
  label?: string;
  className?: string;
  required?: boolean;
  disabled?: boolean;
  tooltipText?: string;
}) => {
  return (
    label && (
      <>
        <Form.Label
          htmlFor={name}
          className={
            !disabled
              ? "fw-bold mt-2 " + className
              : "text-muted mt-2 " + className
          }
        >
          {label} {required === true && <span className="text-danger">*</span>}
        </Form.Label>
        {tooltipText && (
          <TooltipCustom tooltipText={tooltipText} tooltipId={name}>
            <IconInfo />
          </TooltipCustom>
        )}
      </>
    )
  );
};

export const TextInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  disabled = false,
  value,
  placeholder,
  tooltipText,
  password = false,
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <Form.Control
        id={name}
        required={required}
        type={password ? "password" : "text"}
        readOnly={readOnly}
        disabled={disabled}
        defaultValue={value ?? ""}
        placeholder={placeholder}
        {...field}
      />
      {password && (
        <button
          type="button"
          style={{
            position: "absolute",
            right: "10px",
            top: "50%",
            transform: "translateY(-50%)",
            background: "none",
            border: "none",
            cursor: "pointer",
            padding: "0",
          }}
        />
      )}
    </DivWithError>
  );
};

export const TextAreaInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  disabled = false,
  value,
  tooltipText,
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <Form.Control
        id={name}
        required={required}
        as="textarea"
        rows={3}
        readOnly={readOnly}
        disabled={disabled}
        defaultValue={value ?? ""}
        {...field}
      />
    </DivWithError>
  );
};

type CheckBoxInputType = {
  name: string;
  label: string | ReactNode;
  required?: boolean;
  disabled?: boolean;
  checked?: boolean;
  tooltipText?: string;
  onChange?: (...args: any[]) => void;
};

export const CheckBoxInput = ({
  name,
  label,
  required = false,
  disabled = false,
  checked = false,
  tooltipText,
  onChange,
}: CheckBoxInputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <Field
        id={name}
        name={name}
        type="checkbox"
        disabled={disabled}
        onChange={(v: boolean) => (onChange ? onChange(v) : field.onChange(v))}
        checked={field.value ?? checked}
      />
      <FormLabel
        className="p-1"
        label={label}
        required={required}
        disabled={disabled}
        tooltipText={tooltipText}
        name={name}
      />
    </DivWithError>
  );
};

type RadioInputType = {
  name: string;
  label: ReactNode;
  value: string;
  required?: boolean;
  disabled?: boolean;
  tooltipText?: string;
};

export const RadioInput = ({
  name,
  label,
  value,
  required = false,
  disabled = false,
  tooltipText,
}: RadioInputType) => {
  const [, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <Field name={name} type="radio" value={value} disabled={disabled} />
      <FormLabel
        className="p-1"
        label={label}
        required={required}
        disabled={disabled}
        tooltipText={tooltipText}
        name={name}
      />
    </DivWithError>
  );
};

type FileInputType = InputType & {
  accept: string;
  onChange: (e) => any;
};

export const FileInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  disabled = false,
  accept,
  onChange,
  tooltipText,
}: FileInputType) => {
  const [, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <Form.Control
        id={name}
        required={required}
        type="file"
        accept={accept}
        readOnly={readOnly}
        disabled={disabled}
        onChange={onChange}
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
  tooltipText,
  ...rest
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <Form.Control
        id={name}
        type="number"
        min={rest.min ?? 1}
        step={rest.step ?? 0.1}
        readOnly={readOnly}
        defaultValue={value ?? ""}
        {...field}
        {...rest}
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
  tooltipText,
  ...rest
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
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <Form.Control
        id={name}
        type="number"
        onKeyDown={handleKeypress}
        onBlur={handleBlur}
        min={rest.min ?? 1}
        step={rest.step ?? 1}
        readOnly={readOnly}
        defaultValue={value ?? ""}
        required={required}
        {...field}
        {...rest}
      />
    </DivWithError>
  );
};

export default PositiveNumberInput;

export const DateTimeInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  tooltipText,
  ...rest
}: InputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;
  return (
    <DivWithError name={name} error={error}>
      <Row>
        <FormLabel
          label={label}
          required={required}
          className={"me-2"}
          tooltipText={tooltipText}
          name={name}
        />
      </Row>
      <Row className="mx-1">
        <input
          id={name}
          type="datetime-local"
          required={required}
          {...field}
          {...rest}
          disabled={readOnly}
        />
      </Row>
    </DivWithError>
  );
};

type MultiselectType = InputType & {
  options: any[];
  getOptionValue: (e: any) => string;
  getOptionLabel: (e: any) => string;
  isClearable?: boolean;
  defaultValue?: any;
  onChange: (e: any) => any;
  tooltipText?: string;
};

export const Multiselect = ({
  name,
  label,
  options,
  getOptionValue,
  getOptionLabel,
  value,
  isClearable = false,
  required = true,
  readOnly = false,
  onChange,
  tooltipText,
}: MultiselectType) => {
  const [, meta] = useField(name);
  const error = meta.touched ? meta.error : null;

  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <ReactSelect
        id={name}
        isMulti={true}
        placeholder={"Sélectionnez"}
        name={name}
        options={options}
        getOptionValue={getOptionValue}
        getOptionLabel={getOptionLabel}
        isClearable={isClearable}
        value={value}
        onChange={onChange}
        isDisabled={readOnly}
      />
    </DivWithError>
  );
};

export const SelectInput = ({
  name,
  label,
  options,
  getOptionValue,
  getOptionLabel,
  isClearable = false,
  required = true,
  readOnly = false,
  defaultValue,
  tooltipText,
  onChange,
}: MultiselectType) => {
  const [, meta] = useField(name);
  const error = meta.touched ? meta.error : null;

  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        label={label}
        required={required}
        tooltipText={tooltipText}
        name={name}
      />
      <ReactSelect
        id={name}
        isMulti={false}
        placeholder={"Sélectionnez"}
        options={options}
        getOptionValue={getOptionValue}
        getOptionLabel={getOptionLabel}
        isClearable={isClearable}
        value={defaultValue}
        onChange={onChange}
        required={required}
        isDisabled={readOnly}
        name={name}
      />
    </DivWithError>
  );
};
