import { Outlet } from "react-router-dom";
import Header from "../../components/Header/Header.tsx";
import SquelettePage from "../SquelettePage.tsx";

const Dashboard = () => {
  return (
    <SquelettePage navbar={<Header />}>
      <Outlet />
    </SquelettePage>
  );
};

export default Dashboard;
