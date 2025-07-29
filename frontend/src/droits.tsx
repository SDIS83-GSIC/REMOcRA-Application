import { useLocation, Navigate } from "react-router-dom";
import { useAppContext } from "./components/App/AppProvider.tsx";
import UtilisateurEntity from "./Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "./enums/DroitEnum.tsx";
import { URLS } from "./routes.tsx";
import SquelettePage from "./pages/SquelettePage.tsx";
import Header from "./components/Header/Header.tsx";

export const Authorization = (
  { Component, droits, isPublic = false }: AuthorizationEntity,
  props: any[],
) => {
  const { user } = useAppContext();
  const location = useLocation();

  if (!user && !isPublic) {
    return <Navigate to={URLS.ACCUEIL} state={location.state} replace />;
  }

  if (isPublic || (user && isAuthorized(user, droits ?? []))) {
    return <Component {...props} />;
  }

  return (
    <>
      <SquelettePage navbar={<Header />} fluid={true} banner={true}>
        <Forbidden />
      </SquelettePage>
    </>
  );
};

type AuthorizationEntity = {
  Component: any;
  droits?: TYPE_DROIT[];
  isPublic?: boolean;
};

export function isAuthorized(
  user: UtilisateurEntity | null | undefined,
  droits: TYPE_DROIT[],
) {
  return user ? droits.some((droit) => hasDroit(user, droit)) : false;
}

export function hasDroit(
  user: UtilisateurEntity | null | undefined,
  droit: TYPE_DROIT,
) {
  return user ? user.droits.includes(droit) : false;
}

export const Forbidden = () => {
  return <div>Vous n&apos;avez pas le droit d&apos;accéder à cette page</div>;
};
