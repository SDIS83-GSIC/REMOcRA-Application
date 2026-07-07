import {
  Bar,
  BarChart,
  BarShapeProps,
  CartesianGrid,
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

  const barColor = config?.barColor || "#00293e";
  const convertedData = mapAndConvertData(data, config);
  const useThresholdColors = shouldUseThresholdColors(config);
  const sections = buildSections(config, useThresholdColors);
  const thresholdLines = buildThresholdLines(sections);

  return (
    <ResponsiveContainer
      width="100%"
      height="100%"
      initialDimension={{ width: 400, height: 300 }}
    >
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
            content={() => {
              const items = legendPayload(sections);
              return (
                <div style={{ display: "flex", gap: "16px" }}>
                  {items.map((item) => (
                    <div
                      key={item.id}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <div
                        style={{
                          width: "12px",
                          height: "12px",
                          backgroundColor: item.color,
                          borderRadius: "2px",
                        }}
                      />
                      <span>{item.value}</span>
                    </div>
                  ))}
                </div>
              );
            }}
          />
        )}

        {xAxis}
        {yAxis}

        <Tooltip formatter={(value: number) => `${value.toFixed(2)}%`} />

        {useThresholdColors ? (
          <Bar
            dataKey="percentage"
            barSize={20}
            legendType="square"
            shape={(props: BarShapeProps) => {
              const { x, y, width, height, payload } = props;
              const fill = resolveColor(payload.percentage, sections, barColor);
              return (
                <rect x={x} y={y} width={width} height={height} fill={fill} />
              );
            }}
          />
        ) : (
          <Bar dataKey="percentage" barSize={20} fill={barColor} />
        )}

        {legendPosition === Alignement.BOTTOM && sections.length > 0 && (
          <Legend
            verticalAlign={Alignement.BOTTOM}
            align="left"
            wrapperStyle={{ paddingBottom: 8 }}
            content={() => {
              const items = legendPayload(sections);
              return (
                <div style={{ display: "flex", gap: "16px" }}>
                  {items.map((item) => (
                    <div
                      key={item.id}
                      style={{
                        display: "flex",
                        alignItems: "center",
                        gap: "8px",
                      }}
                    >
                      <div
                        style={{
                          width: "12px",
                          height: "12px",
                          backgroundColor: item.color,
                          borderRadius: "2px",
                        }}
                      />
                      <span>{item.value}</span>
                    </div>
                  ))}
                </div>
              );
            }}
          />
        )}
      </BarChart>
    </ResponsiveContainer>
  );
};

export default BaseBarChart;
