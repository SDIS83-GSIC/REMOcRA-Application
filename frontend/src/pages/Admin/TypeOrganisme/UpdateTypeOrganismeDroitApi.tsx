import { useFormikContext } from "formik";
import { Form, Table } from "react-bootstrap";
import Container from "react-bootstrap/Container";
import { object } from "yup";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconInfo, IconUtilisateurs } from "../../../components/Icon/Icon.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";
import {
  TYPE_DROIT_API,
  TypeDroitApiLabel,
} from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import { URLS } from "../../../routes.tsx";
import { TypeOrganismeType } from "../organisme/Organisme.tsx";

const UpdateTypeOrganismeDroitApi = () => {
  const typeOrganismeWithDroitApiState = useGet(
    url`/api/type-organisme/droits-api`,
  );

  if (!typeOrganismeWithDroitApiState.isResolved) {
    return <Loading />;
  }

  const { listTypeOrganisme, listTypeDroitApi } =
    typeOrganismeWithDroitApiState.data;

  return (
    <Container>
      <MyFormik
        initialValues={listTypeOrganisme}
        validationSchema={object()}
        isPost={false}
        submitUrl={`/api/type-organisme/droits-api/update`}
        onSubmit={() => typeOrganismeWithDroitApiState.reload()}
      >
        <TypeOrganismeWithDroitApiInner listTypeDroitApi={listTypeDroitApi} />
      </MyFormik>
    </Container>
  );
};

const TypeOrganismeWithDroitApiInner = ({
  listTypeDroitApi,
}: {
  listTypeDroitApi: TYPE_DROIT_API[];
}) => {
  const { values, setFieldValue } = useFormikContext<TypeOrganismeType[]>();
  return (
    <FormContainer>
      <PageTitle
        title="Attribution des droits API pour les types organismes"
        icon={<IconUtilisateurs />}
      />
      <Table striped bordered hover>
        <thead>
          <tr>
            <th>Type Organisme</th>
            {listTypeDroitApi.map((droitApi, idxPD) => (
              <th key={idxPD}>
                {droitApi}
                <TooltipCustom
                  tooltipText={TypeDroitApiLabel.get(droitApi)}
                  tooltipId={droitApi}
                >
                  <IconInfo />
                </TooltipCustom>
              </th>
            ))}
          </tr>
        </thead>
        <tbody>
          {values.map((typeOrganisme: TypeOrganismeType, idxTD: number) => {
            return (
              <>
                <tr key={idxTD}>
                  <th>
                    {typeOrganisme.typeOrganismeLibelle} (
                    {typeOrganisme.typeOrganismeCode})
                  </th>
                  {listTypeDroitApi.map((droit, idxPD: number) => {
                    return (
                      <td key={`${idxTD}${idxPD}`}>
                        <Form.Check
                          type={"checkbox"}
                          id={`check-${idxTD}-${idxPD}`}
                        >
                          <Form.Check.Input
                            type={"checkbox"}
                            value={droit}
                            checked={
                              typeOrganisme.typeOrganismeDroitApi?.includes(
                                droit,
                              ) ?? false
                            }
                            disabled={
                              typeOrganisme.typeOrganismeDroitApi?.includes(
                                TYPE_DROIT_API.ADMINISTRER,
                              ) &&
                              TYPE_DROIT_API[droit] !==
                                TYPE_DROIT_API.ADMINISTRER
                            }
                            onChange={(event) => {
                              const checked = event.currentTarget.checked;
                              if (checked) {
                                // Si c'est le droit administer, on ajoute les autres
                                if (
                                  TYPE_DROIT_API[droit] ===
                                  TYPE_DROIT_API.ADMINISTRER
                                ) {
                                  setFieldValue(
                                    `${idxTD}.typeOrganismeDroitApi`,
                                    listTypeDroitApi,
                                  );
                                } else {
                                  const tab = [
                                    ...(values[idxTD].typeOrganismeDroitApi ??
                                      []),
                                    droit,
                                  ];
                                  setFieldValue(
                                    `${idxTD}.typeOrganismeDroitApi`,
                                    tab.filter(
                                      (item, pos) => tab.indexOf(item) === pos,
                                    ),
                                  );
                                }
                              } else {
                                setFieldValue(
                                  `${idxTD}.typeOrganismeDroitApi`,
                                  values[idxTD]?.typeOrganismeDroitApi?.filter(
                                    (item) => item !== droit,
                                  ) ?? [],
                                );
                              }
                            }}
                          />
                        </Form.Check>
                      </td>
                    );
                  })}
                </tr>
              </>
            );
          })}
        </tbody>
      </Table>
      <SubmitFormButtons returnLink={URLS.MODULE_ADMIN} />
    </FormContainer>
  );
};

export default UpdateTypeOrganismeDroitApi;
