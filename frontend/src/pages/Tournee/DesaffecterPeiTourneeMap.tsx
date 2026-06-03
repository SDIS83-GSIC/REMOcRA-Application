import { useFormikContext } from "formik";
import { useMemo } from "react";
import { Button, Container, FormCheck, Table } from "react-bootstrap";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import { IconTournee } from "../../components/Icon/Icon.tsx";
import { PeiSelect } from "../../components/Map/MapPei/MapToolbarPei.tsx";
import useModal from "../../components/Modal/ModalUtils.tsx";
import SimpleConfirmModal from "../../components/Modal/SimpleConfirmModal.tsx";
import url from "../../module/fetch.tsx";

export type TourneeInfos = {
  idTournee: string;
  libelleTournee: string;
  organismeTournee: string;
};

export type PeiAvecTournees = {
  peiId: string;
  peiNumeroComplet: string;
  tournees: TourneeInfos[];
};

export const getInitialValues = (dataPeis?: PeiAvecTournees[]) => ({
  peiData: dataPeis
    ? dataPeis.map((pei) => ({
        ...pei,
        tournees: pei.tournees.map((tournee) => ({ ...tournee })),
      }))
    : [],
});

const DesaffecterPeiTourneeMap = ({
  listePei,
  closeVolet,
}: {
  listePei: PeiSelect[];
  closeVolet: () => void;
}) => {
  const ids = listePei?.map((pei: { peiId: string }) => pei.peiId) ?? [];
  const { data: dataPeis } = useGet(
    ids.length
      ? url`/api/pei-desaffecter-tournee/get-pei-tournee?${{ peiIds: JSON.stringify(ids) }}`
      : "",
  );

  return (
    <Container>
      <PageTitle
        displayReturnButton={false}
        icon={<IconTournee />}
        title={"Désaffecter les PEI de leur tournée"}
      />

      <div className="bg-light border p-2 rounded">
        Les points d'eau suivants vont être désaffectés de leurs tournées
        sélectionnées.
      </div>

      <MyFormik
        isPost={false}
        isMultipartFormData={false}
        submitUrl={`/api/pei-desaffecter-tournee/desaffecter-tournee`}
        onSubmit={closeVolet}
        prepareVariables={(values) => prepareVariables(values)}
        initialValues={getInitialValues(dataPeis)}
      >
        <DesaffecterPeiTourneeForm allPeiData={dataPeis} />
      </MyFormik>
    </Container>
  );
};

const DesaffecterPeiTourneeForm = ({
  allPeiData,
}: {
  allPeiData: PeiAvecTournees[] | null;
}) => {
  const { values, setFieldValue, submitForm } = useFormikContext<{
    peiData: PeiAvecTournees[];
  }>();
  const { visible, show, close, ref } = useModal();

  const initPeiData = useMemo(() => {
    if (!allPeiData) {
      return [];
    }
    return allPeiData.flatMap((pei) =>
      pei.tournees.map((tournee) => ({
        peiId: pei.peiId,
        peiNumeroComplet: pei.peiNumeroComplet,
        ...tournee,
      })),
    );
  }, [allPeiData]);

  // Set pour savoir quelles tournées sont actuellement sélectionnées
  const selectedRow = useMemo(() => {
    const set = new Set<string>();
    values.peiData.forEach((pei) =>
      // On ajoute une clé pour chaque tournée sélectionnée qui est selectionnée
      pei.tournees.forEach((tournee) =>
        set.add(`${pei.peiId}_${tournee.idTournee}`),
      ),
    );
    return set; // toutes les lignes selectionnées
  }, [values.peiData]);

  const selectLine = (peiId: string, tourneeId: string) => {
    const newPeiData = values.peiData.map((pei) => {
      if (pei.peiId !== peiId) {
        return pei; // Si ce n'est pas le PEI concerné, on le laisse tel quel
      }
      const tournees = pei.tournees.filter(
        (tournee) => tournee.idTournee !== tourneeId,
      );
      return { ...pei, tournees };
    });

    if (values.peiData.find((pei) => pei.peiId === peiId)) {
      const tournee = allPeiData
        ?.find((pei) => pei.peiId === peiId)
        ?.tournees.find((tournee) => tournee.idTournee === tourneeId);
      // Si la tournée existe et n'était pas sélectionnée avant
      if (tournee && !selectedRow.has(`${peiId}_${tourneeId}`)) {
        setFieldValue(
          "peiData",
          newPeiData.map((pei) =>
            pei.peiId === peiId
              ? { ...pei, tournees: [...pei.tournees, tournee] }
              : pei,
          ),
        );
        return;
      }
    }

    setFieldValue("peiData", newPeiData);
  };

  const onSelectAll = (checked: boolean) => {
    if (!allPeiData) {
      return;
    }
    const newPeiData = checked
      ? allPeiData.map((pei) => ({ ...pei, tournees: [...pei.tournees] }))
      : allPeiData.map((pei) => ({ ...pei, tournees: [] }));
    setFieldValue("peiData", newPeiData);
  };

  const selectedTourneesCount = values.peiData.reduce(
    (acc, pei) => acc + pei.tournees.length,
    0,
  );

  return values ? (
    <FormContainer>
      <Table bordered responsive className="mt-3">
        <thead>
          <tr>
            <th>
              <FormCheck
                type="checkbox"
                label="Sélectionner"
                checked={
                  initPeiData.length > 0 &&
                  selectedTourneesCount === initPeiData.length
                }
                onChange={(e) => onSelectAll(e.target.checked)}
              />
            </th>
            <th>Numéro de PEI</th>
            <th>Libellé de la tournée</th>
            <th>Organisme</th>
          </tr>
        </thead>
        <tbody>
          {initPeiData.map((row) => {
            const key = `${row.peiId}_${row.idTournee}`;
            const isChecked = selectedRow.has(key);
            return (
              <tr key={key}>
                <td>
                  <FormCheck
                    type="checkbox"
                    checked={isChecked}
                    onChange={() => selectLine(row.peiId, row.idTournee)}
                  />
                </td>
                <td>{row.peiNumeroComplet}</td>
                <td>{row.libelleTournee}</td>
                <td>{row.organismeTournee}</td>
              </tr>
            );
          })}
        </tbody>
      </Table>

      <div className="d-flex justify-content-end mt-3">
        <Button
          variant="primary"
          disabled={selectedTourneesCount === 0}
          onClick={() => show()}
        >
          Désaffecter
        </Button>
      </div>

      <SimpleConfirmModal
        onSubmit={() => {
          submitForm();
          close();
        }}
        closeModal={() => close()}
        confirmButtonText={"Confirmer"}
        content={"Confirmer la désaffectation des PEI sélectionnés"}
        header={"Confirmation de la désaffectation"}
        ref={ref}
        visible={visible}
      />
    </FormContainer>
  ) : (
    <div className="mt-3">
      Seuls les PEI dont les tournées sont rattachées à votre organisme ou vos
      organismes enfants peuvent être désaffectés.
    </div>
  );
};

export const prepareVariables = (values: { peiData: PeiAvecTournees[] }) => {
  return values.peiData
    .filter((p) => p.tournees.length > 0)
    .map((p) => ({
      peiId: p.peiId,
      peiNumeroComplet: p.peiNumeroComplet,
      tournees: p.tournees.map((t) => ({
        idTournee: t.idTournee,
        libelleTournee: t.libelleTournee,
        organismeTournee: t.organismeTournee,
      })),
    }));
};

export default DesaffecterPeiTourneeMap;
