import { Form } from "react-bootstrap";
import { PropsAxis } from "./Utils.tsx";

const AxisOrientationConfig = ({ value, onChange }: PropsAxis) => {
  return (
    <>
      <Form.Label className="fw-bold mt-3">
        Orientation de l&apos;axe X :
      </Form.Label>

      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: "15px",
          marginBottom: "15px",
        }}
      >
        <Form.Range
          min={-90}
          max={0}
          step={5}
          value={value}
          onChange={(e) => onChange(Number(e.target.value))}
          style={{ width: "200px" }}
        />

        <Form.Control
          type="number"
          min={-90}
          max={0}
          value={value}
          onChange={(e) => onChange(Number(e.target.value))}
          style={{ width: "80px" }}
        />

        <span>°</span>
      </div>
    </>
  );
};

export default AxisOrientationConfig;
