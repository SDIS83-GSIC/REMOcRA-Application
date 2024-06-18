import { ReactNode } from "react";
import { Form } from "react-bootstrap";

const FilterInput = ({
  type,
  name,
  onChange: onChangeCallback,
}: FilterInputType): ReactNode => {
  return (
    <Form.Control
      type={type}
      name={name}
      onChange={(e) => {
        onChangeCallback({ name: name, value: e.target.value });
      }}
    />
  );
};
type FilterInputType = { type: string; name: string; onChange: any };

export default FilterInput;
