import { WorkoutProps } from "../../../context/appContext";
import moment from "moment";
import {
  AddExerciseProps,
  ExerciseDBProps,
  useExerciseContext,
} from "../context/exerciseContext";

interface GetPreviousWorkoutExercisesProps {
  nearestOldWorkout: WorkoutProps | undefined;
}

const GetPreviousWorkoutExercises = ({
  nearestOldWorkout,
}: GetPreviousWorkoutExercisesProps) => {
  const { addExercise, currentWorkoutId, nearestExercises } =
    useExerciseContext();

  const momentObject = moment(nearestOldWorkout?.date);
  const formattedDate = momentObject.format("DD/MM/YYYY");

  const getPreviousWorkoutExercises = async () => {
    nearestExercises?.forEach((e) => {
      const exercise: AddExerciseProps = {
        workoutId: currentWorkoutId,
        name: e.name,
        reps: e.reps,
        rir: e.rir,
        weight: e.weight,
      };
      addExercise(exercise);
    });
  };

  return (
    <div className="center">
      <button
        className="btn"
        onClick={getPreviousWorkoutExercises}
        style={{ textAlign: "center", marginBottom: "1rem" }}
      >
        Get Exercises From Previous Workout (From {formattedDate} workout)
      </button>
    </div>
  );
};

export default GetPreviousWorkoutExercises;
