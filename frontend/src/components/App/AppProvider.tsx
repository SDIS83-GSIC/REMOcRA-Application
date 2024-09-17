/*global userInfo */

import { createContext, ReactNode, useContext } from "react";

const AppContext = createContext({});

export const useAppContext = () => {
  return useContext(AppContext);
};

export const AppProvider = ({ children }: { children: ReactNode }) => {
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
