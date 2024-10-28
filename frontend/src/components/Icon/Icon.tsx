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
export const IconDelete = () => <i className="fi fi-br-trash" />;
export const IconCreate = () => <i className="fi fi-br-add" />;
export const IconSee = () => <i className="fi fi-br-overview" />;
export const IconPei = () => <i className="fi fi-ss-fire-hydrant" />;

export const IconDocument = () => <i className="fi fi-br-document" />;
export const IconInfo = () => <i className="fi fi-br-interrogation" />;

export const IconAireAspiration = () => <i className="fi fi-rs-triangle" />;
export const IconEtude = () => <i className="fi fi-br-survey-xmark" />;
export const IconImport = () => <i className="fi fi-br-upload" />;

export const IconIndisponibiliteTemporaire = () => (
  <i className="fi fi-bs-time-delete" />
);

export const IconCloseIndisponibiliteTemporaire = () => (
  <i className="fi fi-bs-hourglass-end" />
);
export const IconList = () => <i className="fi fi-br-list" />;

export const IconTournee = () => <i className="fi fi-br-document" />;
export const IconSortList = () => <i className="fi fi-br-apps-sort" />;
export const IconDragNDrop = () => (
  <i className="fi fi-br-grip-dots-vertical" />
);
export const IconDesaffecter = () => <i className="fi fi-br-user-slash" />;
export const IconZeroPourcent = () => <i className="fi fi-br-square-0" />;
export const IconCentPourcent = () => <i className="fi fi-br-percent-100" />;

export const IconLock = () => <i className="fi fi-br-lock" />;
export const IconUnlock = () => <i className="fi fi-br-unlock" />;

export const IconAddContact = () => <i className="fi fi-br-user-add" />;
export const IconGererContact = () => <i className="fi fi-br-users-gear" />;

export const IconNextPage = () => <i className="fi fi-rr-angle-circle-right" />;
export const IconPreviousPage = () => (
  <i className="fi fi-rr-angle-circle-left" />
);
