import {
  Pie,
  PieChart,
  PieSectorShapeProps,
  ResponsiveContainer,
  Sector,
} from "recharts";
import { setGaugeValueMapped } from "../../MappedValueComponent.tsx";

interface Limit {
  color?: string;
  max?: number;
}

interface GaugeConfigData {
  colorMode?: string;
  limits?: Limit[];
  highColor?: string;
  barColor?: string;
}

interface GaugeData {
  value: string;
  max: string;
}

interface GaugeComponentProps {
  data: GaugeData[];
  config: GaugeConfigData;
}

const GaugeComponent = ({ data, config }: GaugeComponentProps) => {
  if (!data || data.length === 0) {
    return;
  }

  const dataMapped = setGaugeValueMapped(data, config);

  // Calcule le pourcentage à afficher
  const safeParseFloat = (str: string): number => {
    const num = parseFloat(parseFloat(str).toFixed(2));
    return isNaN(num) ? 0 : num;
  };

  const sumValues = dataMapped.reduce(
    (sum: number, item: GaugeData) => sum + safeParseFloat(item.value),
    0,
  );
  const sumMax = dataMapped.reduce(
    (sum: number, item: GaugeData) => sum + safeParseFloat(item.max),
    0,
  );

  const percentage = safeParseFloat(
    (sumMax === 0 ? 0 : (sumValues / sumMax) * 100).toFixed(2),
  );

  const colorMode = config?.colorMode;
  const useThresholdColors =
    colorMode === "gradient" ||
    (colorMode == null &&
      Array.isArray(config?.limits) &&
      config.limits.length > 0);

  const rawLimits: Limit[] = Array.isArray(config?.limits) ? config.limits : [];
  const highColor = config?.highColor || "#e74c3c";
  const barColor = config?.barColor || "#00293e";

  interface Section {
    from: number;
    to: number;
    color: string;
  }

  const sections: Section[] = (() => {
    if (!useThresholdColors) {
      return [];
    }
    const limits = rawLimits
      .map((l) => ({ color: l.color || "#cccccc", max: Number(l.max) }))
      .filter((l) => !Number.isNaN(l.max))
      .map((l) => ({ color: l.color, max: Math.max(0, Math.min(100, l.max)) }));
    const s: Section[] = [];
    let prev = 0;
    for (const lim of limits) {
      const to = Math.max(prev, lim.max);
      if (to > prev) {
        s.push({ from: prev, to, color: lim.color });
      }
      prev = to;
    }
    if (s.length === 0) {
      return [
        { from: 0, to: 25, color: "#2ecc71" },
        { from: 25, to: 50, color: "#f1c40f" },
        { from: 50, to: 75, color: "#e67e22" },
        { from: 75, to: 100, color: highColor },
      ];
    }
    if (prev < 100) {
      s.push({ from: prev, to: 100, color: highColor });
    }
    return s;
  })();

  // Préparer les données pour la jauge extérieure
  const outerGaugeData = useThresholdColors
    ? sections.map((sec) => ({
        value: sec.to - sec.from,
        color: sec.color,
      }))
    : [{ value: 100, color: barColor }];

  // Préparer les données pour la jauge intérieure (segments en fonction de la valeur)
  const innerGaugeData = [
    { value: percentage - 1, color: "#f2f4f6" }, // Segment de 0 à (value - 1)
    { value: 2, color: "#000000" }, // Segment de (value - 1) à (value + 1)
    { value: 100 - (percentage + 1), color: "#f2f4f6" }, // Segment de (value + 1) à 100
  ];

  const PieCell = (
    props: PieSectorShapeProps & { payload?: { color?: string } },
  ) => (
    <Sector
      {...props}
      fill={props.payload?.color || props.fill}
      stroke="none"
    />
  );

  return (
    <>
      <ResponsiveContainer
        width="100%"
        height="80%"
        initialDimension={{ width: 400, height: 300 }}
      >
        <PieChart className="d-flex flex-column justify-content-center align-items-center">
          {/* Jauge extérieure */}
          <Pie
            data={outerGaugeData}
            cx="50%"
            cy="100%"
            innerRadius={120}
            outerRadius={150}
            startAngle={180}
            endAngle={0}
            dataKey="value"
            shape={PieCell}
          />

          {/* Jauge intérieure (segments en fonction de la valeur) */}
          <Pie
            data={innerGaugeData}
            cx="50%"
            cy="100%"
            innerRadius={0} // Rayon intérieur de la jauge intérieure
            outerRadius={120} // Rayon extérieur de la jauge intérieure
            startAngle={180}
            endAngle={0} // Demi-jauge (de 180 à 0 degrés)
            dataKey="value"
            shape={PieCell}
          />
        </PieChart>
      </ResponsiveContainer>

      {/* Affichage de la valeur au centre */}
      <div
        style={{
          fontSize: "40px",
          fontWeight: "bold",
          color: barColor,
        }}
        className="text-center"
      >
        {percentage + "%"}
      </div>
    </>
  );
};

export default GaugeComponent;
