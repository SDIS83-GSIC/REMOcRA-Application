import { SelectInput } from "../../../../components/Form/Form.tsx";

const PieChartConfig = (options: {
  fieldOptions: any[];
  config: { value: any; name: any };
  setConfig: (arg0: any) => void;
}) => {
  // Valeur par défaut de `value`
  const fieldValue =
    options.fieldOptions.find(
      (option) => option.value === options.config.value,
    ) || "";

  // Valeur par défaut de `name`
  const fieldName =
    options.fieldOptions.find(
      (option) => option.value === options.config.name,
    ) || "";

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (fieldName: any, newValue: string) => {
    const updatedConfig = { ...options.config };
    updatedConfig[fieldName as keyof typeof updatedConfig] = newValue;

    options.setConfig(updatedConfig);
  };
  return (
    <>
      <SelectInput
        required={false}
        name={"name"}
        label="Libellé"
        onChange={(value) => handleChange("name", value.value)}
        options={options.fieldOptions}
        defaultValue={fieldName}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />
      <SelectInput
        required={false}
        name={"value"}
        label="Valeur"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
        getOptionLabel={(option: any) => option.value}
        getOptionValue={(option: any) => option.value}
      />
    </>
  );
};

export default PieChartConfig;
