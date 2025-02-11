import classNames from "classnames";
import { ReactNode } from "react";
import type { ButtonVariant } from "react-bootstrap/types";
import LinkButton from "./LinkButton.tsx";

const CustomLinkButton = ({
  className,
  disabled = false,
  pathname,
  children,
  state,
  variant = "link",
  onClick,
}: CustomLinkButtonType) => {
  return (
    <LinkButton
      variant={variant}
      classname={classNames("text-decoration-none", className)}
      disabled={disabled}
      pathname={pathname}
      state={state}
      onClick={onClick}
    >
      {children}
    </LinkButton>
  );
};

type CustomLinkButtonType = {
  variant?: ButtonVariant;
  className?: string;
  disabled?: boolean;
  pathname: string;
  state?: object;
  children: ReactNode;
  onClick?: (...args: any[]) => void;
};

export default CustomLinkButton;
