import { useFormikContext } from "formik";
import { useState } from "react";
import { Button, Col, Container, Row } from "react-bootstrap";
import { array, object } from "yup";
import AccordionCustom, {
  useAccordionState,
} from "../../../components/Accordion/Accordion.tsx";
import { useAppContext } from "../../../components/App/AppProvider.tsx";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { FileInput, FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import TransferList, {
  ItemType,
} from "../../../components/Form/TransferList.tsx";
import { IconExport, IconParametre } from "../../../components/Icon/Icon.tsx";
import DeleteModal from "../../../components/Modal/DeleteModal.tsx";
import useModal from "../../../components/Modal/ModalUtils.tsx";
import { hasDroit } from "../../../droits.tsx";
import UtilisateurEntity from "../../../Entities/UtilisateurEntity.tsx";
import TYPE_DROIT from "../../../enums/DroitEnum.tsx";
import { useToastContext } from "../../../module/Toast/ToastProvider.tsx";
import { requiredFile } from "../../../module/validators.tsx";
import { downloadOutputFile } from "../../../utils/fonctionsUtils.tsx";
import { LabelColonnesAttenduesZIP } from "./LabelColonnesAttenduesZIP.tsx";
import VerificationImportAtlasZip from "./VerificationImportAtlasZip.tsx";

const getInitialValues = (): {
  importAtlasZip: string | null;
} => ({
  importAtlasZip: null,
});

type AtlasImportResponseSchema = {
  importZipData?: {
    errors?: {
      message: string;
    }[];
  };
};

const validationSchema = object({
  importAtlasZip: requiredFile, // "Le fichier ZIP est requis
});

const prepareVariables = (values: { importAtlasZip: File | null }) => {
  const formData = new FormData();
  if (values.importAtlasZip) {
    formData.append("importAtlasZip", values.importAtlasZip);
  }
  return formData;
};

export const AtlasManagement = () => {
  const { user } = useAppContext();
  const { handleShowClose, activesKeys } = useAccordionState(
    Array(1).fill(false),
  );
  const [result, setResult] = useState<AtlasImportResponseSchema | null>(null);
  const { isLoading, data: hasElements = false } = useGet(
    "/api/atlas/has-element",
    {},
  );

  const importHasErrors = (result?.importZipData?.errors?.length ?? 0) > 0;
  const importSucceeded = result !== null && !importHasErrors;
  const shouldShowAnnexesOrder =
    (hasElements || importSucceeded) &&
    (hasDroit(user, TYPE_DROIT.ATLAS_C) || hasDroit(user, TYPE_DROIT.ATLAS_A));
  const listeAccordion = [];
  listeAccordion.push({
    header: "Importer un fichier ZIP",
    content: (
      <FormikImportZip
        user={user!}
        setResult={setResult}
        hasElements={hasElements}
      />
    ),
  });

  if (shouldShowAnnexesOrder) {
    listeAccordion.push({
      header: "Ordre des annexes",
      content: <AnnexesOrder />,
    });
  }

  if (isLoading) {
    return <Loading />;
  }

  return (
    <Container>
      <PageTitle title="Gestion de l'Atlas" icon={<IconParametre />} />

      <p>
        Cet écran permet la gestion des ressources utilisées pour
        l&apos;affichage de l&apos;Atlas.
      </p>
      <AccordionCustom
        activesKeys={activesKeys}
        handleShowClose={handleShowClose}
        list={listeAccordion}
      />

      {result && <VerificationImportAtlasZip data={result} />}
    </Container>
  );
};

const FormikImportZip = ({
  user,
  hasElements,
  setResult,
}: {
  user: UtilisateurEntity;
  hasElements?: boolean;
  setResult: { (value: AtlasImportResponseSchema): void };
}) => {
  const { visible, show, close } = useModal();

  return (
    <Container>
      {hasElements ? (
        <p className="text-danger">
          Attention : l&apos;importation d&apos;un fichier ZIP va écraser les
          fichiers existants.
        </p>
      ) : (
        <p className="text-muted">Aucune donnée enregistrée.</p>
      )}

      <ButtonDownloadTemplate />

      {hasDroit(user, TYPE_DROIT.ATLAS_D) && hasElements && (
        <>
          <Button
            className="mb-3 ms-3"
            onClick={() => {
              show();
            }}
          >
            Supprimer les documents
          </Button>

          <DeleteModal
            onCancel={close}
            visible={visible}
            query={`/api/atlas/delete/`}
            header={"Atlas"}
            content={`Confirmez-vous la purge de l'Atlas ?`}
            onDelete={() => {
              window.location.reload();
            }}
            successLibelle={"L'Atlas a été purgé avec succès."}
          />
        </>
      )}

      {(hasDroit(user, TYPE_DROIT.ATLAS_A) ||
        hasDroit(user, TYPE_DROIT.ATLAS_C)) && (
        <MyFormik
          initialValues={getInitialValues()}
          validationSchema={validationSchema}
          isPost={true}
          isMultipartFormData={true}
          submitUrl={`/api/atlas/import-atlas-zip`}
          prepareVariables={prepareVariables}
          onSubmit={(result) => {
            setResult(result);
          }}
        >
          <ImportAtlasFormik />
        </MyFormik>
      )}
    </Container>
  );
};

const ImportAtlasFormik = () => {
  const { setFieldValue } = useFormikContext();
  return (
    <FormContainer>
      <FileInput
        label={<LabelColonnesAttenduesZIP />}
        name="importAtlasZip"
        accept=".zip"
        required={true}
        onChange={(e) =>
          setFieldValue("importAtlasZip", e.target.files?.[0] ?? null)
        }
      />
      <Row className="mt-3">
        <Col className="text-center">
          <SubmitFormButtons submitTitle={"Valider l'import"} />
        </Col>
      </Row>
    </FormContainer>
  );
};

const ButtonDownloadTemplate = () => {
  const { success: successToast, error: errorToast } = useToastContext();

  return (
    <Button
      className="mb-3"
      onClick={() =>
        downloadOutputFile(
          "/api/atlas/download-atlas-zip-template",
          null,
          "importAtlasZip.zip",
          "Export terminé",
          successToast,
          errorToast,
        )
      }
    >
      Télécharger un modèle
      <IconExport />
    </Button>
  );
};

type AtlasAnnexe = {
  atlasAnnexeActif: boolean;
  atlasAnnexeDocumentId: string | null;
  atlasAnnexeId: string;
  atlasAnnexeIsVisible: boolean;
  atlasAnnexeName: string | null;
  atlasAnnexeOrder: number | null;
};

type AnnexesFormValues = {
  downloadAtlasAnnexe: ItemType[];
};

const mapAtlasAnnexeToItem = (annexe: AtlasAnnexe): ItemType => ({
  id: annexe.atlasAnnexeId,
  libelle: annexe.atlasAnnexeName ?? "",
});

const prepareVariablesDownload = (
  selectedItems: ItemType[],
  annexes: AtlasAnnexe[],
): AtlasAnnexe[] => {
  if (selectedItems.length === 0) {
    return annexes;
  }

  return annexes.map((annexe) => {
    const selectedIndex = selectedItems.findIndex(
      (item) => item.id === annexe.atlasAnnexeId,
    );

    return {
      ...annexe,
      atlasAnnexeIsVisible: selectedIndex !== -1,
      atlasAnnexeOrder: selectedIndex !== -1 ? selectedIndex + 1 : null,
    };
  });
};

const AnnexesOrder = () => {
  const { data: atlasDataAnnexes, isLoading } = useGet(
    "/api/atlas/atlas-documents-annexes",
    {},
  );

  if (isLoading || !atlasDataAnnexes) {
    return <Loading />;
  }

  return (
    <MyFormik
      initialValues={{ downloadAtlasAnnexe: [] }}
      validationSchema={object({ downloadAtlasAnnexe: array().required() })}
      isPost={true}
      submitUrl="/api/atlas/update-pagination-atlas-annexes"
      prepareVariables={(values: AnnexesFormValues) =>
        prepareVariablesDownload(values.downloadAtlasAnnexe, atlasDataAnnexes)
      }
      onSubmit={() => null}
    >
      <AnnexesOrderForm atlasDataAnnexes={atlasDataAnnexes} />
    </MyFormik>
  );
};

const AnnexesOrderForm = ({
  atlasDataAnnexes,
}: {
  atlasDataAnnexes: AtlasAnnexe[];
}) => {
  const { setFieldValue } = useFormikContext<AnnexesFormValues>();

  const [selectedOptions, setSelectedOptions] = useState<ItemType[]>(
    atlasDataAnnexes
      .filter((annexe) => annexe.atlasAnnexeIsVisible)
      .sort((a, b) => (a.atlasAnnexeOrder ?? 0) - (b.atlasAnnexeOrder ?? 0))
      .map(mapAtlasAnnexeToItem),
  );

  const [availableOptions, setAvailableOptions] = useState<ItemType[]>(
    atlasDataAnnexes
      .filter((annexe) => !annexe.atlasAnnexeIsVisible)
      .map(mapAtlasAnnexeToItem),
  );

  const handleSelectedOptions = (
    options: ItemType[] | ((precedent: ItemType[]) => ItemType[]),
  ) => {
    setSelectedOptions((precedent) => {
      // si on fournit une fonction, elle reçoit la valeur précédente pour calculer
      // la nouvelle valeur. Sinon la valeur passée est utilisée directement.
      const newOptions =
        typeof options === "function" ? options(precedent) : options;

      setFieldValue("downloadAtlasAnnexe", newOptions);

      return newOptions;
    });
  };

  return (
    <FormContainer>
      <TransferList
        availableOptions={availableOptions}
        selectedOptions={selectedOptions}
        setAvailableOptions={setAvailableOptions}
        setSelectedOptions={handleSelectedOptions}
        label="Pages des annexes à inclure."
        tooltipText="Sélectionnez les pages des annexes que vous souhaitez inclure dans le PDF de l'atlas. L'ordre des pages sera pris en compte."
        required={false}
        name="downloadAtlasAnnexe"
        titleColumnAvailable="Pages d'annexes disponibles"
        titleColumnSelected="Pages sélectionnées"
      />

      <SubmitFormButtons submitTitle="Valider" />
    </FormContainer>
  );
};
