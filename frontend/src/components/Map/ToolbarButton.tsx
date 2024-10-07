import { ReactNode } from "react";
import { ToggleButton } from "react-bootstrap";

const ToolbarButton = ({
  toolName,
  toolLabel,
  toggleTool,
  activeTool,
  disabled = false,
}: {
  toolName: string;
  toolLabel: ReactNode | string;
  toggleTool: (tool: string) => void;
  activeTool: string | undefined;
  disabled?: boolean;
}) => {
  return (
    <ToggleButton
      name={"tool"}
      disabled={disabled}
      onClick={() => toggleTool(toolName)}
      id={toolName}
      value={toolName}
      type={"radio"}
      variant={"outline-primary"}
      checked={activeTool === toolName}
    >
      {toolLabel}
    </ToggleButton>
  );
};

export default ToolbarButton;
