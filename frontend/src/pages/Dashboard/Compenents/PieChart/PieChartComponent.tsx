import {
  PieChart,
  Pie,
  Cell,
  ResponsiveContainer,
  Tooltip,
  Legend,
} from "recharts";
import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";

const COLORS = ["#8884d8", "#82ca9d", "#ffc658", "#ff8042"]; // Couleurs des sections du pie chart

const PieChartComponent = (data: { data: any[] | undefined; config: any }) => {
  if (!data.data || data.data.length === 0) {
    return;
  }
  const dataMapped = setSimpleValueMapped(data.data, data.config);

  const convertedData = dataMapped.map((item: { value: string }) => ({
    ...item, // Conserver les autres propriétés de l'objet
    value: parseInt(item.value), // Convertir `value` en entier
  }));

  return (
    <ResponsiveContainer width="100%" height="100%">
      <PieChart>
        <Pie
          data={convertedData}
          dataKey="value"
          nameKey="name"
          outerRadius="80%" // Rayon externe du pie
          innerRadius="60%" // Rayon interne (pour un graphique en donut, par exemple)
          fill="#8884d8"
          paddingAngle={5} // Espacement entre les secteurs
          label={({ name, value }) => `${name}: ${value}`} // Affichage des valeurs sur chaque secteur
        >
          {/* Ajout des couleurs des sections */}
          {data.data.map((entry: any, index: number) => (
            <Cell key={`cell-${index}`} fill={COLORS[index % COLORS.length]} />
          ))}
        </Pie>
        <Tooltip />
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default PieChartComponent;
