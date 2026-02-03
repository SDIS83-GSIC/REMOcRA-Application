import Header from "../../components/Header/Header.tsx";
import SquelettePage from "../../pages/SquelettePage.tsx";

const NoMatch = () => {
  return (
    <SquelettePage navbar={<Header />} banner>
      <div>La page que vous demandez n&apos;a pas été trouvée.</div>
    </SquelettePage>
  );
};

export default NoMatch;
