import Button from "react-bootstrap/Button";
import Form from "react-bootstrap/Form";
import { SelectInput } from "../../../../components/Form/Form.tsx";
import { IconDelete, IconInfo } from "../../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../../components/Tooltip/Tooltip.tsx";

interface ColorInterval {
  value: string;
  color: string;
}

const PieChartConfig = (options: {
  fieldOptions: any[];
  config: {
    value: any;
    name: any;
    useCustomIntervals?: boolean;
    colorIntervals?: ColorInterval[];
  };
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
  const handleChange = (fieldName: any, newValue: any) => {
    const updatedConfig = { ...options.config };
    updatedConfig[fieldName as keyof typeof updatedConfig] = newValue;

    options.setConfig(updatedConfig);
  };

  // Ajouter un nouvel intervalle
  const addInterval = () => {
    const intervals = options.config.colorIntervals || [];
    handleChange("colorIntervals", [
      ...intervals,
      { value: "", color: "#8884d8" },
    ]);
  };

  // Supprimer un intervalle
  const removeInterval = (index: number) => {
    const intervals = options.config.colorIntervals || [];
    handleChange(
      "colorIntervals",
      intervals.filter((_, i) => i !== index),
    );
  };

  // Mettre à jour un intervalle
  const updateInterval = (
    index: number,
    field: keyof ColorInterval,
    value: string,
  ) => {
    const intervals = [...(options.config.colorIntervals || [])];
    intervals[index] = { ...intervals[index], [field]: value };
    handleChange("colorIntervals", intervals);
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

      <div className="mt-3 d-flex align-items-center">
        <Form.Check
          type="checkbox"
          id="useCustomIntervals"
          label="Plage définie de valeurs"
          checked={options.config.useCustomIntervals || false}
          onChange={(e) => handleChange("useCustomIntervals", e.target.checked)}
        />
        <TooltipCustom
          tooltipText="Permet de définir des plages de valeurs avec des couleurs spécifiques. Si désactivé, les couleurs seront attribuées arbitrairement (cas par défaut)"
          tooltipId="useCustomIntervals-tooltip"
        >
          <IconInfo />
        </TooltipCustom>
      </div>

      {options.config.useCustomIntervals && (
        <div className="mt-3">
          <Form.Label className="fw-bold mt-2">Valeurs possibles :</Form.Label>
          <br />
          {(options.config.colorIntervals || []).map((interval, index) => (
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
                defaultValue={interval.color}
                onBlur={(e) => updateInterval(index, "color", e.target.value)}
                style={{ width: "40px", marginRight: "10px" }}
              />
              <Form.Control
                type="text"
                placeholder="Valeur"
                defaultValue={interval.value}
                onBlur={(e) => updateInterval(index, "value", e.target.value)}
                style={{ marginRight: "10px" }}
              />
              <Button variant={"warning"} onClick={() => removeInterval(index)}>
                <IconDelete />
              </Button>
            </div>
          ))}
          <Button variant={"primary"} className="mt-3" onClick={addInterval}>
            Ajouter une valeur
          </Button>
        </div>
      )}
    </>
  );
};

export default PieChartConfig;
