import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import classnames from "classnames";
import ConfirmModal from "../Modal/ConfirmModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";

const ConfirmButtonWithModal = ({
  path,
  reload,
  icon,
  disabled = false,
  classEnable,
  textDisable,
  textEnable,
  tooltipId,
  isPost = true,
  header,
  content,
}: {
  path: string;
  reload?: () => void;
  icon: ReactNode;
  disabled: boolean;
  classEnable: string;
  textDisable: string | undefined;
  textEnable: string | undefined;
  tooltipId: string;
  isPost: boolean;
  header: string;
  content: string;
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
        <ConfirmModal
          isPost={isPost}
          visible={visible}
          closeModal={close}
          query={path}
          ref={ref}
          header={header}
          content={content}
          onConfirm={() => (reload ? reload() : window.location.reload())}
        />
      )}
    </>
  );
};

export default ConfirmButtonWithModal;
