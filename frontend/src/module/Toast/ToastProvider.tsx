import React, {
  createContext,
  forwardRef,
  ReactNode,
  useContext,
  useImperativeHandle,
  useReducer,
  useRef,
} from "react";
import { ToastContainer } from "react-bootstrap";
import ToastAutohide from "./ToastAutoHide.tsx";

const ToastContext = createContext({});

export const useToastContext = () => {
  return useContext(ToastContext);
};

const toastReducer = (state, action) => {
  switch (action.type) {
    case "ADD_TOAST":
      return {
        ...state,
        toasts: [...state.toasts, action.payload],
      };
    default:
      return state;
  }
};

const ToastWrapper = forwardRef((props, ref) => {
  const [state, dispatch] = useReducer(toastReducer, { toasts: [] });

  const addToast = ({ header, type, message, delay = 3000 }) => {
    const key = Math.floor(Math.random() * 10000000);
    dispatch({
      type: "ADD_TOAST",
      payload: (
        <ToastAutohide
          key={key}
          header={header}
          content={message}
          variant={type}
          delay={delay}
        />
      ),
    });
  };

  useImperativeHandle(ref, () => ({
    addToastRef({ header, type, message, delay }) {
      addToast({ header, type, message, delay });
    },
  }));

  return (
    <ToastContainer position={"bottom-end"} className={"p-2"}>
      {state.toasts.map((toast) => (
        <>{toast}</>
      ))}
    </ToastContainer>
  );
});
ToastWrapper.displayName = "ToastWrapper";

export const ToastProvider = ({ children }: { children: ReactNode }) => {
  const toastRef = useRef();

  const addToast = ({ header, type, message, delay = 3000 }) => {
    toastRef.current?.addToastRef({ header, type, message, delay });
  };

  const info = (message, header = "Information", delay) => {
    addToast({ message, header, delay, type: "info" });
  };

  const success = (message, header = "Information", delay) => {
    addToast({ message, header, delay, type: "success" });
  };

  const warning = (message, header = "Information", delay) => {
    addToast({ message, header, delay, type: "warning" });
  };
  const error = (message, header = "Information", delay) => {
    addToast({ message, header, delay, type: "danger" });
  };

  return (
    <ToastContext.Provider value={{ success, warning, info, error }}>
      {children}
      <ToastWrapper ref={toastRef} />
    </ToastContext.Provider>
  );
};
