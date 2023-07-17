import { useState } from "react";
import {
  AddExerciseProps,
  ExerciseProps,
  WorkoutProps,
  useAppContext,
} from "../context/appContext";
import moment from "moment";

interface GetPreviousWorkoutExercisesProps {
  workoutId: string;
  nearestExercises: ExerciseProps[] | undefined;
  nearestOldWorkout: WorkoutProps | undefined;
  setAlreadySelectedPreviousWorkout: React.Dispatch<
    React.SetStateAction<boolean>
  >;
}

const GetPreviousWorkoutExercises = ({
  workoutId,
  nearestExercises,
  nearestOldWorkout,
  setAlreadySelectedPreviousWorkout,
}: GetPreviousWorkoutExercisesProps) => {
  const { addExercise } = useAppContext();

  const momentObject = moment(nearestOldWorkout?.date);
  const formattedDate = momentObject.format("DD/MM/YYYY");

  const getPreviousWorkoutExercises = async () => {
    nearestExercises?.forEach((e) => {
      const exercise: AddExerciseProps = {
        workoutId,
        name: e.name,
        reps: e.reps,
        rir: e.rir,
        weight: e.weight,
      };
      addExercise(exercise);
    });
    setAlreadySelectedPreviousWorkout(true);
  };

  return (
    <div>
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
