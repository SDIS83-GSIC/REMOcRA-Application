import { Table } from "react-bootstrap";
import { ResponsiveContainer } from "recharts";
import { setTableValueMapped } from "../../MappedValueComponent.tsx";

const TableComponent = (data: { data: string | any[]; config: any }) => {
  if (!data.data || data.data.length === 0) {
    return;
  }

  const dataMapped = setTableValueMapped(
    data.data,
    data.config.map((item: { col: { value: any } }) => item.col.value),
  );

  // Extraire les noms des colonnes (clés du premier objet)
  const columns = data.config.map(
    (item: { col: { name: any } }) => item.col.name,
  );

  return (
    <ResponsiveContainer width="100%" height="100%" className="overflow-auto">
      <Table striped bordered hover responsive>
        <thead>
          <tr>
            {columns.map((column: string, index: number) => (
              <th key={index}>{column}</th> // En-têtes de colonnes
            ))}
          </tr>
        </thead>
        <tbody>
          {dataMapped.map((row: any, rowIndex: any) => (
            <tr key={rowIndex}>
              {Object.values(row).map((value: any, colIndex: number) => (
                <td key={colIndex}>{value}</td>
              ))}
            </tr>
          ))}
        </tbody>
      </Table>
    </ResponsiveContainer>
  );
};

export default TableComponent;
