import classnames from "classnames";
import { Spinner } from "react-bootstrap";
import styles from "./Loading.module.css";

const Loading = ({ className, ...rest }: { className: string }) => (
  <div className={classnames(styles.loading, className)} {...rest}>
    <Spinner animation="border" />;
  </div>
);

export default Loading;
