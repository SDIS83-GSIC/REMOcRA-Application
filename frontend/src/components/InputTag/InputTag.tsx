import React, { useState, useEffect, useRef } from "react";
import {
  Form,
  InputGroup,
  FormControl,
  Badge,
  CloseButton,
} from "react-bootstrap";

type TagInputProps = {
  name: string;
  label?: string;
  onTagsChange: (tags: string[]) => void;
  defaultTags?: string[];
  disabled?: boolean;
  required?: boolean;
};

const TagInput = ({
  name,
  label,
  onTagsChange,
  defaultTags = [],
  disabled = false,
  required = false,
}: TagInputProps) => {
  const [tags, setTags] = useState<string[]>(defaultTags);
  const [inputValue, setInputValue] = useState<string>("");

  const isInitialized = useRef(false); // Empêche le useEffect de trigger "onTagsChange" au montage

  useEffect(() => {
    if (!isInitialized.current) {
      isInitialized.current = true;
      return;
    }
    // Ne pas appeler "onTagsChange" ici pour éviter une boucle infinie
  }, [defaultTags]);

  const addTag = (tag: string) => {
    if (disabled) {
      return;
    }

    const trimmedTag = tag.trim();
    if (trimmedTag && !tags.includes(trimmedTag)) {
      const newTags = [...tags, trimmedTag];
      setTags(newTags);
      onTagsChange(newTags);
    }
    setInputValue("");
  };

  const removeTag = (index: number) => {
    if (disabled) {
      return;
    }

    const newTags = tags.filter((_, i) => i !== index);
    setTags(newTags);
    onTagsChange(newTags);
  };

  const handleKeyDown = (event: React.KeyboardEvent<HTMLInputElement>) => {
    if (event.key === "Enter" && inputValue.trim()) {
      event.preventDefault();
      addTag(inputValue);
    }
  };

  return (
    <Form.Group controlId={name}>
      {label && <Form.Label>{label}</Form.Label>}
      <InputGroup className="mb-3">
        <div
          className="d-flex flex-wrap gap-2 p-2 border rounded"
          style={{
            minHeight: "38px",
            width: "350px",
            maxWidth: "100%",
            overflowY: "auto",
            backgroundColor: disabled ? "#e9ecef" : "white",
          }}
        >
          {tags.map((tag, index) => (
            <Badge
              pill
              bg="primary"
              key={index}
              className="d-flex align-items-center"
            >
              {tag}{" "}
              {!disabled && (
                <CloseButton
                  onClick={() => removeTag(index)}
                  className="ms-2"
                  variant="white"
                  style={{ fontSize: "0.7rem" }}
                />
              )}
            </Badge>
          ))}
          {!disabled && (
            <FormControl
              type="text"
              name={name}
              value={inputValue}
              onChange={(e) => setInputValue(e.target.value)}
              onKeyDown={handleKeyDown}
              placeholder="Ajouter un tag..."
              className="border-0 flex-grow-1"
              disabled={disabled}
              required={required && tags.length === 0}
            />
          )}
        </div>
      </InputGroup>
    </Form.Group>
  );
};

export default TagInput;
