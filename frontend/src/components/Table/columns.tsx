import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import Form from "react-bootstrap/Form";
import { IconDelete, IconEdit, IconList, IconSee } from "../Icon/Icon.tsx";
import DeleteModal from "../Modal/DeleteModal.tsx";
import useModal from "../Modal/ModalUtils.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import { columnType } from "./QueryTable.tsx";

const EditColumn = ({
  to,
  canEdit = false,
  canEditFunction,
  title = true,
  disabled = false,
  disable = () => false,
  textDisable = "",
  ...option
}: EditColumnType) => ({
  // eslint-disable-next-line react/display-name
  Cell: (row: any) => {
    const canEditValue = canEditFunction ? canEditFunction(row.value) : canEdit;
    return (
      <>
        <TooltipCustom
          tooltipText={!(disable(row) || disabled) ? "Modifier" : textDisable}
          tooltipId={row.value}
        >
          {canEditValue && (
            <Button
              variant="link"
              href={to(row.value)}
              disabled={disabled || disable(row)}
            >
              <IconEdit />
              {title && <>&nbsp;Modifier</>}
            </Button>
          )}
        </TooltipCustom>
      </>
    );
  },
  width: title ? 140 : 90,
  ...option,
});

export default EditColumn;

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
type EditColumnType = {
  to: (id: string) => any;
  title?: boolean;
  accessor: string;
  canEdit?: boolean;
  textDisable?: string;
  disabled: boolean;
  disable: (t?: any) => boolean;
  canEditFunction?: (t: any) => boolean;
};

export const SeeColumn = ({
  to,
  title = true,
  header,
  ...options
}: SeeColumnType) => ({
  // eslint-disable-next-line react/display-name
  Header: header,
  Cell: (row) => {
    return row?.value ? (
      <Button variant="link" href={to(row.value)}>
        <IconSee />
        {title && <>&nbsp;Voir</>}
      </Button>
    ) : null;
  },
  width: title ? 70 : 50,
  ...options,
});

type DeleteColumnType = {
  path: string;
  reload: boolean;
  title: boolean;
  canSupress: boolean;
  disabled: boolean;
  disable: (t?: any) => boolean;
  textDisable: string;
};
export const DeleteColumn = ({
  path,
  reload,
  title = true,
  canSupress,
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
        {canSupress && (
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
                onDelete={() =>
                  reload ? reload() : window.location.reload(false)
                }
              />
            )}
          </>
        )}
      </>
    );
  },
  width: title ? 160 : 90,
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

type SeeColumnType = {
  to: (id: string) => any;
  header?: ReactNode;
  title?: boolean;
};
