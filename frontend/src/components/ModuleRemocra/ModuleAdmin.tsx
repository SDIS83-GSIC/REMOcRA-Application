import { Outlet } from "react-router-dom";
import SquelettePage from "../../pages/SquelettePage.tsx";
import Header from "../Header/Header.tsx";

const ModuleAdmin = () => {
  return (
    <SquelettePage header={<Header />}>
      {/* Outlet permet de faire référence à la page enfant sélectionnée */}
      <Outlet />
    </SquelettePage>
  );
};

export default ModuleAdmin;
