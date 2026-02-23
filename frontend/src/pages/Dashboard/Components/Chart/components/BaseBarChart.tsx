import {
  Bar,
  BarChart,
  CartesianGrid,
  Cell,
  Legend,
  ReferenceArea,
  ReferenceLine,
  ResponsiveContainer,
  Tooltip,
} from "recharts";
import {
  buildSections,
  buildThresholdLines,
  legendPayload,
  mapAndConvertData,
  resolveColor,
  shouldUseThresholdColors,
} from "./ChartConfig.tsx";
import { Alignement, BaseBarChartProps, Orientation } from "./Utils.tsx";

const BaseBarChart = ({
  data,
  config,
  layout,
  xAxis,
  yAxis,
  legendPosition,
  margin,
}: BaseBarChartProps) => {
  if (!data || data.length === 0) {
    return null;
  }

  const barColor = config?.barColor || "#8884d8";
  const convertedData = mapAndConvertData(data, config);
  const useThresholdColors = shouldUseThresholdColors(config);
  const sections = buildSections(config, useThresholdColors);
  const thresholdLines = buildThresholdLines(sections);

  return (
    <ResponsiveContainer width="100%" height="100%">
      <BarChart data={convertedData} layout={layout} margin={margin}>
        <CartesianGrid strokeDasharray="3 3" />

        {useThresholdColors &&
          sections.map((sec, idx) => (
            <ReferenceArea
              key={idx}
              {...(layout === Orientation.VERTICAL
                ? { x1: sec.from, x2: sec.to }
                : { y1: sec.from, y2: sec.to })}
              fill={sec.color}
              fillOpacity={0.12}
              ifOverflow="extendDomain"
            />
          ))}

        {thresholdLines.map((val) => (
          <ReferenceLine
            key={val}
            {...(layout === Orientation.VERTICAL ? { x: val } : { y: val })}
            stroke="#9aa0a6"
            strokeDasharray="4 4"
          />
        ))}

        {legendPosition === Alignement.TOP && sections.length > 0 && (
          <Legend
            verticalAlign={Alignement.TOP}
            align="left"
            wrapperStyle={{ paddingBottom: 8 }}
            payload={legendPayload(sections)}
          />
        )}

        {xAxis}
        {yAxis}

        <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />

        {useThresholdColors ? (
          <Bar dataKey="percentage" barSize={20}>
            {convertedData.map(
              (entry: { percentage: number }, index: number) => (
                <Cell
                  key={`cell-${index}`} //key={index}
                  fill={resolveColor(entry.percentage, sections, barColor)}
                />
              ),
            )}
          </Bar>
        ) : (
          <Bar dataKey="percentage" barSize={20} fill={barColor} />
        )}

        {legendPosition === Alignement.BOTTOM && sections.length > 0 && (
          <Legend
            verticalAlign={Alignement.BOTTOM}
            align="left"
            wrapperStyle={{ paddingBottom: 8 }}
            payload={legendPayload(sections)}
          />
        )}
      </BarChart>
    </ResponsiveContainer>
  );
};

export default BaseBarChart;
