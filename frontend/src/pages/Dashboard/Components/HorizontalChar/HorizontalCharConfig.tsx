import { SelectInput } from "../../../../components/Form/Form.tsx";

const HorizontalCharConfig = (options: any) => {
  // Valeur par défaut de `value`
  const fieldValue =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.value,
    ) || "";

  // Valeur par défaut de `name`
  const fieldName =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.name,
    ) || "";

  // Valeur par défaut de `max`
  const fieldMax =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.max,
    ) || "";

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (fieldName: any, newValue: string) => {
    const updatedConfig = { ...options.config };
    updatedConfig[fieldName] = newValue;

    options.setConfig(updatedConfig);
  };
  return (
    <>
      <SelectInput
        required={false}
        name={"name"}
        label="Libellé"
        onChange={(value) => handleChange("name", value.value)}
        defaultValue={fieldName}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />
      <SelectInput
        required={false}
        name={"value"}
        label="Valeur initiale"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />

      <SelectInput
        required={false}
        name={"max"}
        label="Valeur maximum"
        onChange={(value) => handleChange("max", value.value)}
        defaultValue={fieldMax}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />
    </>
  );
};

export default HorizontalCharConfig;
