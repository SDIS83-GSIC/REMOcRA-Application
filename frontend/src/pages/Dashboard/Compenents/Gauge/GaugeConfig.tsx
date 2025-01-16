import { Button, Form } from "react-bootstrap";
import { useState } from "react";
import { SelectInput } from "../../../../components/Form/Form.tsx";

const GaugeConfig = (options: any) => {
  const [limits, setLimits] = useState<{ color: string; max: number }[]>(
    options.config.limits || [],
  );

  // Mettre à jour la couleur d'une limite
  const handleColorChange = (index: number, newColor: string) => {
    const updatedLimits = [...limits];
    updatedLimits[index].color = newColor;
    setLimits(updatedLimits);

    const updatedConfig = { ...options.config, limits: updatedLimits };
    options.setConfig(updatedConfig);
  };

  // Mettre à jour la valeur maximale d'une limite
  const handleMaxChange = (index: number, newMax: number) => {
    const updatedLimits = [...limits];
    updatedLimits[index].max = newMax;
    setLimits(updatedLimits);

    const updatedConfig = { ...options.config, limits: updatedLimits };
    options.setConfig(updatedConfig);
  };

  // Ajouter une nouvelle limite
  const addLimit = () => {
    const newLimit = { color: "#000000", max: 0 }; // Valeurs par défaut
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

  // Valeur par défaut de `value`
  const fieldValue =
    options.fieldOptions.find(
      (option: any) => option.value === options.config.value,
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
      {/* Sélection de la valeur initiale */}
      <SelectInput
        required={false}
        name={"value"}
        label="Valeur initiale"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={options.fieldOptions}
      />

      {/* Sélection de la valeur maximale */}
      <SelectInput
        required={false}
        name={"max"}
        label="Valeur maximum"
        onChange={(value) => handleChange("max", value.value)}
        defaultValue={fieldMax}
        options={options.fieldOptions}
      />

      {/* Configuration des limites (couleurs et max) */}
      <Form.Label className="fw-bold mt-2">Limites des sections :</Form.Label>
      {limits.map((limit, index) => (
        <div key={index} style={{ marginBottom: "10px" }}>
          <Form.Control
            type="color"
            value={limit.color}
            onChange={(e) => handleColorChange(index, e.target.value)}
            style={{ width: "100px", height: "40px", marginRight: "10px" }}
          />
          <Form.Control
            type="number"
            min={0}
            step={1}
            max={100}
            required={false}
            value={limit.max}
            onChange={(e) =>
              handleMaxChange(index, parseInt(e.target.value, 10))
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
      {/* Bouton pour ajouter une nouvelle limite */}
      <Button variant={"primary"} className="mt-3" onClick={addLimit}>
        Ajouter une section
      </Button>
    </>
  );
};

export default GaugeConfig;
