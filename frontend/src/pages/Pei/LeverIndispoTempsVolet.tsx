import { useFormikContext } from "formik";
import { useEffect, useState } from "react";
import { Alert, Container, FormCheck, Row, Table } from "react-bootstrap";
import { object } from "yup";
import PageTitle from "../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../components/Fetch/useFetch.tsx";
import { FormContainer } from "../../components/Form/Form.tsx";
import MyFormik from "../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../components/Form/SubmitFormButtons.tsx";
import { IconLeverIndisponibiliteTemporaire } from "../../components/Icon/Icon.tsx";
import { PeiEntity } from "../../Entities/PeiEntity.tsx";
import url from "../../module/fetch.tsx";
import { getStatutIndispo } from "../../utils/columnUtils.tsx";
import formatDateTime from "../../utils/formatDateUtils.tsx";

const LeverIndispoTempsVolet = ({
  idPei,
  closeVolet,
  onClick,
}: {
  idPei: string;
  closeVolet: () => void;
  onClick?: () => void;
}) => {
  const [peiInfo, setPeiInfo] = useState<PeiEntity | null>(null);
  const { data: peiData } = useGet(idPei ? url`/api/pei/${idPei}` : "");

  useEffect(() => {
    if (peiData) {
      setPeiInfo(peiData);
    }
  }, [peiData]);

  return (
    <Container>
      <PageTitle
        displayReturnButton={false}
        icon={<IconLeverIndisponibiliteTemporaire />}
        title={
          "Lever une indisponibilité temporaire pour le PEI " +
          (peiInfo ? peiInfo.peiNumeroComplet : "")
        }
      />

      <MyFormik
        isPost={false}
        isMultipartFormData={false}
        submitUrl={`/api/indisponibilite-temporaire/lever-indispo-temp`}
        onSubmit={closeVolet}
        prepareVariables={(values) => prepareVariables(values)}
        validationSchema={object()}
        initialValues={{ data: [] }}
      >
        <LeverIndispoTempForm idPei={idPei} onClick={onClick} />
      </MyFormik>
    </Container>
  );
};

type PeiIndispoTemp = {
  indisponibiliteTemporaireId: string;
  indisponibiliteTemporaireDateDebut: Date;
  indisponibiliteTemporaireDateFin: Date | null;
  indisponibiliteTemporaireMotif: string;
  indisponibiliteTemporaireStatus: string | null;
  listePei: string;
};

const LeverIndispoTempForm = ({
  idPei,
  onClick,
}: {
  idPei: string;
  onClick?: () => void;
}) => {
  const { setFieldValue, values } = useFormikContext<{
    data: PeiIndispoTemp[];
  }>();

  const [tableData, setTableData] = useState<PeiIndispoTemp[] | null>(null);

  const { data: indispos } = useGet(
    idPei ? url`/api/pei/get-pei-indispo-temp/${idPei}` : "",
  );

  useEffect(() => {
    if (indispos && indispos.length > 0) {
      const enrichedData: PeiIndispoTemp[] = indispos
        .map((item: any) => ({
          ...item,
          indisponibiliteTemporaireStatus: getStatutIndispo(
            item.indisponibiliteTemporaireDateDebut,
            item.indisponibiliteTemporaireDateFin,
          ),
        }))
        .filter(
          (item: { indisponibiliteTemporaireStatus: string }) =>
            item.indisponibiliteTemporaireStatus !== "Terminée",
        );

      setTableData(enrichedData);
    }
  }, [indispos]);

  const updateFieldValue = (indispoId: string, isSelected: boolean) => {
    if (!tableData) {
      return;
    }
    setFieldValue(
      "data",
      isSelected
        ? [
            ...(values?.data || []),
            tableData?.find(
              (indispo) => indispo.indisponibiliteTemporaireId === indispoId,
            ),
          ]
        : values?.data?.filter(
            (indispo) => indispo.indisponibiliteTemporaireId !== indispoId,
          ),
    );
  };

  const onSelectAll = (e: React.ChangeEvent<HTMLInputElement>) => {
    if (!tableData) {
      return;
    }
    setFieldValue("data", e.target.checked ? tableData : []);
  };

  return tableData ? (
    <>
      <FormContainer>
        <Row>
          <Alert variant="warning">
            <Alert.Heading>Attention</Alert.Heading>
            Les indisponibilités temporaires sélectionnées seront levées pour{" "}
            <b>tous les PEI</b> qui leurs sont associées.
          </Alert>
        </Row>

        <Table bordered responsive className="mt-3">
          <thead>
            <tr>
              <th>
                <FormCheck
                  type="checkbox"
                  label="Sélectionner"
                  onChange={onSelectAll}
                  checked={
                    values?.data?.length === tableData?.length &&
                    tableData.every((indispoTemp) =>
                      values?.data?.some(
                        (val) =>
                          val.indisponibiliteTemporaireId ===
                          indispoTemp.indisponibiliteTemporaireId,
                      ),
                    )
                  }
                />
              </th>
              <th>Date de début</th>
              <th>PEI concernés</th>
              <th>Statut</th>
              <th>Motif</th>
            </tr>
          </thead>

          <tbody>
            {tableData.map((row: PeiIndispoTemp) => (
              <tr key={row.indisponibiliteTemporaireId}>
                <td>
                  <FormCheck
                    type="checkbox"
                    checked={values?.data?.some(
                      (val) =>
                        val.indisponibiliteTemporaireId ===
                        row.indisponibiliteTemporaireId,
                    )}
                    onChange={() =>
                      updateFieldValue(
                        row.indisponibiliteTemporaireId,
                        !values?.data?.some(
                          (val) =>
                            val.indisponibiliteTemporaireId ===
                            row.indisponibiliteTemporaireId,
                        ),
                      )
                    }
                  />
                </td>
                <td>
                  {formatDateTime(row.indisponibiliteTemporaireDateDebut)}
                </td>
                <td>{row.listePei}</td>
                <td>{row.indisponibiliteTemporaireStatus}</td>
                <td>{row.indisponibiliteTemporaireMotif}</td>
              </tr>
            ))}
          </tbody>
        </Table>

        <SubmitFormButtons
          submitTitle={"Lever"}
          onClick={onClick}
          disabledValide={
            !Array.isArray(values?.data) || values.data.length === 0
          }
        />
      </FormContainer>
    </>
  ) : (
    <p>Aucune donnée trouvée pour ce PEI.</p>
  );
};

export const prepareVariables = (val: any) => {
  return val.data?.map(
    (indispo: PeiIndispoTemp) => indispo.indisponibiliteTemporaireId,
  );
};

export default LeverIndispoTempsVolet;
