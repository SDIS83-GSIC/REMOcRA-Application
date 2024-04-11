import { useRoutes } from "react-router-dom";
import PropTypes from "prop-types";

const RouteConfig = ({ routes }) => {
  return useRoutes(routes);
};

RouteConfig.propTypes = {
  routes: PropTypes.arrayOf(
    PropTypes.shape({
      path: PropTypes.string.isRequired,
      element: PropTypes.node.isRequired,
    }),
  ),
};

export default RouteConfig;
