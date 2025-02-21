import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./style.scss";

// FIXME: remettre en place <StrictMode> quand on n'utilisera plus react-async
// cf. src/components/Fetch/useFetch.tsx
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(
  <App />,
);
