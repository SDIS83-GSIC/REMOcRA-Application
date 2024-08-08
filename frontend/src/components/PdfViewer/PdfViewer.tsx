import React, { ReactElement } from "react";
import {
  defaultLayoutPlugin,
  ToolbarProps,
  ToolbarSlot,
} from "@react-pdf-viewer/default-layout";
import { pdfjs } from "react-pdf";
import {
  LocalizationMap,
  ProgressBar,
  Viewer,
  Worker,
} from "@react-pdf-viewer/core";
import "@react-pdf-viewer/core/lib/styles/index.css";
import "@react-pdf-viewer/default-layout/lib/styles/index.css";
import classNames from "classnames";
import fr_FR from "./Traduction_FR.json";
import styles from "./PdfViewer.module.css";

const renderError = () => {
  return (
    <div className={styles.containerError}>
      <p className={styles.errorPdf}>Erreur lors du chargement du document</p>
    </div>
  );
};
const PdfViewer = ({ url }: { url: string }) => {
  const renderToolbar = (Toolbar: (props: ToolbarProps) => ReactElement) => (
    <Toolbar>
      {(slots: ToolbarSlot) => {
        const {
          CurrentPageInput,
          Download,
          Print,
          EnterFullScreen,
          GoToNextPage,
          GoToPreviousPage,
          NumberOfPages,
          ShowSearchPopover,
          Zoom,
          ZoomIn,
          ZoomOut,
        } = slots;
        return (
          <div
            style={{
              alignItems: "center",
              display: "flex",
              width: "100%",
            }}
          >
            <div className={styles.btnPdf}>
              <ShowSearchPopover />
            </div>
            <div className={styles.btnPdf}>
              <ZoomOut />
            </div>
            <div className={styles.btnPdf}>
              <Zoom />
            </div>
            <div className={styles.btnPdf}>
              <ZoomIn />
            </div>
            <div className={classNames(styles.btnPdf, styles.btnRightPdf)}>
              <GoToPreviousPage />
            </div>
            <div className={styles.btnPdf}>
              <CurrentPageInput /> / <NumberOfPages />
            </div>
            <div className={styles.btnPdf}>
              <GoToNextPage />
            </div>
            <div className={classNames(styles.btnPdf, styles.btnRightPdf)}>
              <EnterFullScreen />
            </div>
            <>
              <div className={styles.btnPdf}>
                <Download />
              </div>

              <div className={styles.btnPdf}>
                <Print />
              </div>
            </>
          </div>
        );
      }}
    </Toolbar>
  );

  const defaultLayoutPluginInstance = defaultLayoutPlugin({
    renderToolbar,
  });

  return (
    <div className={styles.worker}>
      <Worker
        workerUrl={`https://unpkg.com/pdfjs-dist@${pdfjs.version}/build/pdf.worker.min.js`}
      >
        <div className={styles.height100}>
          <Viewer
            localization={fr_FR as LocalizationMap}
            fileUrl={url}
            plugins={[defaultLayoutPluginInstance]}
            renderError={renderError}
            renderLoader={(percentages: number) => (
              <div className={styles.progressBar}>
                <ProgressBar progress={Math.round(percentages)} />
              </div>
            )}
          />
        </div>
      </Worker>
    </div>
  );
};
export default PdfViewer;
