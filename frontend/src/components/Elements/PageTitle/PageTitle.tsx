import { ReactNode } from "react";
import styles from "./PageTitle.module.css";

type TitlePageModel = {
  title: string;
  icon: ReactNode;
  right?: ReactNode;
};

const PageTitle = ({ title, icon, right }: TitlePageModel) => {
  return (
    <div className={styles.pageTitle}>
      <h1 className={styles.title}>
        <span className={styles.icon}>{icon}</span> {title}
      </h1>
      {right ? <div className={styles.right}>{right}</div> : ""}
    </div>
  );
};

export default PageTitle;
