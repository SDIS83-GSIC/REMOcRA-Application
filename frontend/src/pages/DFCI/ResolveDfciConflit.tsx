import { useParams } from "react-router-dom";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconMerge } from "../../components/Icon/Icon.tsx";
import { DfciConflitEntity } from "../../Entities/DfciConflitEntity.tsx";
import url from "../../module/fetch.tsx";
import { URLS } from "../../routes.tsx";
import {
  DfciConflit,
  getInitialValuesDfciConflit,
  prepareVariablesDfciConflit,
  validationSchemaDfciConflit,
} from "./DfciConflit.tsx";

export const ResolveDfciConflit = () => {
  const { dfciConflitId } = useParams();

  const { data }: { data?: DfciConflitEntity } = useGet(
    url`/api/dfci-conflit/` + dfciConflitId,
  );

  if (!data) {
    return <p>Chargement en cours…</p>;
  }

  return (
    <>
      <PageTitle icon={<IconMerge />} title={"Résoudre le conflit"} />
      <MyFormik
        initialValues={getInitialValuesDfciConflit(data)}
        isPost={false}
        prepareVariables={prepareVariablesDfciConflit}
        validationSchema={validationSchemaDfciConflit}
        onSubmit={() => null}
        submitUrl="/api/dfci-conflit/resolve"
        redirectUrl={URLS.DFCI_GESTION_CONFLITS}
      >
        <DfciConflit />
      </MyFormik>
    </>
  );
};
