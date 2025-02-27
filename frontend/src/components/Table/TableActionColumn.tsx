import { ReactNode } from "react";
import { Button, Row, Col } from "react-bootstrap";
import classNames from "classnames";
import ButtonWithSimpleModal from "../Button/ButtonWithSimpleModal.tsx";
import ConfirmButtonWithModal from "../Button/ConfirmButtonWithModal.tsx";
import DeleteButton from "../Button/DeleteButton.tsx";
import { IconClose, IconDelete, IconEdit, IconSee } from "../Icon/Icon.tsx";
import DeleteModal from "../Modal/DeleteModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import CustomLinkButton from "../../components/Button/CustomLinkButton.tsx";

const TableActionColumn = ({
  row,
  disabled = false,
  textDisable,
  textEnable,
  classEnable = "primary",
  deleteModal = null,
  confirmModal,
  simpleModal,
  isPost = true,
  reload,
  icon,
  pathname,
  hide = () => false,
  onClick,
  isLink = true,
}: TableActionButtonType) => {
  return (
    <Col className={"m-0 p-0"} xs={12} md={6} xxl={3}>
      {!hide ||
        (!hide(row.original) && (
          <>
            {/*si il y a une deleteModal*/}
            {deleteModal != null ? (
              <>
                <TooltipCustom
                  tooltipText={disabled ? textDisable : textEnable}
                  tooltipId={row.value}
                >
                  <DeleteButton
                    className={disabled ? "text-muted" : "text-" + classEnable}
                    disabled={disabled}
                    onClick={deleteModal?.show}
                  />
                </TooltipCustom>
                {!disabled && (
                  <DeleteModal
                    visible={deleteModal.visible}
                    closeModal={deleteModal.close}
                    query={pathname}
                    ref={deleteModal.ref}
                    onDelete={() =>
                      reload ? reload() : window.location.reload(false)
                    }
                  />
                )}
              </>
            ) : confirmModal ? (
              <ConfirmButtonWithModal
                path={pathname}
                icon={icon}
                disabled={disabled}
                classEnable={classEnable}
                textDisable={textDisable}
                textEnable={textEnable}
                tooltipId={row.value}
                isPost={isPost}
              />
            ) : simpleModal != null ? (
              <ButtonWithSimpleModal
                icon={icon}
                disabled={disabled}
                classEnable={classEnable}
                textDisable={textDisable}
                textEnable={textEnable}
                tooltipId={row.value}
                header={simpleModal.header}
                content={simpleModal.content}
              />
            ) : isLink ? (
              <TooltipCustom
                tooltipText={disabled ? textDisable : textEnable}
                tooltipId={row.value}
              >
                <Col>
                  <CustomLinkButton
                    variant={"link"}
                    className={classNames(
                      "text-decoration-none",
                      disabled ? "text-muted" : "text-" + classEnable,
                    )}
                    disabled={disabled}
                    pathname={pathname}
                    onClick={onClick}
                  >
                    {icon}
                  </CustomLinkButton>
                </Col>
              </TooltipCustom>
            ) : (
              <TooltipCustom
                tooltipText={disabled ? textDisable : textEnable}
                tooltipId={row.value}
              >
                <Button
                  variant={"link"}
                  className={classNames(
                    "text-decoration-none",
                    disabled ? "text-muted" : "text-" + classEnable,
                  )}
                  disabled={disabled}
                  href={pathname}
                  onClick={onClick}
                >
                  {icon}
                </Button>
              </TooltipCustom>
            )}
          </>
        ))}
    </Col>
  );
};

type TableActionButtonType = {
  row?: any;
  disable?: (row?: any) => boolean;
  disabled?: boolean;
  textDisable?: string;
  textDisableFunction?: (row?: any) => string;
  conditionnalTextDisable?: (row?: any) => string;
  textEnable?: string;
  icon?: ReactNode;
  classEnable?: "primary" | "danger" | "warning" | "info" | "success";
  reload?: () => void;
  confirmModal?: boolean | null;
  deleteModal?: object | null;
  simpleModal?: SimpleModalType | null;
  hide?: (param: any) => boolean;
  onClick?: (param?: any) => any;
  isPost?: boolean;
  pathname?: string;
  isLink?: boolean;
};

