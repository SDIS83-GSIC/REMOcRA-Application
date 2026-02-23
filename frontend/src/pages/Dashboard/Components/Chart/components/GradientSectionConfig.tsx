import { Button, Form } from "react-bootstrap";
import { IconDelete } from "../../../../../components/Icon/Icon.tsx";
import { ElementsGradient } from "./Utils.tsx";

const GradientSection = ({
  limits,
  config,
  updateConfig,
  handleColorChange,
  handleMaxChange,
  addLimit,
  removeLimit,
}: ElementsGradient) => {
  const styleForm = { width: "100px", marginRight: "10px" };

  return (
    <>
      <Form.Label className="fw-bold mt-2">Paliers des sections :</Form.Label>
      <br />

      {limits.map((limit, index) => (
        <div
          key={index}
          style={{
            marginBottom: "10px",
            display: "flex",
            alignItems: "center",
          }}
        >
          <Form.Control
            type="color"
            defaultValue={limit.color}
            onBlur={(e) => handleColorChange(index, e.target.value)}
            style={{ width: "40px", marginRight: "10px" }}
          />

          <Form.Control
            type="number"
            disabled
            value={index === 0 ? 0 : (limits[index - 1]?.max ?? 0)}
            style={styleForm}
          />

          <Form.Control
            type="number"
            defaultValue={limit.max}
            onBlur={(e) => handleMaxChange(index, parseInt(e.target.value, 10))}
            style={styleForm}
          />

          <Button variant="warning" onClick={() => removeLimit(index)}>
            <IconDelete />
          </Button>
        </div>
      ))}

      <div
        style={{ marginBottom: "10px", display: "flex", alignItems: "center" }}
      >
        <Form.Control
          type="color"
          defaultValue={config.highColor || "#12ca15"}
          onBlur={(e) => updateConfig("highColor", e.target.value)}
          style={{ width: "40px", marginRight: "10px" }}
        />

        <Form.Control
          type="number"
          disabled
          value={limits.length > 0 ? (limits[limits.length - 1].max ?? 0) : 75}
          style={styleForm}
        />

        <Form.Control type="number" disabled value="100" style={styleForm} />
      </div>

      <Button variant="primary" className="mt-3" onClick={addLimit}>
        Ajouter un palier
      </Button>
      <br />
    </>
  );
};

export default GradientSection;
