import { ReactNode } from "react";
import { ToggleButton } from "react-bootstrap";
import TooltipCustom from "../Tooltip/Tooltip.tsx";

const ToolbarButton = ({
  toolName,
  toolIcon,
  toolLabelTooltip,
  toggleTool,
  activeTool,
  disabled = false,
  variant = "primary",
}: {
  toolName: string;
  toolLabelTooltip: ReactNode | string;
  toolIcon: ReactNode;
  toggleTool: (tool: string) => void;
  activeTool: string | undefined;
  disabled?: boolean;
  variant?: string;
  onClick?: () => void;
}) => {
  return (
    <TooltipCustom tooltipText={toolLabelTooltip} tooltipId={toolName}>
      <ToggleButton
        name={"tool"}
        disabled={disabled}
        onClick={() => toggleTool(toolName)}
        id={toolName}
        value={toolName}
        type={"radio"}
        variant={"outline-" + variant}
        checked={!disabled && activeTool === toolName}
        className="m-2"
      >
        {toolIcon}
      </ToggleButton>
    </TooltipCustom>
  );
};

export default ToolbarButton;
