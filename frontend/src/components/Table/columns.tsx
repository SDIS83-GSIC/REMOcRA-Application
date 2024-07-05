import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import { IconEdit, IconSee } from "../Icon/Icon.tsx";

const EditColumn = ({
  to,
  canEdit = false,
  canEditFunction,
  title = true,
  ...option
}: EditColumnType) => ({
  // eslint-disable-next-line react/display-name
  Cell: (row: any) => {
    const canEditValue = canEditFunction ? canEditFunction(row.value) : canEdit;
    return (
      <>
        {canEditValue && (
          <Button variant="link" href={to(row.value)}>
            <IconEdit />
            {title && <>&nbsp;Modifier</>}
          </Button>
        )}
      </>
    );
  },
  width: title ? 140 : 90,
  ...option,
});

export default EditColumn;

type EditColumnType = {
  to: (id: string) => any;
  title?: boolean;
  accessor: string;
  canEdit?: boolean;
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

type SeeColumnType = {
  to: (id: string) => any;
  header?: ReactNode;
  title?: boolean;
};
