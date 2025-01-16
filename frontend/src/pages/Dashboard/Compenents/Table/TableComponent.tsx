import { Table } from "react-bootstrap";
import { setTableValueMapped } from "../../MappedValueComponent.tsx";

const TableComponent = (data: { data: string | any[]; config: any }) => {
  if (!data.data || data.data.length === 0) {
    return;
  }
  const dataMapped = setTableValueMapped(data.data, data.config);

  // Extraire les noms des colonnes (clés du premier objet)
  const columns = Object.keys(dataMapped[0]);

  return (
    <Table striped bordered hover responsive>
      <thead>
        <tr>
          {columns.map((column, index) => (
            <th key={index}>{column}</th> // En-têtes de colonnes
          ))}
        </tr>
      </thead>
      <tbody>
        {dataMapped.map((row: any, rowIndex: any) => (
          <tr key={rowIndex}>
            {columns.map((column, colIndex) => (
              <td key={colIndex}>{row[column]}</td> // Cellules de données (lecture seule)
            ))}
          </tr>
        ))}
      </tbody>
    </Table>
  );
};

export default TableComponent;
