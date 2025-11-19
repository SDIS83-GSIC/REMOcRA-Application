import { ReactNode, useState } from "react";
import type { Placement } from "react-bootstrap/types";
import { OverlayTrigger, Popover } from "react-bootstrap";

const TooltipCustom = ({
  tooltipText,
  tooltipId,
  tooltipHeader,
  placement = "bottom",
  maxWidth,
  children,
  nowrap = true,
}: TooltipType) => {
  const [showTooltip, setShowTooltip] = useState(false);

  // Masque la tooltip lors du click sur le children
  const handleButtonClick = () => {
    setShowTooltip(false);
  };

  return (
    <div className={"tooltip-wrapper m-0 p-0"}>
      <OverlayTrigger
        /*
               Ajoute la tooltips au composant map-container si il existe pour qu'elle s'affiche
               aussi en fullscreen
           */
        container={document.getElementById("map-container")}
        show={showTooltip}
        overlay={
          <Popover id={tooltipId} className="tooltip-popover">
            {tooltipHeader && (
              <Popover.Header as="h3">{tooltipHeader}</Popover.Header>
            )}
            <Popover.Body>{tooltipText}</Popover.Body>
          </Popover>
        }
        placement={placement}
      >
        <div
          onMouseEnter={() => setShowTooltip(true)}
          onMouseLeave={() => setShowTooltip(false)}
          onClick={handleButtonClick}
          style={{
            maxWidth: maxWidth,
            overflowX: "hidden",
            textOverflow: "ellipsis",
            whiteSpace: nowrap ? "nowrap" : "normal",
          }}
        >
          {children}
        </div>
      </OverlayTrigger>
    </div>
  );
};

type TooltipType = {
  tooltipText: string | ReactNode;
  tooltipId: string;
  tooltipHeader?: string;
  placement?: Placement;
  children: React.ReactElement | ((props: any) => React.ReactNode);
  maxWidth?: number;
  nowrap?: boolean;
};

export default TooltipCustom;
