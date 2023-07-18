import { Link } from "react-router-dom";
import { useExerciseContext } from "../context/exerciseContext";

interface RemoveWorkoutButtonProps {
  currentWorkoutMuscle: string;
}

const RemoveWorkoutButton = ({
  currentWorkoutMuscle,
}: RemoveWorkoutButtonProps) => {
  const { handleWorkoutRemove, currentWorkoutId } = useExerciseContext();
  return (
    <div className="center">
      <p style={{ maxWidth: "auto" }}>
        <Link
          to={`/singleMuscle/${currentWorkoutMuscle}`}
          style={{ color: "red", maxWidth: "auto" }}
          onClick={() =>
            handleWorkoutRemove(currentWorkoutId, currentWorkoutMuscle)
          }
        >
          Remove workout
        </Link>
      </p>
    </div>
  );
};
export default RemoveWorkoutButton;
