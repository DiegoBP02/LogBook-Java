import moment from "moment";
import { WorkoutProps } from "../context/appContext";

interface FindNearestWorkoutProps {
  workouts: WorkoutProps[];
  currentWorkout: WorkoutProps;
}

const FindNearestWorkout: React.FC<FindNearestWorkoutProps> = ({
  workouts,
  currentWorkout,
}: FindNearestWorkoutProps) => {
  const currentWorkoutDate = moment(currentWorkout.date, "YYYY-MM-DD");
  const currentMuscle = currentWorkout.muscle;
  const currentWorkoutLowerRepsRange = currentWorkout.lowerRepsRange;
  const currentWorkoutUpperRepsRange = currentWorkout.upperRepsRange;

  let nearestWorkout: WorkoutProps | undefined = undefined;
  let nearestDateDifference = Infinity;

  workouts.forEach((workout) => {
    const workoutDate = moment(workout.date, "YYYY-MM-DD");

    if (
      workoutDate.isBefore(currentWorkoutDate) &&
      workout.muscle === currentMuscle &&
      workout.lowerRepsRange === currentWorkoutLowerRepsRange &&
      workout.upperRepsRange === currentWorkoutUpperRepsRange
    ) {
      const dateDifference = currentWorkoutDate.diff(workoutDate);
      if (dateDifference < nearestDateDifference) {
        nearestWorkout = workout;
        nearestDateDifference = dateDifference;
      }
    }
  });

  return nearestWorkout;
};

export default FindNearestWorkout;
