import {
  BarChart,
  Bar,
  XAxis,
  YAxis,
  CartesianGrid,
  Tooltip,
  ResponsiveContainer,
} from "recharts";
import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";

const HorizontalChartComponent = (data: {
  data: { value: number; max: number }[];
  config: any;
}) => {
  const barColor = "#8884d8";

  if (!data.data || data.data.length === 0) {
    return;
  }
  const dataMapped = setSimpleValueMapped(data.data, data.config);

  const convertedData = dataMapped.map(
    (item: { value: string; max: string }) => ({
      ...item, // Conserver les autres propriétés de l'objet
      value: parseInt(item.value), // Convertir `value` en entier
      max: parseInt(item.max), // Convertir `value` en entier
      percentage: (parseInt(item.value) / parseInt(item.max)) * 100, // Calcul du pourcentage
    }),
  );

  return (
    <ResponsiveContainer width="100%" height="100%">
      <BarChart
        data={convertedData}
        layout="vertical" // Mode horizontal
        margin={{ top: 20, right: 30, left: 40, bottom: 5 }}
      >
        <CartesianGrid strokeDasharray="3 3" />
        <XAxis
          type="number"
          domain={[0, 100]}
          tickFormatter={(tick) => `${tick}%`}
        />
        <YAxis type="category" dataKey="name" width={150} />
        <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />
        <Bar dataKey="percentage" fill={barColor} barSize={20} />
      </BarChart>
    </ResponsiveContainer>
  );
};

export default HorizontalChartComponent;
