import { ReactNode } from "react";
import type { ButtonVariant } from "react-bootstrap/types";
import { NavLink, useLocation } from "react-router-dom";
import classNames from "classnames";

const LinkButton = ({
  pathname,
  state = {},
  children,
  variant = "primary",
  classname,
  onClick,
  disabled = false,
}: {
  pathname: string;
  state?: object;
  children: ReactNode;
  variant?: string | ButtonVariant;
  classname?: string;
  onClick?: (...args: any[]) => void;
  disabled?: boolean;
}) => {
  const {
    pathname: currentPathname,
    search,
    state: currentState,
  } = useLocation();
  const statePrevious = currentState?.from ?? [];
  return !disabled ? (
    <NavLink
      to={{ pathname: pathname }}
      state={{
        ...currentState,
        ...state,
        from: [...statePrevious, `${currentPathname}${search}`],
      }}
      className={classNames(
        `text-decoration-none btn btn-${variant} ${classname} text-nowrap`,
      )}
      onClick={onClick}
    >
      {children}
    </NavLink>
  ) : (
    <NavLink
      variant={variant}
      className={variant === "link" ? "text-muted" : ""}
      disabled={disabled}
    >
      {children}
    </NavLink>
  );
};

export default LinkButton;