type SimpleModalType = {
  header: string | ((row: any) => string);
  content: ReactNode | ((id: string) => ReactNode);
};
export const ActionButton = ({
  buttons,
  row,
}: {
  buttons: Array<ButtonType>;
  row: any;
}) => {
  return (
    <Row className={"d-flex flex-row"}>
      {buttons.map((_button) => {
        switch (_button.type) {
          case TYPE_BUTTON.DELETE:
            return <DeleteButtonPrivate _button={_button} row={row} />;
          case TYPE_BUTTON.CONFIRM:
            return <ConfirmButtonPrivate _button={_button} row={row} />;
          case TYPE_BUTTON.SIMPLE_MODAL:
            return <SimpleModalButtonPrivate _button={_button} row={row} />;
          case TYPE_BUTTON.UPDATE:
            return (
              <TableActionColumn
                row={row}
                textEnable={"Modifier"}
                icon={<IconEdit />}
                classEnable={"info"}
                textDisable={_button.textDisable}
                disabled={_button.disable ? _button.disable(row) : false}
                pathname={_button.route?.(row.value)}
                onClick={() => _button.onClick?.(row.value)}
              />
            );
          case TYPE_BUTTON.SEE:
            return (
              <TableActionColumn
                row={row}
                textEnable={"Voir plus"}
                icon={<IconSee />}
                pathname={_button.route?.(row.value)}
              />
            );
          case TYPE_BUTTON.LINK:
            return (
              <TableActionColumn
                row={row}
                textEnable={_button.textEnable}
                icon={_button.icon}
                pathname={_button.route?.(row.value)}
                onClick={() => _button.onClick?.(row.value)}
                hide={_button.hide}
                disabled={_button.disable ? _button.disable(row) : false}
                classEnable={_button.classEnable}
                confirmModal={_button.confirmModal}
                reload={_button.reload}
                textDisable={_button.textDisable}
              />
            );
          case TYPE_BUTTON.BUTTON:
            return (
              <TableActionColumn
                row={row}
                textEnable={_button.textEnable}
                icon={_button.icon}
                pathname={_button.route?.(row.value)}
                onClick={() => _button.onClick?.(row.value)}
                hide={_button.hide}
                disabled={_button.disable ? _button.disable(row) : false}
                classEnable={_button.classEnable}
                confirmModal={_button.confirmModal}
                reload={_button.reload}
                textDisable={_button.textDisable}
                isLink={false}
              />
            );
        }
      })}
    </Row>
  );
};

export type ButtonType = TableActionButtonType & {
  route?: (param: any) => string;
  type: TYPE_BUTTON;
};

export enum TYPE_BUTTON {
  DELETE,
  CONFIRM,
  SIMPLE_MODAL,
  UPDATE,
  SEE,
  LINK,
  BUTTON,
}

type DeleteButtonType = { row: any; _button: ButtonType };
const DeleteButtonPrivate = ({ row, _button }: DeleteButtonType) => {
  const { visible, show, close, ref } = useModal();
  const deleteModal = {
    close: close,
    ref: ref,
    show: show,
    visible: visible,
  };
  return (
    <TableActionColumn
      row={row}
      disabled={_button.disable ? _button.disable(row) : false}
      textDisable={_button.textDisable ?? _button.textDisableFunction?.(row)}
      textEnable={_button.textEnable ?? "Supprimer"}
      classEnable={"danger"}
      deleteModal={deleteModal}
      icon={<IconDelete />}
      pathname={`${_button.pathname}${row.value}`}
    />
  );
};

type ConfirmButtonType = { row: any; _button: ButtonType };
const ConfirmButtonPrivate = ({ row, _button }: ConfirmButtonType) => {
  return (
    <TableActionColumn
      isPost={_button.isPost}
      row={row}
      disabled={_button.disable ? _button.disable(row) : false}
      textDisable={
        _button.textDisable ?? _button.conditionnalTextDisable?.(row)
      }
      textEnable={_button.textEnable}
      classEnable={_button.classEnable}
      confirmModal={true}
      icon={_button.icon ?? <IconClose />}
      pathname={`${_button.pathname}${row.value}`}
    />
  );
};

type SimpleModalButtonType = {
  row: any;
  _button: ButtonType;
};
const SimpleModalButtonPrivate = ({ row, _button }: SimpleModalButtonType) => {
  const simpleModal: SimpleModalType = {
    content: _button.simpleModal?.content(row.value),
    header: _button.simpleModal?.header(row),
  };
  return (
    <TableActionColumn
      row={row}
      simpleModal={simpleModal}
      textEnable={_button.textEnable ?? "Voir plus"}
      icon={_button.icon ?? <IconClose />}
    />
  );
};
