import { MutableRefObject, ReactNode } from "react";
import Col from "react-bootstrap/Col";
import Row from "react-bootstrap/Row";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import DeleteModal from "../Modal/DeleteModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";
import { IconClose, IconDelete, IconEdit, IconSee } from "../Icon/Icon.tsx";
import DeleteButton from "../Form/DeleteButton.tsx";
import ConfirmModal from "../Modal/ConfirmModal.tsx";
import CustomLinkButton from "../Form/CustomLinkButton.tsx";
import SimpleModal from "../Modal/SimpleModal.tsx";

const TableActionColumn = ({
  row,
  disabled = false,
  textDisable,
  textEnable,
  classEnable = "primary",
  deleteModale = null,
  confirmModale = null,
  simpleModal = null,
  isPost = true,
  query,
  reload,
  icon,
  href,
  hide = () => false,
  onClick,
}: TableActionButtonType) => {
  return (
    <Col className={"m-0 p-0"}>
      {!hide ||
        (!hide(row.original) && (
          <>
            {/*si il y a une deleteModale*/}
            {deleteModale != null ? (
              <>
                <TooltipCustom
                  tooltipText={disabled ? textDisable : textEnable}
                  tooltipId={row.value}
                >
                  <DeleteButton
                    className={disabled ? "text-muted" : "text-" + classEnable}
                    disabled={disabled}
                    onClick={deleteModale?.show}
                  />
                </TooltipCustom>
                {!disabled && (
                  <DeleteModal
                    visible={deleteModale.visible}
                    closeModal={deleteModale.close}
                    query={query}
                    ref={deleteModale.ref}
                    onDelete={() =>
                      reload ? reload() : window.location.reload(false)
                    }
                  />
                )}
              </>
            ) : confirmModale != null ? (
              <>
                <TooltipCustom
                  tooltipText={disabled ? textDisable : textEnable}
                  tooltipId={row.value}
                >
                  <CustomLinkButton
                    className={disabled ? "text-muted" : "text-" + classEnable}
                    disabled={disabled}
                    onClick={confirmModale?.show}
                  >
                    {icon}
                  </CustomLinkButton>
                </TooltipCustom>
                {!disabled && (
                  <ConfirmModal
                    isPost={isPost}
                    visible={confirmModale.visible}
                    closeModal={confirmModale.close}
                    query={query}
                    ref={confirmModale.ref}
                    onConfirm={() =>
                      reload ? reload() : window.location.reload(false)
                    }
                  />
                )}
              </>
            ) : simpleModal != null ? (
              <>
                <SimpleModal
                  closeModal={simpleModal.close}
                  content={simpleModal.content}
                  header={simpleModal.header}
                  ref={simpleModal.ref}
                  visible={simpleModal.visible}
                />
                <TooltipCustom
                  tooltipText={disabled ? textDisable : textEnable}
                  tooltipId={row.value}
                >
                  <CustomLinkButton
                    className={disabled ? "text-muted" : "text-" + classEnable}
                    disabled={disabled}
                    onClick={simpleModal?.show}
                  >
                    {icon}
                  </CustomLinkButton>
                </TooltipCustom>
              </>
            ) : (
              <TooltipCustom
                tooltipText={disabled ? textDisable : textEnable}
                tooltipId={row.value}
              >
                <CustomLinkButton
                  className={disabled ? "text-muted" : "text-" + classEnable}
                  disabled={disabled}
                  href={href}
                  onClick={onClick}
                >
                  {icon}
                </CustomLinkButton>
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
  textEnable?: string;
  icon?: ReactNode;
  classEnable?: "primary" | "danger" | "warning" | "info" | "success";
  query?;
  reload?;
  confirmModale?: ModaleType | null;
  deleteModale?: ModaleType | null;
  simpleModal?: SimpleModalType | null;
  path?: string;
  href?: (param: any) => string;
  hide?: (param: any) => boolean;
  onClick?: (param?: any) => any;
  isPost?: boolean;
};

type ModaleType = {
  visible?: boolean;
  show?: (value?: any) => void;
  close?: () => void;
  ref?: MutableRefObject<HTMLDialogElement | null>;
};

type SimpleModalType = ModaleType & {
  header: string;
  content: (id: string) => ReactNode;
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
                href={_button.href?.(row.value)}
                onClick={() => _button.onClick?.(row.value)}
              />
            );
          case TYPE_BUTTON.SEE:
            return (
              <TableActionColumn
                row={row}
                textEnable={"Voir plus"}
                icon={<IconSee />}
                href={_button.href(row.value)}
              />
            );
          case TYPE_BUTTON.CUSTOM:
            return (
              <TableActionColumn
                row={row}
                textEnable={_button.textEnable}
                icon={_button.icon}
                href={_button.href?.(row.value)}
                onClick={_button.onClick?.(row.value)}
                hide={_button.hide}
                query={_button.query}
                disabled={_button.disable ? _button.disable(row) : false}
                path={_button.path}
                classEnable={_button.classEnable}
                confirmModale={_button.confirmModale}
                reload={_button.reload}
                textDisable={_button.textDisable}
              />
            );
        }
      })}
    </Row>
  );
};

export type ButtonType = TableActionButtonType & {
  type: TYPE_BUTTON;
};

export enum TYPE_BUTTON {
  DELETE,
  CONFIRM,
  SIMPLE_MODAL,
  UPDATE,
  SEE,
  CUSTOM,
}

type DeleteButtonType = { row: any; _button: ButtonType };
const DeleteButtonPrivate = ({ row, _button }: DeleteButtonType) => {
  const { visible, show, close, ref } = useModal();
  const deleteModale: ModaleType = {
    close: close,
    ref: ref,
    show: show,
    visible: visible,
  };
  return (
    <TableActionColumn
      row={row}
      disabled={_button.disable ? _button.disable(row) : false}
      textDisable={_button.textDisable}
      textEnable={"Supprimer"}
      classEnable={"danger"}
      deleteModale={deleteModale}
      icon={<IconDelete />}
      query={`${_button.path}${row.value}`}
    />
  );
};

type ConfirmeButtonType = { row: any; _button: ButtonType };
const ConfirmButtonPrivate = ({ row, _button }: ConfirmeButtonType) => {
  const { visible, show, close, ref } = useModal();
  const confirmModale: ModaleType = {
    close: close,
    ref: ref,
    show: show,
    visible: visible,
  };
  return (
    <TableActionColumn
      isPost={_button.isPost}
      row={row}
      disabled={_button.disable ? _button.disable(row) : false}
      textDisable={_button.textDisable}
      textEnable={_button.textEnable}
      classEnable={_button.classEnable}
      confirmModale={confirmModale}
      icon={_button.icon ?? <IconClose />}
      query={`${_button.path}${row.value}`}
    />
  );
};

type SimpleModalButtonType = {
  row: any;
  _button: ButtonType;
};
const SimpleModalButtonPrivate = ({ row, _button }: SimpleModalButtonType) => {
  const { visible, show, close, ref } = useModal();
  const simpleModal: SimpleModalType = {
    close: close,
    ref: ref,
    show: show,
    visible: visible,
    content: _button.simpleModal.content(row.value),
    header: _button.simpleModal.header,
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
