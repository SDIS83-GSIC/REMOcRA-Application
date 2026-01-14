import React, { useEffect, useMemo, useRef, useState } from "react";
import { Button, Col, Row } from "react-bootstrap";
import { Document, Page, pdfjs } from "react-pdf";
import "react-pdf/dist/Page/AnnotationLayer.css";
import "react-pdf/dist/Page/TextLayer.css";
import { IconCamera, IconExport, IconList, IconPrint } from "../Icon/Icon.tsx";
import TooltipCustom from "../Tooltip/Tooltip.tsx";
import styles from "./PdfViewer.module.css";

// Configuration du worker PDF.js
// Le fichier pdf.worker.min.js est déjà généré dans le build par Parcel
pdfjs.GlobalWorkerOptions.workerSrc = new URL(
  "npm:pdfjs-dist/build/pdf.worker.min.mjs",
  import.meta.url,
).toString();

const PdfViewer = ({ url }: { url: string }) => {
  const [numPages, setNumPages] = useState<number | null>(null);
  const [pageInput, setPageInput] = useState<string>("");
  const [isFullscreen, setIsFullscreen] = useState<boolean>(false);
  const [showThumbnails, setShowThumbnails] = useState<boolean>(false);
  const containerRef = useRef<HTMLDivElement>(null);
  const pageRefs = useRef<Map<number, HTMLDivElement>>(new Map());

  const options = useMemo(
    () => ({
      cMapUrl: `cmaps/`,
      cMapPacked: true,
      standardFontDataUrl: `standard_fonts/`,
    }),
    [],
  );

  // On met en cache le fichier pour éviter trop d'appels HTTP
  const fileConfig = useMemo(() => ({ url: url }), [url]);

  function onDocumentLoadSuccess({ numPages }: { numPages: number }): void {
    setNumPages(numPages);
  }

  const toggleFullscreen = async () => {
    if (!containerRef.current) {
      return;
    }

    if (!document.fullscreenElement) {
      // Entrer en plein écran
      await containerRef.current.requestFullscreen();
    } else {
      // Sortir du plein écran
      await document.exitFullscreen();
    }
  };

  // Écouter les changements de plein écran
  useEffect(() => {
    const handleFullscreenChange = () => {
      setIsFullscreen(!!document.fullscreenElement);
    };

    document.addEventListener("fullscreenchange", handleFullscreenChange);

    return () => {
      document.removeEventListener("fullscreenchange", handleFullscreenChange);
    };
  }, []);

  const goToPage = (pageNumber: number) => {
    if (pageNumber >= 1 && pageNumber <= (numPages ?? 0)) {
      const pageElement = pageRefs.current.get(pageNumber);
      if (pageElement) {
        pageElement.scrollIntoView({ behavior: "smooth", block: "start" });
      }
    }
  };

  const handlePageInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const value = e.target.value;
    setPageInput(value);
  };

  const handleGoToPageClick = () => {
    const pageNum = parseInt(pageInput);
    if (!isNaN(pageNum)) {
      goToPage(pageNum);
    }
  };

  const renderError = () => (
    <div className={styles.containerError}>
      <p className={styles.errorPdf}>Erreur lors du chargement du document</p>
    </div>
  );

  return (
    <div ref={containerRef} className={styles.container}>
      <div className={styles.header}>
        <Row>
          <Col>
            <TooltipCustom
              tooltipId="download-tooltip"
              tooltipText="Télécharger le PDF"
            >
              <Button
                href={url}
                target="_blank"
                className={
                  isFullscreen ? "btn bg-white text-primary" : "btn btn-primary"
                }
              >
                <IconExport />
              </Button>
            </TooltipCustom>
          </Col>
          <Col>
            <TooltipCustom
              tooltipId="print-tooltip"
              tooltipText="Imprimer le PDF"
            >
              <Button
                onClick={() => window.print()}
                className={
                  isFullscreen ? "btn bg-white text-primary" : "btn btn-primary"
                }
              >
                <IconPrint />
              </Button>
            </TooltipCustom>
          </Col>
          <Col>
            <TooltipCustom
              tooltipId="fullscreen-tooltip"
              tooltipText={
                isFullscreen ? "Quitter le plein écran" : "Plein écran"
              }
            >
              <Button
                onClick={toggleFullscreen}
                className={
                  isFullscreen ? "btn bg-white text-primary" : "btn btn-primary"
                }
              >
                <IconCamera />
              </Button>
            </TooltipCustom>
          </Col>
          <Col>
            <TooltipCustom
              tooltipId="thumbnails-tooltip"
              tooltipText={
                showThumbnails
                  ? "Masquer les miniatures"
                  : "Afficher les miniatures"
              }
            >
              <Button
                onClick={() => setShowThumbnails(!showThumbnails)}
                className={
                  isFullscreen ? "btn bg-white text-primary" : "btn btn-primary"
                }
              >
                <IconList />
              </Button>
            </TooltipCustom>
          </Col>
          <Col>
            <div className={styles.pageNavigation}>
              <input
                type="number"
                min="1"
                max={numPages ?? 1}
                value={pageInput}
                onChange={handlePageInputChange}
                placeholder="Page"
                className={styles.pageInput}
              />
              <Button
                onClick={handleGoToPageClick}
                size="sm"
                className="bg-primary"
              >
                Aller
              </Button>
            </div>
          </Col>
        </Row>
        {numPages && (
          <div className={styles.pageInfo}>
            Total : {numPages} page{numPages > 1 ? "s" : ""}
          </div>
        )}
      </div>
      <div className={styles.contentWrapper}>
        {showThumbnails && (
          <div className={styles.thumbnailsSidebar}>
            <Document
              file={fileConfig}
              onLoadSuccess={onDocumentLoadSuccess}
              options={options}
            >
              {numPages &&
                Array.from({ length: numPages }, (_, index) => (
                  <div
                    key={`thumb_${index + 1}`}
                    className={styles.thumbnailItem}
                    onClick={() => goToPage(index + 1)}
                  >
                    <Page
                      pageNumber={index + 1}
                      width={100}
                      renderTextLayer={false}
                      renderAnnotationLayer={false}
                    />
                    <div className={styles.thumbnailLabel}>
                      Page {index + 1}
                    </div>
                  </div>
                ))}
            </Document>
          </div>
        )}
        <div className={styles.scrollContainer}>
          <Document
            file={fileConfig}
            onLoadSuccess={onDocumentLoadSuccess}
            loading={<div>Chargement du PDF...</div>}
            error={renderError}
            className={styles.document}
            options={options}
          >
            {numPages &&
              Array.from({ length: numPages }, (_, index) => (
                <Page
                  key={`page_${index + 1}`}
                  pageNumber={index + 1}
                  className={styles.page}
                  inputRef={(ref) => {
                    if (ref) {
                      pageRefs.current.set(index + 1, ref);
                    }
                  }}
                />
              ))}
          </Document>
        </div>
      </div>
    </div>
  );
};
export default PdfViewer;
