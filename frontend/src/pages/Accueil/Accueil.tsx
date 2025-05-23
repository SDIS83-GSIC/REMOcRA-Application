import { useAppContext } from "../../components/App/AppProvider.tsx";
import Header from "../../components/Header/Header.tsx";
import SquelettePage from "../SquelettePage.tsx";
import AccueilPrive from "./AccueilPrive.tsx";
import AccueilPublic from "./AccueilPublic.tsx";

const Accueil = () => {
  const { user } = useAppContext();

  return (
    <SquelettePage navbar={<Header />} fluid={true} banner={true}>
      {user != null ? <AccueilPrive user={user} /> : <AccueilPublic />}
    </SquelettePage>
  );
};

export default Accueil;
