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

interface ColorInterval {
  value: string;
  color: string;
}

const PieChartComponent = (data: { data: any[] | undefined; config: any }) => {
  if (!data.data || data.data.length === 0) {
    return;
  }
  const dataMapped = setSimpleValueMapped(data.data, data.config);

  const convertedData = dataMapped.map((item: { value: string }) => ({
    ...item, // Conserver les autres propriétés de l'objet
    value: parseInt(item.value), // Convertir `value` en entier
  }));

  // Fonction pour obtenir la couleur d'un élément
  const getColorForItem = (item: any, index: number): string => {
    // Si les intervalles personnalisés sont activés
    if (data.config.useCustomIntervals && data.config.colorIntervals) {
      const intervals: ColorInterval[] = data.config.colorIntervals;

      // Rechercher une correspondance par nom
      const matchingInterval = intervals.find(
        (interval) => interval.value === item.name,
      );

      if (matchingInterval) {
        return matchingInterval.color;
      }
    }

    // Sinon, utiliser les couleurs par défaut
    return COLORS[index % COLORS.length];
  };

  const renderCustomizedLabel = ({
    percent,
    value,
  }: {
    percent: number;
    value: number;
  }) => {
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
          {convertedData.map((entry: any, index: number) => (
            <Cell key={`cell-${index}`} fill={getColorForItem(entry, index)} />
          ))}
        </Pie>
        <Legend />
      </PieChart>
    </ResponsiveContainer>
  );
};

export default PieChartComponent;
