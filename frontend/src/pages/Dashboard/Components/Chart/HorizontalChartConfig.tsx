import BaseChartConfig from "./components/BaseChartConfig.tsx";
import { defaultLimits, ElementsChart } from "./components/Utils.tsx";

const HorizontalChartConfig = (props: ElementsChart) => {
  return <BaseChartConfig {...props} defaultLimits={defaultLimits} />;
};

export default HorizontalChartConfig;
