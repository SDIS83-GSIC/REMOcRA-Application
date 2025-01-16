import { SelectInput } from "../../../../components/Form/Form.tsx";

const MapConfig = (options: any) => {
  // Valeur par défaut de `geojson`
  const fieldValue =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.geojson,
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
        name={"geojson"}
        label="Valeur géométrique"
        onChange={(value) => handleChange("geojson", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
      />
    </>
  );
};

export default MapConfig;
