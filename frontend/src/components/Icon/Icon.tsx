import "@flaticon/flaticon-uicons/css/all/all.css";

export const IconClose = () => <i className="fi fi-rr-cross" />;
export const IconError = () => <i className="fi fi-rr-cross" />;
export const IconLoading = () => <i className="fi fi-br-loading" />;
export const IconSearch = () => <i className="fi fi-br-search" />;
export const Toggle = ({ toggled = false }: { toggled: boolean }) => (
  <i className={toggled ? "fi fi-rr-toggle-on" : "fi fi-rr-toggle-off"} />
);

export const IconEdit = () => <i className="fi fi-br-pencil" />;
export const IconSee = () => <i className="fi fi-br-overview" />;
