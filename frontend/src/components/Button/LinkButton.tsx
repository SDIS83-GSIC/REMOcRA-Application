import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import type { ButtonVariant } from "react-bootstrap/types";
import { Link, useLocation } from "react-router-dom";

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
    <Link
      to={{ pathname: pathname }}
      state={{
        ...state,
        ...currentState,
        from: [...statePrevious, `${currentPathname}${search}`],
      }}
      className={`text-decoration-none btn btn-${variant} ${classname} text-nowrap`}
      onClick={onClick}
    >
      {children}
    </Link>
  ) : (
    <Button
      variant={variant}
      className={variant === "link" ? "text-muted" : ""}
      disabled={disabled}
    >
      {children}
    </Button>
  );
};

export default LinkButton;
