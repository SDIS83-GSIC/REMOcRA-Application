import classNames from "classnames";
import { Field, Form as FormikForm, useField } from "formik";
import { ReactNode } from "react";
import { Row } from "react-bootstrap";
import { Typeahead } from "react-bootstrap-typeahead";
import Form from "react-bootstrap/Form";
import FormRange from "react-bootstrap/esm/FormRange";
import ReactSelect from "react-select";
import { SelectFilterFromUrlType } from "../../utils/typeUtils.tsx";
import Loading from "../Elements/Loading/Loading.tsx";
import { useGet } from "../Fetch/useFetch.tsx";
import { IconInfo } from "../Icon/Icon.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import "./form.css";

type InputType = {
  name: string;
  label?: string | ReactNode;
  required?: boolean;
  readOnly?: boolean;
  value?: string | any;
  disabled?: boolean;
  placeholder?: string;
  tooltipText?: string;
  password?: boolean;
  onChange?: (...args: any[]) => void;
  onBlur?: (...args: any[]) => void;
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
  onChange,
  onBlur,
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
        onBlur={(v: string) => (onBlur ? onBlur(v) : field.onBlur(v))}
        onChange={(v: string) => (onChange ? onChange(v) : field.onChange(v))}
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
  onChange,
  rows = 3,
}: InputType & { rows?: number }) => {
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
        rows={rows}
        readOnly={readOnly}
        disabled={disabled}
        defaultValue={value ?? ""}
        {...field}
        onChange={(v: string) => (onChange ? onChange(v) : field.onChange(v))}
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
  tooltipText?: string | ReactNode;
  onChange?: (...args: any[]) => void;
};

export const CheckBoxInput = ({
  name,
  label,
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
      <label
        htmlFor={name}
        className={classNames("mx-2 mt-2", disabled ? "text-muted" : "")}
      >
        {label}
        {tooltipText && (
          <TooltipCustom tooltipText={tooltipText} tooltipId={name}>
            <IconInfo />
          </TooltipCustom>
        )}
      </label>
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
  multiple?: boolean;
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
  multiple = false,
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
        multiple={multiple}
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
  min,
  step,
  max,
  ...rest
}: InputType & {
  min?: number;
  step?: number;
  max?: number;
}) => {
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
        min={min ?? 1}
        step={step ?? 0.1}
        readOnly={readOnly}
        defaultValue={value ?? ""}
        max={max ?? undefined}
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
    if (
      (characterNumber >= 0 && characterNumber <= 9) ||
      characterCode === "." ||
      characterCode === ","
    ) {
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

type DateType = InputType & {
  dateType?: "datetime-local" | "date";
};

export const DateInput = (props: InputType) => (
  <DateTimeInput dateType="date" {...props} />
);

type RangeInputType = {
  name: string;
  label: string | ReactNode;
  min: number;
  max: number;
  step: number;
  value?: number;
  required?: boolean;
  disabled?: boolean;
  tooltipText?: string;
};

export const RangeInput = ({
  name,
  label,
  min,
  max,
  step,
  required = false,
  disabled = false,
  tooltipText,
}: RangeInputType) => {
  const [field, meta] = useField(name);
  const error = meta.touched ? meta.error : null;

  return (
    <DivWithError name={name} error={error}>
      <FormLabel
        className="p-1"
        label={label}
        required={required}
        disabled={disabled}
        tooltipText={tooltipText}
        name={name}
      />
      <FormRange
        {...field}
        id={name}
        name={name}
        min={min}
        max={max}
        step={step}
        disabled={disabled}
      />
    </DivWithError>
  );
};

export const DateTimeInput = ({
  name,
  label,
  required = true,
  readOnly = false,
  tooltipText,
  dateType = "datetime-local",
  value,
  ...rest
}: DateType) => {
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
          type={dateType}
          required={required}
          {...field}
          {...rest}
          disabled={readOnly}
          value={value}
          max="9999-12-31T23:59"
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
  noOptionsMessage?: string;
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
        noOptionsMessage={() => "Aucune donnée trouvée"}
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
  disabled = false,
  noOptionsMessage = "Aucune donnée trouvée",
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
        isDisabled={readOnly || disabled}
        name={name}
        noOptionsMessage={() => noOptionsMessage}
      />
    </DivWithError>
  );
};

export const AsyncTypeahead = ({
  onChange,
  url,
  disabled = false,
  labelKey = "libelle",
}: SelectFilterFromUrlType) => {
  const stateData = useGet(url);

  const {
    isResolved: isResolvedListData,
    // eslint-disable-next-line no-empty-pattern
    data: listData = ([] = {}),
  } = stateData;

  if (!isResolvedListData) {
    return <Loading />;
  } else {
    return (
      <Typeahead
        className="d-flex"
        placeholder={"Sélectionnez..."}
        size={"sm"}
        options={listData}
        labelKey={labelKey}
        onChange={(data) => {
          onChange(data && data[0]);
        }}
        defaultSelected={[]}
        clearButton
        disabled={disabled}
      />
    );
  }
};

export const FieldSet = ({
  title,
  children,
}: {
  title?: ReactNode;
  children: ReactNode;
}) => {
  return (
    <fieldset>
      {title && <legend>{title}</legend>}
      {children}
    </fieldset>
  );
};
