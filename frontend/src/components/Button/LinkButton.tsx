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
    search: currentSearch,
    state: currentState,
  } = useLocation();
  const statePrevious = currentState?.from ?? [];

  // Si on a des search param pour la page destination, on les charge
  const newSearch = localStorage.getItem(pathname);

  // Puis on sauvegarde les nouveaux search de la page courrante
  localStorage.setItem(currentPathname, currentSearch);

  return (
    <NavLink
      to={{
        pathname: pathname,
        search: newSearch ?? "",
      }}
      state={{
        ...currentState,
        ...state,
        from: [...statePrevious, `${currentPathname}${currentSearch}`],
      }}
      className={classNames(
        `text-decoration-none btn btn-${variant} ${classname} text-nowrap`, //class général
        disabled ? "disabled pointer-events-none" : "", // class quand disable
      )}
      onClick={onClick}
    >
      {children}
    </NavLink>
  );
};

export default LinkButton;
