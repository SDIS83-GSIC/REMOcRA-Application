import { Form } from "react-bootstrap";

export const CheckBoxInput = ({
  name,
  label,
  onChange: onChangeCallback,
  required = false,
  disabled = false,
  type = "checkbox",
  checked = false,
}: any) => {
  return (
    <Form.Check
      className="form-check form-switch fs-4"
      type={type}
      label={label}
      required={required}
      disabled={disabled}
      onChange={(data) => {
        onChangeCallback({
          name: name,
          value: data.target.checked,
        });
      }}
      checked={checked}
    />
  );
};

export default CheckBoxInput;
