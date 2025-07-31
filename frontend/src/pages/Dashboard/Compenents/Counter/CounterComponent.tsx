import { setSimpleValueMapped } from "../../MappedValueComponent.tsx";

const CounterComponent = (data: any) => {
  const valueColor = "#8884d8";
  const valueSize = "40px";
  const labelSize = "20px";

  if (data.data) {
    // On map seulement les valeurs
    const dataMapped = setSimpleValueMapped(data.data, {
      value: data.config.value,
    });

    let sumData = 0;
    dataMapped.forEach((data: { value: number }) => {
      return (sumData += data.value);
    });

    return (
      <div style={{ textAlign: "center" }}>
        {/* Valeur en gros */}
        <div
          style={{
            fontSize: valueSize,
            fontWeight: "bold",
            color: valueColor,
          }}
        >
          {sumData}
        </div>

        {/* Label en dessous */}
        <div
          style={{
            fontSize: labelSize,
            fontWeight: "normal",
            color: "#666",
            marginTop: "10px",
          }}
        >
          {data.config.label}
        </div>
      </div>
    );
  }
};

export default CounterComponent;
