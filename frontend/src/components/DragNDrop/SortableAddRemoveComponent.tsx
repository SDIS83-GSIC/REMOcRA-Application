import { DndContext } from "@dnd-kit/core";
import { arrayMove, SortableContext } from "@dnd-kit/sortable";
import { useFormikContext } from "formik";
import { ReactNode } from "react";
import AddRemoveComponent from "../AddRemoveComponent/AddRemoveComponent.tsx";

type SortableAddRemoveComponentType = {
  setData: React.Dispatch<React.SetStateAction<any[]>>;
  nomListe: string;
  defaultElement: any;
  createComponentToRepeat: (index: number, listeElements: any[]) => ReactNode;
};

const SortableAddRemoveComponent = ({
  nomListe,
  setData,
  defaultElement,
  createComponentToRepeat,
}: SortableAddRemoveComponentType) => {
  const { values } = useFormikContext();

  function dragEndEvent(e: DndContext) {
    const { over, active } = e;

    setData(
      arrayMove(
        values[nomListe],
        values[nomListe].findIndex((item) => item.id === active.id),
        values[nomListe].findIndex((item) => item.id === over?.id),
      ),
    );
  }

  return (
    values[nomListe] && (
      <DndContext onDragEnd={dragEndEvent}>
        <SortableContext items={values[nomListe]}>
          <AddRemoveComponent
            name={"listeRapportPersonnaliseParametre"}
            createComponentToRepeat={createComponentToRepeat}
            listeElements={values[nomListe]}
            defaultElement={{
              id: Math.random(),
              ...defaultElement,
            }}
          />
        </SortableContext>
      </DndContext>
    )
  );
};

export default SortableAddRemoveComponent;
