import { useAppContext } from "../../components/App/AppProvider.tsx";
import Header from "../../components/Header/Header.tsx";
import LoginError from "../LoginError/LoginError.tsx";
import SquelettePage from "../SquelettePage.tsx";
import AccueilPrive from "./AccueilPrive.tsx";
import AccueilPublic from "./AccueilPublic.tsx";

const Accueil = () => {
  const { user } = useAppContext();

  return (
    <SquelettePage navbar={<Header />} fluid={true} banner={true}>
      {user != null ? (
        user.droits.length === 0 ? (
          <LoginError />
        ) : (
          <AccueilPrive user={user} />
        )
      ) : (
        <AccueilPublic />
      )}
    </SquelettePage>
  );
};

export default Accueil;
