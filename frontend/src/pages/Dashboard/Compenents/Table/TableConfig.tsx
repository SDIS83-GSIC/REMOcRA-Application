import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { SelectInput, TextInput } from "../../../../components/Form/Form.tsx";

const TableConfig = (options: any) => {
  // État pour stocker les colonnes configurées
  const [columns, setColumns] = useState<
    Array<{ col: { value: string; name: string } }>
  >(options.config || []);

  // Ajouter un nouveau champ
  const addSelectInput = () => {
    setColumns([...columns, { col: { value: "", name: "" } }]);
  };

  // Supprimer un champ
  const removeSelectInput = (index: number) => {
    const newColumns = columns.filter((_, i) => i !== index);
    setColumns(newColumns);
    options.setConfig(newColumns);
  };

  // Mettre à jour la valeur d'un champ
  const handleSelectChange = (index: number, value: string) => {
    const newColumns = [...columns];
    newColumns[index].col.value = value;
    setColumns(newColumns);
    options.setConfig(newColumns);
  };

  // Mettre à jour le nom d'un champ
  const handleNameChange = (index: number, name: string) => {
    const newColumns = [...columns];
    newColumns[index].col.name = name;
    setColumns(newColumns);
    options.setConfig(newColumns);
  };

  return (
    <Form>
      {columns.map((column, index) => (
        <Form.Group key={index} className="mb-3">
          {/* Champ pour le nom de la colonne */}
          <TextInput
            required={false}
            name={`name-${index}`}
            label="Nom de la colonne"
            onChange={(e) => handleNameChange(index, e.target.value)}
            value={column.col.name}
          />

          {/* Champ pour la valeur de la colonne */}
          <SelectInput
            required={false}
            name={`value-${index}`}
            label="Valeur de la colonne"
            options={options.fieldOptions}
            defaultValue={
              column.col.value
                ? options.fieldOptions.find(
                    (option: { value: string }) =>
                      option.value === column.col.value,
                  )
                : []
            }
            onChange={(selected) =>
              handleSelectChange(index, selected.value || "")
            }
          />

          {/* Bouton pour supprimer la colonne */}
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

      {/* Bouton pour ajouter une nouvelle colonne */}
      <Button variant="primary" onClick={addSelectInput}>
        Ajouter un champ
      </Button>
    </Form>
  );
};

export default TableConfig;
