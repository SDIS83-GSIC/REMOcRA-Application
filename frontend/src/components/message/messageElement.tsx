import { Card } from "react-bootstrap";
import formatDateTime from "../../utils/formatDateUtils.tsx";

/**
 * Permet d'afficher un message
 */

function capitalizeFirstLetter(str: string): string {
  if (!str || str.length === 0) {
    return str;
  }
  return str.charAt(0).toUpperCase() + str.slice(1).toLowerCase();
}

const MessageElement = ({ message }: { message: any }) => {
  return (
    <Card bg={"light"} style={{ width: "18rem" }}>
      <Card.Body>
        <Card.Title>{message.messageObjet}</Card.Title>
        <Card.Text>
          <b>Auteur.</b> {capitalizeFirstLetter(message.messageUtilisateur)}
          <br />
          <b>Date.</b> {formatDateTime(message.messageDateConstat)}
          <br />
          <b>Desc.</b> {message.messageDescription}
        </Card.Text>
      </Card.Body>
    </Card>
  );
};

export default MessageElement;
