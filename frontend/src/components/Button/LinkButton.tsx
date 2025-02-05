import { ReactNode } from "react";
import { Button } from "react-bootstrap";
import { Link, useLocation } from "react-router-dom";

const LinkButton = ({
  href,
  children,
  variant = "primary",
  classname,
  onClick,
  disabled = false,
}: {
  href: string;
  children: ReactNode;
  variant: string;
  classname: string;
  onClick: (e: any) => void;
  disabled: boolean;
}) => {
  const { pathname, search, state } = useLocation();
  const statePrevious = state?.from ?? [];
  return !disabled ? (
    <Link
      to={{ pathname: href }}
      state={{
        ...state,
        from: [...statePrevious, `${pathname}${search}`],
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
