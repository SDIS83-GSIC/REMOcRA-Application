import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import {
  IconDelete,
  IconEdit,
  IconList,
  IconLock,
  IconUnlock,
} from "../Icon/Icon.tsx";
import DeleteModal from "../Modal/DeleteModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import { actionColumnType, columnType } from "./QueryTable.tsx";
import { ActionButton } from "./TableActionColumn.tsx";

const EditColumn = ({
  to,
  title = true,
  disabled = false,
  disable = () => false,
  textDisable = "",
  ...option
}: EditColumnType) => ({
  // eslint-disable-next-line react/display-name
  Cell: (row: any) => {
    return (
      <>
        <TooltipCustom
          tooltipText={!(disable(row) || disabled) ? "Modifier" : textDisable}
          tooltipId={row.value}
        >
          <Button
            variant="link"
            href={to(row.value)}
            disabled={disabled || disable(row)}
          >
            <IconEdit />
            {title && <>&nbsp;Modifier</>}
          </Button>
        </TooltipCustom>
      </>
    );
  },
  width: title ? 140 : 90,
  ...option,
});

type EditColumnType = {
  to: (id: string) => any;
  title?: boolean;
  accessor: string;
  textDisable?: string;
  disabled: boolean;
  disable: (t?: any) => boolean;
};

export default EditColumn;

export const LinkColumn = ({
  to,
  title = "",
  icon,
  disabled = false,
  disable = () => false,
  tooltipText = "",
  textDisable = "",
  ...option
}: LinkColumnType) => ({
  // eslint-disable-next-line react/display-name
  Cell: (row: any) => {
    const titleIsNotEmpty = title?.trim().length > 0;
    return (
      <>
        <TooltipCustom
          tooltipText={!(disable(row) || disabled) ? tooltipText : textDisable}
          tooltipId={row.value}
        >
          <Button
            variant="link"
            href={to(row.value)}
            disabled={disabled || disable(row)}
          >
            {icon}
            {titleIsNotEmpty && title}
          </Button>
        </TooltipCustom>
      </>
    );
  },
  width: title ? 140 : 90,
  ...option,
});

type LinkColumnType = {
  to: (id: string) => any;
  title?: string;
  icon: ReactNode;
  accessor: string;
  textDisable?: string;
  disabled: boolean;
  disable: (t?: any) => boolean;
  tooltipText?: string;
};

export const ListePeiColumn = ({
  handleButtonClick,
  accessor,
}: ListePeiColumnType) => ({
  Header: "",
  accessor: accessor,
  Cell: (value) => {
    return (
      <TooltipCustom tooltipText={"Lister les points d'eau"} tooltipId={value}>
        <Button
          variant={"link"}
          onClick={() => {
            handleButtonClick(value.value);
          }}
        >
          <IconList />
        </Button>
      </TooltipCustom>
    );
  },
});
type ListePeiColumnType = {
  handleButtonClick: (value: string) => any;
  accessor: string;
};

type DeleteColumnType = {
  path: string;
  reload?: boolean;
  title: boolean;
  accessor: string;
  disabled?: boolean;
  disable?: (t?: any) => boolean;
  textDisable?: string;
};
export const DeleteColumn = ({
  path,
  reload,
  title = true,
  disabled = false,
  disable = () => false,
  textDisable = "",
  ...options
}): DeleteColumnType => ({
  // eslint-disable-next-line react/display-name
  Cell: (row) => {
    const { visible, show, close, ref } = useModal();
    const query = `${path}${row.value}`;
    return (
      <>
        <TooltipCustom
          tooltipText={!disable(row) ? "Supprimer" : textDisable}
          tooltipId={row.value}
        >
          <Button
            variant={"link"}
            className={disabled || disable(row) ? "" : "text-danger"}
            disabled={disabled || disable(row)}
            onClick={show}
          >
            <IconDelete />
            {title && <>&nbsp;Supprimer</>}
          </Button>
        </TooltipCustom>
        {!disable(row) && (
          <DeleteModal
            visible={visible}
            closeModal={close}
            query={query}
            ref={ref}
            onDelete={() => (reload ? reload() : window.location.reload(false))}
          />
        )}
      </>
    );
  },
  width: title ? 160 : 90,
  ...options,
});

export const ActionColumn = ({
  Header,
  accessor,
  sortField,
  buttons,
  ...options
}: actionColumnType) => ({
  // eslint-disable-next-line react/display-name
  Header: Header,
  accessor: accessor,
  Cell: (row) => {
    return <ActionButton buttons={buttons} row={row} />;
  },
  sortField: sortField,
  width: 200,
  ...options,
});

export const BooleanColumn = ({
  Header,
  accessor,
  sortField,
  ...options
}: columnType) => ({
  // eslint-disable-next-line react/display-name
  Header: Header,
  accessor: accessor,
  Cell: (value) => {
    return (
      <div className="text-center">
        <Form.Check type="checkbox" disabled checked={value.value === true} />
      </div>
    );
  },
  sortField: sortField,
  ...options,
});

export const ProtectedColumn = ({
  Header,
  accessor,
  sortField,
  ...options
}: columnType) => ({
  // eslint-disable-next-line react/display-name
  Header: Header,
  accessor: accessor,
  Cell: (value: { value: any }) => {
    return (
      <>
        <div className="text-center">
          {value.value ? <IconLock /> : <IconUnlock />}
        </div>
      </>
    );
  },
  sortField: sortField,
  ...options,
});
