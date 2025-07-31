import { SelectInput, TextInput } from "../../../../components/Form/Form.tsx";

const CounterConfig = (options: any) => {
  // Valeur par défaut de `value`
  const fieldValue =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.value,
    ) || "";

  // Valeur par défaut de `label`
  const fieldLabel = options.config.label || "";

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (fieldName: any, newValue: string) => {
    const updatedConfig = { ...options.config };
    updatedConfig[fieldName] = newValue;

    options.setConfig(updatedConfig);
  };

  // Mettre à jour la valeur `label`
  const handleChangeLabel = (fieldName: any, newValue: string) => {
    const updatedConfig = { ...options.config };
    updatedConfig[fieldName] = newValue;

    options.setConfig(updatedConfig);
  };
  return (
    <>
      <TextInput
        required={false}
        name={"label"}
        label="Libellé"
        onChange={(e) => handleChangeLabel("label", e.target.value)}
        value={fieldLabel}
      />
      <SelectInput
        required={false}
        name={"value"}
        label="Somme de"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />
    </>
  );
};

export default CounterConfig;
