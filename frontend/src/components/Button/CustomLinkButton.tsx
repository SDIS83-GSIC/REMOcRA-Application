import classNames from "classnames";
import { ReactNode } from "react";
import type { ButtonVariant } from "react-bootstrap/types";
import LinkButton from "./LinkButton.tsx";

const CustomLinkButton = ({
  className,
  disabled = false,
  href,
  children,
  variant = "link",
  onClick,
}: CustomLinkButtonType) => {
  return (
    <LinkButton
      variant={variant}
      classname={classNames("text-decoration-none", className)}
      disabled={disabled}
      href={href}
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
  href?: string;
  children?: ReactNode;
  onClick?: (...args: any[]) => void;
};

export default CustomLinkButton;
