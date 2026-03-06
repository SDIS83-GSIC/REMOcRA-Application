import {
  createContext,
  forwardRef,
  ReactNode,
  useContext,
  useImperativeHandle,
  useReducer,
  useRef,
} from "react";
import { ToastContainer } from "react-bootstrap";
import { TypeToastEnum } from "../../enums/TypeToastEnum.tsx";
import ToastAutohide from "./ToastAutoHide.tsx";

const ToastContext = createContext<
  | {
      success: (message: string, header?: string, delay?: number) => void;
      error: (message: string, header?: string, delay?: number) => void;
      warning: (message: string, header?: string, delay?: number) => void;
      info: (message: string, header?: string, delay?: number) => void;
      persistent: (message: string, header?: string) => number;
      addToast: (options: {
        header: string;
        type: TypeToastEnum;
        message: string;
        delay?: number | null;
      }) => number;
      removeToast: (id: number) => void;
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
    case "REMOVE_TOAST":
      return {
        ...state,
        toasts: state.toasts.filter(
          (toast: { id: number }) => toast.id !== action.payload,
        ),
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
    type: TypeToastEnum;
    message: string;
    delay?: number | null;
  }) => {
    const key = Math.floor(Math.random() * 10000000);
    const toastDelay = type === TypeToastEnum.PERSISTENT ? null : delay;

    const handleRemoveToast = (id: number) => {
      dispatch({ type: "REMOVE_TOAST", payload: id });
    };

    dispatch({
      type: "ADD_TOAST",
      payload: {
        id: key,
        component: (
          <ToastAutohide
            key={key}
            id={key}
            header={header}
            content={message}
            variant={type === TypeToastEnum.PERSISTENT ? "info" : type}
            delay={toastDelay}
            onClose={handleRemoveToast}
          />
        ),
      },
    });
    return key;
  };

  const removeToast = (id: number) => {
    dispatch({ type: "REMOVE_TOAST", payload: id });
  };

  useImperativeHandle(ref, () => ({
    addToastRef({
      header,
      type,
      message,
      delay,
    }: {
      header: string;
      type: TypeToastEnum;
      message: string;
      delay?: number | null;
    }) {
      return addToast({ header, type, message, delay });
    },
    removeToastRef(id: number) {
      removeToast(id);
    },
  }));

  return (
    <ToastContainer position={"bottom-end"} className={"p-2"}>
      {state.toasts.map((toast: { id: number; component: ReactNode }) => (
        <div key={toast.id}>{toast.component}</div>
      ))}
    </ToastContainer>
  );
});
ToastWrapper.displayName = "ToastWrapper";

export const ToastProvider = ({ children }: { children: ReactNode }) => {
  const toastRef = useRef<{
    addToastRef: (options: {
      header: string;
      type: string;
      message: string;
      delay?: number | null;
    }) => number;
    removeToastRef: (id: number) => void;
  }>(null);

  const addToast = ({
    header,
    type,
    message,
    delay = 3000,
  }: {
    header: string;
    type: string;
    message: string;
    delay?: number | null;
  }): number => {
    return toastRef.current?.addToastRef({ header, type, message, delay }) || 0;
  };

  const removeToast = (id: number) => {
    toastRef.current?.removeToastRef(id);
  };

  const info = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: TypeToastEnum.INFO });
  };

  const success = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: TypeToastEnum.SUCCESS });
  };

  const warning = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: TypeToastEnum.WARNING });
  };
  const error = (message: string, header = "Information", delay?: number) => {
    addToast({ message, header, delay, type: TypeToastEnum.DANGER });
  };

  const persistent = (message: string, header = "Information"): number => {
    return addToast({ message, header, type: TypeToastEnum.PERSISTENT }) || 0;
  };

  return (
    <ToastContext.Provider
      value={{
        success,
        warning,
        info,
        error,
        persistent,
        addToast,
        removeToast,
      }}
    >
      {children}
      <ToastWrapper ref={toastRef} />
    </ToastContext.Provider>
  );
};
