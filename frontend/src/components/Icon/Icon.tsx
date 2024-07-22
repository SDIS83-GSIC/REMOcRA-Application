import "@flaticon/flaticon-uicons/css/all/all.css";

export const IconClose = () => <i className="fi fi-rr-cross" />;
export const IconError = () => <i className="fi fi-rr-cross" />;
export const IconLoading = () => <i className="fi fi-br-loading" />;
export const IconSearch = () => <i className="fi fi-br-search" />;
export const IconOverview = () => <i className="fi fi-br-overview" />;
export const Toggle = ({ toggled = false }: { toggled: boolean }) => (
  <i className={toggled ? "fi fi-rr-toggle-on" : "fi fi-rr-toggle-off"} />
);

export const IconEdit = () => <i className="fi fi-br-pencil" />;
export const IconCreate = () => <i className="fi fi-br-add" />;
export const IconSee = () => <i className="fi fi-br-overview" />;
export const IconPei = () => <i className="fi fi-ss-fire-hydrant" />;

export const IconDocument = () => <i className="fi fi-br-document" />;
export const IconInfo = () => <i className="fi fi-br-interrogation" />;

export const IconAireAspiration = () => <i className="fi fi-rs-triangle" />;
