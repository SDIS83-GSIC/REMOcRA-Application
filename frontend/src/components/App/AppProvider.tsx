/*global userInfo */

import { createContext, useContext } from "react";
import PropTypes from "prop-types";

const AppContext = createContext({});

export const useAppContext = () => {
  return useContext(AppContext);
};

export const AppProvider = ({ children }) => {
  return (
    <AppContext.Provider
      value={{
        user: userInfo,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

AppProvider.propTypes = {
  children: PropTypes.node,
};
