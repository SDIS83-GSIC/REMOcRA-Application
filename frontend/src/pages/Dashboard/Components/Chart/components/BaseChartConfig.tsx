import { ReactNodeLike } from "prop-types";
import { ReactNode } from "react";
import { Form } from "react-bootstrap";
import { SelectInput } from "../../../../../components/Form/Form.tsx";
import useChartColorConfig from "../hooks/useChartColorConfig.tsx";
import GradientSection from "./GradientSectionConfig.tsx";
import { ChartConfig, ColorMode, OptionsList, Props } from "./Utils.tsx";

const BaseChartConfig = ({
  config,
  setConfig,
  fieldOptions,
  defaultLimits,
  children,
}: React.PropsWithChildren<Props>) => {
  const {
    limits,
    isGradient,
    updateConfig,
    handleColorChange,
    handleMaxChange,
    addLimit,
    removeLimit,
  } = useChartColorConfig({ config, setConfig, defaultLimits });

  const getFieldValue = (field: keyof ChartConfig) =>
    fieldOptions.find((o) => o.value === config[field]) || "";

  return (
    <>
      <SelectInput
        name="name"
        label="Libellé"
        defaultValue={getFieldValue("name")}
        options={fieldOptions}
        onChange={(v: OptionsList) => updateConfig("name", v.value)}
        getOptionLabel={(option: OptionsList) => option.value}
        getOptionValue={(option: OptionsList) => option.value}
      />

      <SelectInput
        name="value"
        label="Valeur initiale"
        defaultValue={getFieldValue("value")}
        options={fieldOptions}
        onChange={(v: OptionsList) => updateConfig("value", v.value)}
        getOptionLabel={(option: OptionsList) => option.value}
        getOptionValue={(option: OptionsList) => option.value}
      />

      <SelectInput
        name="max"
        label="Valeur maximum"
        defaultValue={getFieldValue("max")}
        options={fieldOptions}
        onChange={(v: OptionsList) => updateConfig("max", v.value)}
        getOptionLabel={(option: OptionsList) => option.value}
        getOptionValue={(option: OptionsList) => option.value}
      />

      {children}

      <Form.Label className="fw-bold mt-3">Mode de couleur :</Form.Label>
      <div>
        <Form.Check
          type="radio"
          label="Couleur unie"
          checked={!isGradient}
          onChange={() => updateConfig("colorMode", ColorMode.SOLID)}
        />
        <Form.Check
          type="radio"
          label="Dégradé de couleurs"
          checked={isGradient}
          onChange={() => updateConfig("colorMode", ColorMode.GRADIANT)}
        />
      </div>

      {!isGradient && (
        <>
          <Form.Label className="fw-bold mt-3">Couleur des barres :</Form.Label>
          <Form.Control
            type="color"
            defaultValue={config.barColor || "#8884d8"}
            onBlur={(e) => updateConfig("barColor", e.target.value)}
            style={{ width: "100px", height: "40px" }}
          />
        </>
      )}

      {isGradient && (
        <GradientSection
          limits={limits}
          config={config}
          updateConfig={updateConfig}
          handleColorChange={handleColorChange}
          handleMaxChange={handleMaxChange}
          addLimit={addLimit}
          removeLimit={removeLimit}
        />
      )}
    </>
  );
};

export default BaseChartConfig;
