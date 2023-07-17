import { Link } from "react-router-dom";

interface RemoveWorkoutButtonProps {
  currentWorkoutMuscle: string;
  handleWorkoutRemove: () => Promise<void>;
}

const RemoveWorkoutButton = ({
  currentWorkoutMuscle,
  handleWorkoutRemove,
}: RemoveWorkoutButtonProps) => {
  return (
    <div style={{ textAlign: "center" }}>
      <p style={{ maxWidth: "auto" }}>
        <Link
          to={`/singleMuscle/${currentWorkoutMuscle}`}
          style={{ color: "red", maxWidth: "auto" }}
          onClick={handleWorkoutRemove}
        >
          Remove workout
        </Link>
      </p>
    </div>
  );
};
export default RemoveWorkoutButton;
