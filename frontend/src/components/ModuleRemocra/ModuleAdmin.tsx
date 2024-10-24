import { Outlet } from "react-router-dom";
import SquelettePage from "../../pages/SquelettePage.tsx";
import Header from "../Header/Header.tsx";

const ModuleAdmin = () => {
  return (
    <SquelettePage header={<Header />}>
      {/* TODO mettre les différents blocs avec le lien vers les pages d'administration */}

      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleAdmin;
