import { Cell, Legend, Pie, PieChart, ResponsiveContainer } from "recharts";
import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";

const COLORS = [
  "#8884d8",
  "#82ca9d",
  "#ffc658",
  "#ff8042",
  "#0088FE",
  "#00C49F",
  "pink",
  "#cf5340",
  "#851c7e",
  "#0b8000",
]; // Couleurs des sections du pie chart

const PieChartComponent = (data: { data: any[] | undefined; config: any }) => {
  if (!data.data || data.data.length === 0) {
    return;
  }
  const dataMapped = setSimpleValueMapped(data.data, data.config);

  const convertedData = dataMapped.map((item: { value: string }) => ({
    ...item, // Conserver les autres propriétés de l'objet
    value: parseInt(item.value), // Convertir `value` en entier
  }));

  const renderCustomizedLabel = ({ percent, value }) => {
    if (!percent) {
      return null;
    }
    return `${value} (${(percent * 100).toFixed(2)}%)`;
  };

  return (
    <ResponsiveContainer width="100%" height="100%">
      <PieChart>
        <Pie
          data={convertedData}
          dataKey="value"
          nameKey="name"
          outerRadius="80%" // Rayon externe du pie
          innerRadius="30%" // Rayon interne (pour un graphique en donut, par exemple)
          fill="#8884d8"
          label={renderCustomizedLabel}
        >
          {/* Ajout des couleurs des sections */}
          {data.data.map((entry: any, index: number) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default PieChartComponent;
