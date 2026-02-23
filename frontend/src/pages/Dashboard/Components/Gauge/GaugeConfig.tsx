import { useEffect, useState } from "react";
import { Button, Form } from "react-bootstrap";
import { SelectInput } from "../../../../components/Form/Form.tsx";
import { IconDelete } from "../../../../components/Icon/Icon.tsx";

interface Limit {
  color: string;
  max: number;
}

interface GaugeConfigData {
  value?: string;
  max?: string;
  colorMode?: string;
  barColor?: string;
  highColor?: string;
  limits?: Limit[];
}

interface OptionsList {
  value: string;
}

interface GaugeConfigProps {
  config: GaugeConfigData;
  setConfig: (config: GaugeConfigData) => void;
  fieldOptions: OptionsList[];
}

const GaugeConfig = ({ config, setConfig, fieldOptions }: GaugeConfigProps) => {
  const [limits, setLimits] = useState<Limit[]>(config.limits || []);

  // Mettre à jour la couleur d'une limite
  const handleColorChange = (index: number, newColor: string) => {
    const updatedLimits = [...limits];
    updatedLimits[index].color = newColor;
    setLimits(updatedLimits);

    const updatedConfig = { ...config, limits: updatedLimits };
    setConfig(updatedConfig);
  };

  // Mettre à jour la valeur maximale d'une limite
  const handleMaxChange = (index: number, newMax: number) => {
    const updatedLimits = [...limits];
    updatedLimits[index].max = newMax;
    setLimits(updatedLimits);

    const updatedConfig = { ...config, limits: updatedLimits };
    setConfig(updatedConfig);
  };

  // Ajouter une nouvelle limite
  const addLimit = () => {
    const newLimit = { color: "#000000", max: 0 }; // Valeurs par défaut
    const updatedLimits = [...limits, newLimit];
    setLimits(updatedLimits);

    const updatedConfig = { ...config, limits: updatedLimits };
    setConfig(updatedConfig);
  };

  // Supprimer une limite
  const removeLimit = (index: number) => {
    const updatedLimits = limits.filter((_: Limit, i: number) => i !== index);
    setLimits(updatedLimits);

    const updatedConfig = { ...config, limits: updatedLimits };
    setConfig(updatedConfig);
  };

  // Valeur par défaut de `value`
  const fieldValue =
    fieldOptions.find((option: OptionsList) => option.value === config.value) ||
    "";

  // Valeur par défaut de `max`
  const fieldMax =
    fieldOptions.find((option: OptionsList) => option.value === config.max) ||
    "";

  // Mettre à jour la config du composant avec la nouvelle valeur
  const handleChange = (fieldName: keyof GaugeConfigData, newValue: any) => {
    const updatedConfig = { ...config };
    updatedConfig[fieldName] = newValue;

    setConfig(updatedConfig);
  };

  // Mode gradient detection & defaults
  const isGradient =
    config.colorMode === "gradient" ||
    (config.colorMode == null && (config.limits?.length || 0) > 0);

  const defaultLimits: Limit[] = [
    { color: "#2ecc71", max: 25 },
    { color: "#f1c40f", max: 50 },
    { color: "#e67e22", max: 75 },
  ];

  useEffect(() => {
    if (isGradient && limits.length === 0) {
      setLimits(defaultLimits);
      const updatedConfig = {
        ...config,
        limits: defaultLimits,
        highColor: config.highColor || "#e74c3c",
      };
      setConfig(updatedConfig);
    }
  }, [isGradient, config.highColor, setConfig, config, limits.length]);

  return (
    <>
      {/* Sélection de la valeur initiale */}
      <SelectInput
        required={false}
        name={"value"}
        label="Numérateur"
        onChange={(value) => handleChange("value", value.value)}
        defaultValue={fieldValue}
        options={fieldOptions}
        getOptionLabel={(option: OptionsList) => option.value}
        getOptionValue={(option: OptionsList) => option.value}
      />

      {/* Sélection de la valeur maximale */}
      <SelectInput
        required={false}
        name={"max"}
        label="Dénominateur"
        onChange={(value) => handleChange("max", value.value)}
        defaultValue={fieldMax}
        options={fieldOptions}
        getOptionLabel={(option: OptionsList) => option.value}
        getOptionValue={(option: OptionsList) => option.value}
      />

      {/* Mode de couleur: Couleur unie vs Dégradé de couleurs */}
      <Form.Label className="fw-bold mt-3">Mode de couleur :</Form.Label>
      <div>
        <Form.Check
          type="radio"
          name="colorMode"
          id="gauge-colorMode-solid"
          label="Couleur unie"
          checked={!isGradient}
          onChange={() => handleChange("colorMode", "solid")}
        />
        <Form.Check
          type="radio"
          name="colorMode"
          id="gauge-colorMode-gradient"
          label="Dégradé de couleurs"
          checked={isGradient}
          onChange={() => handleChange("colorMode", "gradient")}
        />
      </div>

      {/* Couleur des barres (solide) */}
      {!isGradient && (
        <>
          <Form.Label className="fw-bold mt-3">
            Couleur de la jauge :
          </Form.Label>
          <div style={{ marginBottom: "10px" }}>
            <Form.Control
              type="color"
              defaultValue={config.barColor || "#8884d8"}
              onBlur={(e) => handleChange("barColor", e.target.value)}
              style={{ width: "100px", height: "40px" }}
            />
          </div>
        </>
      )}

      {/* Configuration des limites (gradient) */}
      {isGradient && (
        <>
          <Form.Label className="fw-bold mt-2">
            Paliers des sections :
          </Form.Label>
          <br />
          {limits.map((limit, index) => (
            <div
              key={index}
              style={{
                marginBottom: "10px",
                display: "flex",
                alignItems: "center",
              }}
            >
              <Form.Control
                type="color"
                defaultValue={limit.color}
                onBlur={(e) => handleColorChange(index, e.target.value)}
                style={{ width: "40px", marginRight: "10px" }}
              />
              <Form.Control
                type="number"
                min={0}
                step={1}
                max={100}
                disabled
                value={index === 0 ? 0 : (limits[index - 1]?.max ?? 0)}
                style={{ width: "100px", marginRight: "10px" }}
              />
              <Form.Control
                type="number"
                min={0}
                step={1}
                max={100}
                required={false}
                defaultValue={limit.max}
                onBlur={(e) =>
                  handleMaxChange(index, parseInt(e.target.value, 10))
                }
                style={{ width: "100px", marginRight: "10px" }}
              />
              <Button variant={"warning"} onClick={() => removeLimit(index)}>
                <IconDelete />
              </Button>
            </div>
          ))}
          <div
            key={"last"}
            style={{
              marginBottom: "10px",
              display: "flex",
              alignItems: "center",
            }}
          >
            <Form.Control
              type="color"
              defaultValue={config.highColor || "#e74c3c"}
              onBlur={(e) => handleChange("highColor", e.target.value)}
              style={{ width: "40px", marginRight: "10px" }}
            />
            <Form.Control
              type="number"
              min={0}
              step={1}
              max={100}
              disabled
              value={
                limits.length > 0 ? (limits[limits.length - 1].max ?? 0) : 75
              }
              style={{ width: "100px", marginRight: "10px" }}
            />
            <Form.Control
              type="number"
              disabled
              value="100"
              style={{ width: "100px", marginRight: "10px" }}
            />
          </div>
          <Button variant={"primary"} className="mt-3" onClick={addLimit}>
            Ajouter un palier
          </Button>
        </>
      )}
    </>
  );
};

export default GaugeConfig;
