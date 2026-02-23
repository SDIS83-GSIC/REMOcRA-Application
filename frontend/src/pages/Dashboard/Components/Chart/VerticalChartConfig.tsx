import AxisOrientationConfig from "./components/AxisOrientationConfig.tsx";
import BaseChartConfig from "./components/BaseChartConfig.tsx";
import { defaultLimits, ElementsChart } from "./components/Utils.tsx";

const VerticalChartConfig = ({
  config,
  setConfig,
  fieldOptions,
}: ElementsChart) => {
  return (
    <BaseChartConfig
      config={config}
      setConfig={setConfig}
      fieldOptions={fieldOptions}
      defaultLimits={defaultLimits}
    >
      <AxisOrientationConfig
        value={config.xAxisOrientation || 0}
        onChange={(value) => setConfig({ ...config, xAxisOrientation: value })}
      />
    </BaseChartConfig>
  );
};

export default VerticalChartConfig;
