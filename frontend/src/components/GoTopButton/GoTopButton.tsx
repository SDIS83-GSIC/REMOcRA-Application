import React, { useState, useEffect } from "react";
import { Button, Row, Col } from "react-bootstrap";
import "bootstrap/dist/css/bootstrap.min.css";
import { IconArrowUp } from "../Icon/Icon.tsx";

const GoTopButton = () => {
  const [showButton, setShowButton] = useState(false);

  useEffect(() => {
    const handleScroll = () => {
      setShowButton(window.scrollY > 600);
    };

    window.addEventListener("scroll", handleScroll);
    return () => {
      window.removeEventListener("scroll", handleScroll);
    };
  }, []);

  const scrollToTop = () => {
    window.scrollTo({
      top: 0,
      behavior: "smooth",
    });
  };

  return (
    showButton && (
      <Row>
        <Col xs="auto" className="go-top-wrapper bounce-on-hover">
          <Button
            onClick={scrollToTop}
            variant="transparent"
            className={"opacity-75"}
          >
            <IconArrowUp />
          </Button>
        </Col>
      </Row>
    )
  );
};

export default GoTopButton;
