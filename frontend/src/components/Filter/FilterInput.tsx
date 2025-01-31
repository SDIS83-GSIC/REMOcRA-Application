import { ReactNode } from "react";
import { Form } from "react-bootstrap";

const FilterInput = ({
  type,
  name,
  onChange: onChangeCallback,
  value,
}: FilterInputType): ReactNode => {
  return (
    <Form.Control
      type={type}
      id={name}
      name={name}
      value={value}
      onChange={(e) => {
        onChangeCallback({ name: name, value: e.target.value });
      }}
    />
  );
};
type FilterInputType = {
  type: string;
  name: string;
  onChange?: any;
  value?: any;
};

export default FilterInput;
