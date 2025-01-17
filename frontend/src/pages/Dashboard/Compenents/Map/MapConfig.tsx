import { useState } from "react";
import { Button, Form } from "react-bootstrap";
import { SelectInput } from "../../../../components/Form/Form.tsx";

const MapConfig = (options: any) => {
  // Valeur par défaut de `geojson`
  const fieldGeojson =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.geojson,
    ) || "";

  // Valeur par défaut de `geojson`
  const fieldValue =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.value,
    ) || "";

  // Valeur par défaut de `geojson`
  const fieldMax =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.max,
    ) || "";

  // Gérer les limites dynamiquement
  const [limits, setLimits] = useState(options.config.limits || []);

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (fieldName: string, newValue: string) => {
    const updatedConfig = { ...options.config, [fieldName]: newValue };
    options.setConfig(updatedConfig);
  };

  // Mettre à jour les limites
  const handleLimitsChange = (index: number, field: string, value: string) => {
    const updatedLimits = limits.map((limit: any, i: number) =>
      i === index ? { ...limit, [field]: value } : limit,
    );
    setLimits(updatedLimits);

    const updatedConfig = { ...options.config, limits: updatedLimits };
    options.setConfig(updatedConfig);
  };

  // Ajouter une nouvelle limite
  const addLimit = () => {
    const newLimit = { color: "#000000", value: "" };
    const updatedLimits = [...limits, newLimit];
    setLimits(updatedLimits);

    const updatedConfig = { ...options.config, limits: updatedLimits };
    options.setConfig(updatedConfig);
  };

  // Supprimer une limite
  const removeLimit = (index: number) => {
    const updatedLimits = limits.filter((_: any, i: number) => i !== index);
    setLimits(updatedLimits);

    const updatedConfig = { ...options.config, limits: updatedLimits };
    options.setConfig(updatedConfig);
  };

  return (
    <>
      {/* Champ pour `geojson` */}
      <SelectInput
        required={false}
        name={"geojson"}
        label="Valeur géométrique"
        onChange={(value) => handleChange("geojson", value.value)}
        defaultValue={fieldGeojson}
        options={options.fieldOptions}
      />

      {/* Champ pour `value` */}
      <SelectInput
        required={false}
        name={"value"}
        label="Valeur de référence"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
      />

      {/* Champ pour `max` */}
      <SelectInput
        required={false}
        name={"max"}
        label="Valeur maximale"
        onChange={(value) => handleChange("max", value.value)}
        defaultValue={fieldMax}
        options={options.fieldOptions}
      />

      <hr />
      {/* Champ pour `limits` */}
      <div>
        <Form.Label className="fw-bold mt-2">
          Couleur du pourcentage :
        </Form.Label>
        <br />
        {limits.map((limit: any, index: number) => (
          <div key={index} style={{ marginBottom: "10px" }}>
            <Form>
              <Form.Control
                type="color"
                value={limit.color}
                onChange={(e) =>
                  handleLimitsChange(index, "color", e.target.value)
                }
                style={{ width: "100px", height: "40px", marginRight: "10px" }}
              />
            </Form>
            <Form.Control
              type="number"
              min={0}
              step={1}
              max={100}
              required={false}
              value={limit.value}
              onChange={(e) =>
                handleLimitsChange(index, "value", e.target.value)
              }
            />
            <Button
              variant={"warning"}
              className="mt-3"
              onClick={() => removeLimit(index)}
            >
              Supprimer
            </Button>
          </div>
        ))}
        <Button variant={"primary"} className="mt-3" onClick={addLimit}>
          Ajouter une limite
        </Button>
      </div>
    </>
  );
};

export default MapConfig;
