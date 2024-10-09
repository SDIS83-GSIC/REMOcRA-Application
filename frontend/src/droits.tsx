import { useAppContext } from "./components/App/AppProvider.tsx";
import UtilisateurEntity, {
  TYPE_DROIT,
} from "./Entities/UtilisateurEntity.tsx";

export const Authorization = (
  { Component, droits, isPublic = false }: AuthorizationEntity,
  props: any[],
) => {
  const { user }: { user: UtilisateurEntity } = useAppContext();

  if (isPublic || (user && isAuthorized(user, droits ?? []))) {
    return <Component {...props} />;
  }

  return <Forbidden />;
};

type AuthorizationEntity = {
  Component: any;
  droits?: TYPE_DROIT[];
  isPublic?: boolean;
};

export function isAuthorized(user: UtilisateurEntity, droits: TYPE_DROIT[]) {
  return droits.some((droit) => hasDroit(user, droit));
}

export function hasDroit(user: UtilisateurEntity, droit: TYPE_DROIT) {
  return user.droits.includes(droit);
}

export const Forbidden = () => {
  return <div>Vous n&apos;avez pas le droit d&apos;accéder à cette page</div>;
};
