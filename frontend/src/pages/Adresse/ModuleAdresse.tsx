import Header from "../../components/Header/Header.tsx";
import SquelettePage from "../SquelettePage.tsx";
import MapAdresse from "../../components/Map/MapAdresses/MapAdresse.tsx";

const ModuleAdresse = () => {
  return (
    <SquelettePage navbar={<Header />} fluid={true}>
      <MapAdresse />
    </SquelettePage>
  );
};

export default ModuleAdresse;
