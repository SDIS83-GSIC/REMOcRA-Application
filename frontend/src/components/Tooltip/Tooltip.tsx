import { ReactNode } from "react";
import { OverlayTrigger, Tooltip } from "react-bootstrap";

const TooltipCustom = ({
  tooltipText,
  tooltipId,
  placement = "bottom",
  children,
}: TooltipType) => {
  return (
    <OverlayTrigger
      overlay={<Tooltip id={tooltipId}>{tooltipText}</Tooltip>}
      placement={placement}
      delayShow={300}
      delayHide={150}
    >
      {children}
    </OverlayTrigger>
  );
};

type TooltipType = {
  tooltipText: string;
  tooltipId: string;
  placement?: string;
  children: ReactNode;
};

export default TooltipCustom;
