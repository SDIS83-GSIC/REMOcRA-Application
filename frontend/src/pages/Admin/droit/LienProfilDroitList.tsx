import { useFormikContext } from "formik";
import { Form, Table } from "react-bootstrap";
import { object } from "yup";
import Loading from "../../../components/Elements/Loading/Loading.tsx";
import PageTitle from "../../../components/Elements/PageTitle/PageTitle.tsx";
import { useGet } from "../../../components/Fetch/useFetch.tsx";
import { FormContainer } from "../../../components/Form/Form.tsx";
import MyFormik from "../../../components/Form/MyFormik.tsx";
import SubmitFormButtons from "../../../components/Form/SubmitFormButtons.tsx";
import { IconUtilisateurs } from "../../../components/Icon/Icon.tsx";
import {
  SECTION_DROIT,
  TypeDroitLabel,
  TypeDroitSection,
} from "../../../enums/DroitEnum.tsx";
import url from "../../../module/fetch.tsx";

const LienProfilDroitList = () => {
  const lienProfilDroitListState = useGet(url`/api/lien-profil-droit`);

  if (!lienProfilDroitListState.isResolved) {
    return <Loading />;
  }

  const { profilDroitList, typeDroitList } = lienProfilDroitListState.data;

  return (
    <MyFormik
      initialValues={profilDroitList}
      validationSchema={object()}
      isPost={false}
      submitUrl={`/api/lien-profil-droit/update`}
      onSubmit={() => lienProfilDroitListState.reload()}
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
        title="Attribution des fonctionnalités"
        icon={<IconUtilisateurs />}
      />
      <FormContainer>
        <Parent>
          <Table striped bordered hover>
            <thead>
              <tr>
                <th>Clé</th>
                <th className={"sticky-col-1"}>Libellé</th>
                {values.map((profilDroit, idxPD) => (
                  <th key={idxPD}>
                    {TypeDroitLabel.get(profilDroit.profilDroitLibelle)
                      ? TypeDroitLabel.get(profilDroit.profilDroitLibelle)
                      : profilDroit.profilDroitLibelle}
                  </th>
                ))}
              </tr>
            </thead>
            <tbody>
              {Object.values(SECTION_DROIT).map((currentSection) => {
                return (
                  <>
                    <tr>
                      {/*2 + values.length car on colspan "clé" + " libellé" + le nombre de values*/}
                      <td
                        colSpan={2 + values.length}
                        className={
                          "bg-primary text-light text-center fw-bolder"
                        }
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
                                    <th>{typeDroit}</th>
                                    <td className={"sticky-col-1"}>
                                      {TypeDroitLabel.get(typeDroit)
                                        ? TypeDroitLabel.get(typeDroit)
                                        : ""}{" "}
                                    </td>
                                    {values.map((profilDroit, idxPD) => (
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
                                            checked={profilDroit.profilDroitDroits?.some(
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
                                              if (event.currentTarget.checked) {
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
                                                        ].profilDroitDroits.indexOf(
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
                                                  `${idxPD}.profilDroitDroits`,
                                                  [
                                                    ...values[idxPD]
                                                      .profilDroitDroits,
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
                                                  `${idxPD}.profilDroitDroits`,
                                                  values[
                                                    idxPD
                                                  ].profilDroitDroits?.filter(
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
                                    ))}
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

export default LienProfilDroitList;
