import { ReactNode } from "react";
import { OverlayTrigger, Popover } from "react-bootstrap";

const TooltipCustom = ({
  tooltipText,
  tooltipId,
  tooltipHeader,
  placement = "bottom",
  children,
}: TooltipType) => {
  return (
    <OverlayTrigger
      overlay={
        <Popover id={tooltipId}>
          {tooltipHeader && (
            <Popover.Header as="h3">{tooltipHeader}</Popover.Header>
          )}
          <Popover.Body>{tooltipText}</Popover.Body>
        </Popover>
      }
      placement={placement}
    >
      <a> {children}</a>
    </OverlayTrigger>
  );
};

type TooltipType = {
  tooltipText: string | ReactNode;
  tooltipId: string;
  tooltipHeader?: string;
  placement?: string;
  children: ReactNode;
};

export default TooltipCustom;
