import { useFormikContext } from "formik";
import { Form, Table } from "react-bootstrap";
import { object } from "yup";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconInfo, IconUtilisateurs } from "../../../components/Icon/Icon.tsx";
import {
  SECTION_DROIT,
  TypeDroitLabel,
  TypeDroitSection,
} from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";
import TooltipCustom from "../../../components/Tooltip/Tooltip.tsx";

const LienProfilGroupeFonctionnalitesList = () => {
  const lienGroupeFonctionnalitesListState = useGet(
    url`/api/lien-groupe-fonctionnalites`,
  );

  if (!lienGroupeFonctionnalitesListState.isResolved) {
    return <Loading />;
  }

  const { groupeFonctionnalitesList, typeDroitList } =
    lienGroupeFonctionnalitesListState.data;

  return (
    <MyFormik
      initialValues={groupeFonctionnalitesList}
      validationSchema={object()}
      isPost={false}
      submitUrl={`/api/lien-groupe-fonctionnalites/update`}
      onSubmit={() => lienGroupeFonctionnalitesListState.reload()}
      fluid={true}
    >
      <LienProfilInner typeDroitList={typeDroitList} />
    </MyFormik>
  );
};

const LienProfilInner = ({ typeDroitList }: { typeDroitList: any[] }) => {
  const { values, setFieldValue, resetForm } = useFormikContext();
  return (
    <>
      <PageTitle
        title={
          <>
            Attribution des fonctionnalités
            <TooltipCustom
              tooltipText={
                "Cet écran permet d'attribuer ou non les différents droits ci-dessous aux groupes de fonctionnalités définis en amont. Pour chaque groupe, cochez les droits que vous voulez lui attribuer. Certains droits sont liés entre eux, par exemple si vous cochez le droit d'administration, les droit d'accès CRUD associés seront automatiquement cochés."
              }
              tooltipId={"tooltip-groupe-fonctionnalites"}
            >
              <IconInfo />
            </TooltipCustom>
          </>
        }
        icon={<IconUtilisateurs />}
      />
      <FormContainer>
        <Parent>
          <Table striped bordered hover>
            <thead>
              <tr>
                <th
                  className={"sticky-col-1"}
                  style={{
                    "min-width": "300px",
                    "border-right": "1px solid #dee2e6",
                  }}
                >
                  Libellé
                </th>
                {values.map((groupeFonctionnalites, idxPD) => (
                  <th key={idxPD}>
                    {TypeDroitLabel.get(
                      groupeFonctionnalites.groupeFonctionnalitesLibelle,
                    )
                      ? TypeDroitLabel.get(
                          groupeFonctionnalites.groupeFonctionnalitesLibelle,
                        )
                      : groupeFonctionnalites.groupeFonctionnalitesLibelle}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {Object.values(SECTION_DROIT).map((currentSection) => {
                return (
                  <>
                    <tr>
                      <td
                        colSpan={1 + values.length}
                        className={
                          "bg-primary text-light text-center fw-bolder"
                        }
                        style={{
                          "border-right": "1px solid #dee2e6",
                        }}
                      >
                        <div className="section-header-cell">
                          {currentSection}
                        </div>
                      </td>
                    </tr>
                    {TypeDroitSection.entries()
                      .filter(([, value]) => value === currentSection)
                      .map(([key]) => {
                        return (
                          <>
                            {typeDroitList
                              .filter((typeDroit) => key === typeDroit)
                              .map((typeDroit, idxTD) => {
                                return (
                                  <tr key={idxTD}>
                                    <td className={"sticky-col-1"}>
                                      <TooltipCustom
                                        maxWidth={300}
                                        tooltipId={`tooltip-${idxTD}`}
                                        tooltipText={typeDroit}
                                        nowrap={false}
                                      >
                                        {TypeDroitLabel.get(typeDroit)
                                          ? TypeDroitLabel.get(typeDroit)
                                          : ""}{" "}
                                      </TooltipCustom>
                                    </td>
                                    {values.map(
                                      (groupeFonctionnalites, idxPD) => (
                                        <td
                                          key={`${idxTD}${idxPD}`}
                                          className={"text-center"}
                                        >
                                          <Form.Check
                                            type={"checkbox"}
                                            id={`check-${idxTD}-${idxPD}`}
                                          >
                                            <Form.Check.Input
                                              type={"checkbox"}
                                              value={typeDroit}
                                              checked={groupeFonctionnalites.groupeFonctionnalitesDroits?.some(
                                                (e) => e === typeDroit,
                                              )}
                                              onChange={(event) => {
                                                const value =
                                                  event.currentTarget.value;
                                                const arrayVal = [value];
                                                const right = value.substring(
                                                  value.lastIndexOf("_") + 1,
                                                );
                                                const left = value.substring(
                                                  0,
                                                  value.lastIndexOf("_"),
                                                );
                                                if (
                                                  event.currentTarget.checked
                                                ) {
                                                  // si un droit _A est coché, on rajoute les droits équivalents
                                                  if (right === "A") {
                                                    typeDroitList.forEach(
                                                      (typeDroit) => {
                                                        const substr =
                                                          typeDroit.substring(
                                                            0,
                                                            value.lastIndexOf(
                                                              "_",
                                                            ),
                                                          );
                                                        if (
                                                          left === substr &&
                                                          values[
                                                            idxPD
                                                          ].groupeFonctionnalitesDroits.indexOf(
                                                            typeDroit,
                                                          ) === -1
                                                        ) {
                                                          arrayVal.push(
                                                            typeDroit,
                                                          );
                                                        }
                                                      },
                                                    );
                                                  }
                                                  setFieldValue(
                                                    `${idxPD}.groupeFonctionnalitesDroits`,
                                                    [
                                                      ...values[idxPD]
                                                        .groupeFonctionnalitesDroits,
                                                      ...arrayVal,
                                                    ],
                                                  );
                                                } else {
                                                  // si un droit _CRUD est décoché, on tente de retirer le droit _A s'il existe
                                                  if (
                                                    "CRUD"
                                                      .split("")
                                                      .some(
                                                        (action) =>
                                                          action === right,
                                                      )
                                                  ) {
                                                    arrayVal.push(`${left}_A`);
                                                  }
                                                  setFieldValue(
                                                    `${idxPD}.groupeFonctionnalitesDroits`,
                                                    values[
                                                      idxPD
                                                    ].groupeFonctionnalitesDroits?.filter(
                                                      (v) =>
                                                        v &&
                                                        !arrayVal.some(
                                                          (a) => v === a,
                                                        ),
                                                    ),
                                                  );
                                                }
                                              }}
                                            />
                                          </Form.Check>
                                        </td>
                                      ),
                                    )}
                                  </tr>
                                );
                              })}
                          </>
                        );
                      })}
                  </>
                );
              })}
            </tbody>
          </Table>
        </Parent>
        <SubmitFormButtons
          onSecondaryActionClick={() => resetForm()}
          secondaryActionTitle={"Annuler"}
        />
      </FormContainer>
    </>
  );
};

const Parent = ({ children }: { children?: ReactNode }) => {
  return <div className={"container-profil"}>{children}</div>;
};

export default LienProfilGroupeFonctionnalitesList;
