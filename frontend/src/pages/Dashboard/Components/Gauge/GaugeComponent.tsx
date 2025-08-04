import { Cell, Pie, PieChart, ResponsiveContainer, Sector } from "recharts";
import { setGaugeValueMapped } from "../../MappedValueComponent.tsx";

const GaugeComponent = (options: any) => {
  if (!options.data || options.data.length === 0) {
    return;
  }

  const dataMapped = setGaugeValueMapped(options.data, options.config);

  // Calcule le pourcentage à afficher
  const safeParseFloat = (str: string): number => {
    const num = parseFloat(parseFloat(str).toFixed(2));
    return isNaN(num) ? 0 : num;
  };

  const sumValues = dataMapped.reduce(
    (sum: number, item: { value: string }) => sum + safeParseFloat(item.value),
    0,
  );
  const sumMax = dataMapped.reduce(
    (sum: number, item: { max: string }) => sum + safeParseFloat(item.max),
    0,
  );

  const percentage = safeParseFloat(
    (sumMax === 0 ? 0 : (sumValues / sumMax) * 100).toFixed(2),
  );

  // Calculer l'angle de la valeur par rapport au maximum (100 dans ce cas)
  const maxAngle = 180; // Demi-jauge (180 degrés)
  const valueAngle = 180 - (percentage / 100) * maxAngle; // Inverser pour correspondre à la demi-jauge

  // Préparer les données pour la jauge extérieure (segments colorés)
  const outerGaugeData = options.config.limits.map((item: any) => ({
    ...item,
    startAngle: 180,
    endAngle: 0, // Demi-jauge (de 180 à 0 degrés)
  }));

  // Préparer les données pour la jauge intérieure (segments en fonction de la valeur)
  const innerGaugeData = [
    { value: percentage - 1, color: "#e0e0e0" }, // Segment de 0 à (value - 1)
    { value: 2, color: "#000000" }, // Segment de (value - 1) à (value + 1)
    { value: 100 - (percentage + 1), color: "#e0e0e0" }, // Segment de (value + 1) à 100
  ];

  return (
    <>
      <ResponsiveContainer
        width="100%"
        height="80%"
        className="d-flex flex-column justify-content-center align-items-center overflow-hidden"
      >
        <PieChart className="d-flex flex-column justify-content-center align-items-center">
          {/* Jauge extérieure (segments colorés) */}
          <Pie
            data={outerGaugeData}
            cx="50%"
            cy="100%"
            innerRadius={120} // Rayon intérieur de la jauge extérieure
            outerRadius={150} // Rayon extérieur de la jauge extérieure
            startAngle={180}
            endAngle={0} // Demi-jauge (de 180 à 0 degrés)
            dataKey="max"
          >
            {outerGaugeData.map(
              (entry: { color: string | undefined }, index: any) => (
                <Cell key={`outer-cell-${index}`} fill={entry.color} />
              ),
            )}
          </Pie>

          {/* Jauge intérieure (segments en fonction de la valeur) */}
          <Pie
            data={innerGaugeData}
            cx="50%"
            cy="100%"
            innerRadius={80} // Rayon intérieur de la jauge intérieure
            outerRadius={120} // Rayon extérieur de la jauge intérieure
            startAngle={180}
            endAngle={0} // Demi-jauge (de 180 à 0 degrés)
            dataKey="value"
          >
            {innerGaugeData.map((entry, index) => (
              <Cell key={`inner-cell-${index}`} fill={entry.color} />
            ))}
          </Pie>

          {/* Indicateur (aiguille) */}
          <Sector
            cx={200}
            cy={200}
            innerRadius={0}
            outerRadius={120}
            startAngle={valueAngle - 2} // Largeur de l'aiguille
            endAngle={valueAngle + 2} // Largeur de l'aiguille
            fill="#FF0000" // Couleur de l'aiguille
          />
        </PieChart>
      </ResponsiveContainer>

      {/* Affichage de la valeur au centre */}
      <div
        style={{
          fontSize: "40px",
          fontWeight: "bold",
          color: "#8884d8",
        }}
        className="text-center"
      >
        {percentage + "%"}
      </div>
    </>
  );
};

export default GaugeComponent;
