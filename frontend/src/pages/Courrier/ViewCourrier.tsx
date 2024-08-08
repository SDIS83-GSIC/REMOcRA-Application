import { Container } from "react-bootstrap";
import PdfViewer from "../../components/PdfViewer/PdfViewer.tsx";
import { useUrlCourrier } from "./GenereCourrier.tsx";

const ViewCourrier = () => {
  const { urlCourrier } = useUrlCourrier();

  if (urlCourrier == null) {
    return;
  }

  return (
    <Container>
      <PdfViewer url={urlCourrier.url} />
    </Container>
  );
};

export default ViewCourrier;
