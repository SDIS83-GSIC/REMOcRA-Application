import FormRange from "react-bootstrap/esm/FormRange";

export const SelectInputBar = ({
  name,
  min,
  max,
  step,
  onChange: onChangeCallback,
  required = false,
  disabled = false,
}: any) => {
  return (
    <FormRange
      required={required}
      id={name}
      name={name}
      min={min}
      max={max}
      step={step}
      disabled={disabled}
      onChange={(data) => {
        onChangeCallback({ name: name, value: data.currentTarget.value });
      }}
    />
  );
};

export default SelectInputBar;
