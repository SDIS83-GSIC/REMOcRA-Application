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

const ToastContext = createContext<
  | {
      success: (message: string) => void;
      error: (message: string) => void;
      warning: (message: string) => void;
      info: (message: string) => void;
    }
  | undefined
>(undefined);

export const useToastContext = () => {
  const context = useContext(ToastContext);
  if (!context) {
    throw new Error("useToastContext must be used within a ToastProvider");
  }
  return context;
};

const toastReducer = (state: any, action: any) => {
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

  const addToast = ({
    header,
    type,
    message,
    delay = 3000,
  }: {
    header: string;
    type: string;
    message: string;
    delay?: number;
  }) => {
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
    addToastRef({
      header,
      type,
      message,
      delay,
    }: {
      header: string;
      type: string;
      message: string;
      delay?: number;
    }) {
      addToast({ header, type, message, delay });
    },
  }));

  return (
    <ToastContainer position={"bottom-end"} className={"p-2"}>
      {state.toasts.map((toast: ReactNode) => (
        <>{toast}</>
      ))}
    </ToastContainer>
  );
});
ToastWrapper.displayName = "ToastWrapper";

export const ToastProvider = ({ children }: { children: ReactNode }) => {
  const toastRef = useRef<any>(null);

  const addToast = ({
    header,
    type,
    message,
    delay = 3000,
  }: {
    header: string;
    type: string;
    message: string;
    delay?: number;
  }) => {
    toastRef.current?.addToastRef({ header, type, message, delay });
  };

  const info = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: "info" });
  };

  const success = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: "success" });
  };

  const warning = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: "warning" });
  };
  const error = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: "danger" });
  };

  return (
    <ToastContext.Provider value={{ success, warning, info, error }}>
      {children}
      <ToastWrapper ref={toastRef} />
    </ToastContext.Provider>
  );
};
