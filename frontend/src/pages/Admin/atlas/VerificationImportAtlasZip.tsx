import { Col, Row, Table } from "react-bootstrap";

type ImportZipError = {
  message: string;
};

const VerificationImportAtlasZip = (data: {
  data?: { importZipData?: { errors?: ImportZipError[] } };
}) => {
  const errors: ImportZipError[] = data?.data?.importZipData?.errors || [];
  return (
    <Row>
      <Col>
        {errors.length > 0 ? (
          <>
            <Table bordered>
              <thead>
                <tr>
                  <th>Erreurs</th>
                </tr>
              </thead>
              <tbody>
                {errors.map((error, index) => (
                  <tr key={`${error.message}-${index}`}>
                    <td>{error.message}</td>
                  </tr>
                ))}
              </tbody>
            </Table>
            <div className="alert alert-danger text-center p-5 mt-4">
              <h4>Les fichiers n'ont pas été enregistrés.</h4>
              <p>Veuillez corriger les erreurs et réessayer.</p>
            </div>
          </>
        ) : (
          <div className="alert alert-success text-center p-5 mt-4">
            <h4>Les fichiers ont bien été enregistrés.</h4>
          </div>
        )}
      </Col>
    </Row>
  );
};

export default VerificationImportAtlasZip;
