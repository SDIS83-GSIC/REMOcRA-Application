import { Alert, Container } from "react-bootstrap";

const LoginError = () => {
  return (
    <Container className="mt-5">
      <Alert variant="danger" role="alert">
        <Alert.Heading className="h2 mb-4">Accès refusé</Alert.Heading>
        <p className="fs-6 fw-semibold mb-3">
          Vous n'avez pas les droits nécessaires pour accéder à l'application.
        </p>
        <hr />
        <p className="fs-6 mb-0">
          Contactez votre administrateur pour qu'il configure votre profil.
        </p>
      </Alert>
    </Container>
  );
};

export default LoginError;
