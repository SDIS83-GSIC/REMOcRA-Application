import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { SelectInput } from "../../../../components/Form/Form.tsx";

const TableConfig = (options: any) => {
  const [selectValues, setSelectValues] = useState<string[]>(
    options.config || [],
  );

  // Ajouter un nouveau champ
  const addSelectInput = () => {
    setSelectValues([...selectValues, ""]);
  };

  // Supprimer un champ
  const removeSelectInput = (index: number) => {
    const newValues = selectValues.filter((_, i) => i !== index);

    setSelectValues(newValues);
    options.setConfig(newValues);
  };

  // Mettre à jour la valeur d'un champ
  const handleSelectChange = (index: number, value: string) => {
    const newValues = [...selectValues];
    newValues[index] = value;
    setSelectValues(newValues);
    options.setConfig(newValues);
  };

  return (
    <Form>
      {selectValues.map((value, index) => (
        <Form.Group key={index} className="mb-3">
          <SelectInput
            required={false}
            name="value"
            label="Nom de la colonne"
            options={options.fieldOptions}
            defaultValue={value ? [{ value, label: value }] : []}
            onChange={(value) => handleSelectChange(index, value.value || "")}
          />
          <Button
            variant="danger"
            size="sm"
            onClick={() => removeSelectInput(index)}
            className="mt-2"
          >
            Supprimer
          </Button>
        </Form.Group>
      ))}
      <Button variant="primary" onClick={addSelectInput}>
        Ajouter un champ
      </Button>
    </Form>
  );
};

export default TableConfig;
