import ReactDOM from "react-dom/client";
import App from "./App.tsx";
import "./style.scss";

// FIXME: remettre en place <StrictMode> quand on n'utilisera plus react-async
// cf. src/components/Fetch/useFetch.tsx
document
  .querySelector<HTMLLinkElement>('link[rel="icon"]')!
  .setAttribute("href", "/images/favicon");
const root = ReactDOM.createRoot(document.getElementById("root"));
root.render(<App />);
