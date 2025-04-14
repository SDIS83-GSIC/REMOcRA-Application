import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import classnames from "classnames";
import useModal from "../Modal/ModalUtils.tsx";
import SimpleModal from "../Modal/SimpleModal.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";

const ButtonWithSimpleModal = ({
  icon,
  disabled = false,
  classEnable,
  textDisable,
  textEnable,
  tooltipId,
  header,
  content,
}: {
  icon: ReactNode;
  disabled: boolean;
  classEnable: string;
  textDisable: string | undefined;
  textEnable: string | undefined;
  tooltipId: string;
  header: string;
  content: ReactNode;
}) => {
  const { visible, show, close, ref } = useModal();
  return (
    <>
      <TooltipCustom
        tooltipText={disabled ? textDisable : textEnable}
        tooltipId={tooltipId}
      >
        <Button
          variant={"link"}
          className={classnames(
            "p-0 m-0",
            disabled
              ? "text-decoration-none text-muted"
              : "text-decoration-none text-" + classEnable,
          )}
          disabled={disabled}
          onClick={() => show()}
        >
          {icon}
        </Button>
      </TooltipCustom>
      {!disabled && (
        <SimpleModal
          visible={visible}
          closeModal={close}
          ref={ref}
          content={content}
          header={header}
        />
      )}
    </>
  );
};

export default ButtonWithSimpleModal;
