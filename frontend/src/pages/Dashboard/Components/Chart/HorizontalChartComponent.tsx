import { XAxis, YAxis } from "recharts";
import BaseBarChart from "./components/BaseBarChart.tsx";
import {
  Alignement,
  BaseChartData,
  ChartConfig,
  Orientation,
} from "./components/Utils.tsx";

const HorizontalChartComponent = ({
  data,
  config,
}: {
  data: BaseChartData[];
  config: ChartConfig;
}) => {
  return (
    <BaseBarChart
      data={data}
      config={config}
      layout={Orientation.VERTICAL}
      legendPosition={Alignement.BOTTOM}
      margin={{
        top: 20,
        right: 30,
        left: 40,
        bottom: 5,
      }}
      xAxis={
        <XAxis
          type="number"
          domain={[0, 100]}
          tickFormatter={(tick) => `${tick}%`}
        />
      }
      yAxis={<YAxis type="category" dataKey="name" width={150} />}
    />
  );
};

export default HorizontalChartComponent;
