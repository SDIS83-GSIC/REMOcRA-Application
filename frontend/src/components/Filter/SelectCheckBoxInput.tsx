import { Form } from "react-bootstrap";

export const CheckBoxInput = ({
  name,
  label,
  onChange: onChangeCallback,
  required = false,
  disabled = false,
}: any) => {
  return (
    <Form.Check
      type="checkbox"
      label={label}
      required={required}
      disabled={disabled}
      onChange={(data) => {
        onChangeCallback({
          name: name,
          value: data.target.checked,
        });
      }}
    />
  );
};

export default CheckBoxInput;
