import { Button } from "react-bootstrap";
import classNames from "classnames";
import Col from "react-bootstrap/Col";
import type { ButtonVariant } from "react-bootstrap/types";
import { ReactNode } from "react";

const CustomLinkButton = ({
  className,
  disabled,
  href,
  children,
  onClick,
}: CustomLinkButtonType) => {
  return (
    <Col>
      <Button
        variant={"link"}
        className={classNames("text-decoration-none", className)}
        disabled={disabled}
        href={href}
        onClick={onClick}
      >
        {children}
      </Button>
    </Col>
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
