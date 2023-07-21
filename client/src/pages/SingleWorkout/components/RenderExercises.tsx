import { WorkoutProps, useAppContext } from "../../../context/appContext";
import { LoadingText } from "./";
import { AiOutlineEdit, AiOutlineCheck } from "react-icons/ai";
import { CgPlayListRemove } from "react-icons/cg";
import {
  ExerciseDBProps,
  useExerciseContext,
} from "../context/exerciseContext";

interface RenderExercisesProps {
  currentWorkout: WorkoutProps;
}

const RenderExercises = ({ currentWorkout }: RenderExercisesProps) => {
  const { isLoading } = useAppContext();
  const {
    editingExerciseId,
    updateValues,
    nearestExercises,
    exercises,
    handleExerciseUpdate,
    handleExerciseRemove,
    handleUpdateChange,
    handleEditButtonClick,
    currentWorkoutId,
  } = useExerciseContext();

  const { lowerRepsRange, upperRepsRange } = currentWorkout;

  const processedExerciseIds: string[] = [];

  return (
    <>
      <article
        className="properties marginBottom"
        style={{ paddingBottom: "1rem" }}
      >
        <p>Set</p>
        <p>Exercise</p>
        {nearestExercises && <p>Previous</p>}
        <p>
          Reps ({lowerRepsRange} - {upperRepsRange})
        </p>
        <p>Weight</p>
      </article>
      {exercises &&
        exercises.length > 0 &&
        exercises
          .sort((a, b) => {
            const nameComparison = a.name.localeCompare(b.name);
            if (nameComparison !== 0) {
              return nameComparison;
            } else {
              return a.createdAt - b.createdAt;
            }
          })
          .map((exercise, index) => {
            let previousExercise: ExerciseDBProps | undefined;
            if (!processedExerciseIds.includes(exercise.id)) {
              previousExercise = nearestExercises?.find(
                (prevExercise: ExerciseDBProps) => {
                  return (
                    prevExercise.name === exercise.name &&
                    !processedExerciseIds.includes(prevExercise.id)
                  );
                }
              );
              previousExercise &&
                processedExerciseIds.push(previousExercise.id);
            }
            return (
              <div key={index} className="properties">
                <p>{index + 1}</p>
                <p className="nameInput">
                  {exercise.id === editingExerciseId ? (
                    <input
                      type="text"
                      value={updateValues.name}
                      name="name"
                      autoComplete="on"
                      onChange={handleUpdateChange}
                      className="form-input"
                      style={{
                        width: "6.5rem",
                      }}
                    />
                  ) : (
                    exercise.name
                  )}
                </p>
                {nearestExercises && previousExercise ? (
                  <p>
                    ({previousExercise?.reps} x {previousExercise?.weight})
                  </p>
                ) : (
                  <p></p>
                )}
                <p
                  style={{
                    color:
                      exercise.reps < lowerRepsRange ||
                      exercise.reps > upperRepsRange
                        ? "red"
                        : undefined,
                  }}
                >
                  {exercise.id === editingExerciseId ? (
                    <input
                      type="number"
                      value={updateValues.reps}
                      name="reps"
                      autoComplete="on"
                      onChange={handleUpdateChange}
                      className="form-input"
                    />
                  ) : (
                    exercise.reps
                  )}
                </p>
                <p>
                  {exercise.id === editingExerciseId ? (
                    <input
                      type="number"
                      value={updateValues.weight}
                      name="weight"
                      autoComplete="on"
                      onChange={handleUpdateChange}
                      className="form-input"
                    />
                  ) : (
                    exercise.weight
                  )}
                </p>

                {editingExerciseId && exercise.id === editingExerciseId ? (
                  <p
                    onClick={() =>
                      handleExerciseUpdate(exercise.id, currentWorkoutId)
                    }
                    className="editInput"
                  >
                    <AiOutlineCheck />
                  </p>
                ) : (
                  <p
                    onClick={() => handleEditButtonClick(exercise)}
                    className="editInput"
                  >
                    <AiOutlineEdit />
                  </p>
                )}
                <p
                  style={{ color: "red", cursor: "pointer" }}
                  onClick={() =>
                    handleExerciseRemove(exercise.id, currentWorkoutId)
                  }
                >
                  <CgPlayListRemove />
                </p>
              </div>
            );
          })}
      {isLoading && <LoadingText />}
    </>
  );
};
export default RenderExercises;
