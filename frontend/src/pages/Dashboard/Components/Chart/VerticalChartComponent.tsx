import { XAxis, YAxis } from "recharts";
import BaseBarChart from "./components/BaseBarChart.tsx";
import {
  Alignement,
  BaseChartData,
  ChartConfig,
  Orientation,
} from "./components/Utils.tsx";

const VerticalChartComponent = ({
  data,
  config,
}: {
  data: BaseChartData[];
  config: ChartConfig;
}) => {
  const xAxisOrientation = config?.xAxisOrientation || 0;

  return (
    <BaseBarChart
      data={data}
      config={config}
      layout={Orientation.HORIZONTAL}
      legendPosition={Alignement.TOP}
      margin={{
        top: 20,
        right: 30,
        left: 40,
        bottom: xAxisOrientation !== 0 ? 80 : 5,
      }}
      xAxis={
        <XAxis
          type="category"
          dataKey="name"
          angle={xAxisOrientation}
          textAnchor={xAxisOrientation !== 0 ? "end" : "middle"}
          height={xAxisOrientation !== 0 ? 80 : 30}
        />
      }
      yAxis={
        <YAxis
          type="number"
          domain={[0, 100]}
          tickFormatter={(tick) => `${tick}%`}
        />
      }
    />
  );
};

export default VerticalChartComponent;
