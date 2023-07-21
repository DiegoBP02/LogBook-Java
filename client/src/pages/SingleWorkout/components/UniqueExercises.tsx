import { WorkoutProps, useAppContext } from "../../../context/appContext";
import moment from "moment";
import { FindNearestWorkout } from "../../../utils";
import { useState } from "react";
import { ExerciseDBProps } from "../context/exerciseContext";

interface UniqueExercisesProps {
  currentWorkout: WorkoutProps;
}

const UniqueExercises: React.FC<UniqueExercisesProps> = ({
  currentWorkout,
}: UniqueExercisesProps) => {
  const [uniqueOldWorkouts, setUniqueOldWorkouts] = useState<string[]>();
  const [uniqueCurrentWorkouts, setUniqueCurrentWorkouts] =
    useState<string[]>();
  const [showUniqueExercises, setShowUniqueExercises] =
    useState<boolean>(false);
  const { workouts } = useAppContext();

  const { authToken } = useAppContext();
  const nearestOldWorkout = FindNearestWorkout({ workouts, currentWorkout }) as
    | WorkoutProps
    | undefined;

  const getUniqueOldWorkoutExercises = async () => {
    if (nearestOldWorkout?.id === undefined) {
      console.log(
        "No previous workout with the same reps range and muscle was found."
      );
      return;
    }
    const { data } = await authToken.get(
      `/workouts/uniqueOldExercises/${nearestOldWorkout?.id}/${currentWorkout.id}`
    );

    setUniqueOldWorkouts(
      data.map((exercise: ExerciseDBProps) => exercise.name)
    );
  };

  const getUniqueCurrentWorkoutExercises = async () => {
    if (nearestOldWorkout?.id === undefined) {
      console.log(
        "No previous workout with the same reps range and muscle was found."
      );
      return;
    }
    const { data } = await authToken.get(
      `/workouts/uniqueCurrentExercises/${currentWorkout?.id}/${nearestOldWorkout.id}`
    );

    setUniqueCurrentWorkouts(
      data.map((exercise: ExerciseDBProps) => exercise.name)
    );
  };

  const momentObject = moment(nearestOldWorkout?.date);
  const formattedDate = momentObject.format("DD/MM/YYYY");

  if (nearestOldWorkout === undefined) {
    return null;
  }

  return (
    <div>
      <div style={{ textAlign: "center" }}>
        <button
          onClick={() => {
            getUniqueOldWorkoutExercises();
            getUniqueCurrentWorkoutExercises();
            setShowUniqueExercises(!showUniqueExercises);
          }}
          className="btn"
        >
          Get Unique Workout Exercises (Compare to {formattedDate} workout)
        </button>
      </div>
      {showUniqueExercises ? (
        ((uniqueOldWorkouts && uniqueOldWorkouts.length === 0) ||
          uniqueOldWorkouts === undefined) &&
        ((uniqueCurrentWorkouts && uniqueCurrentWorkouts.length === 0) ||
          uniqueCurrentWorkouts === undefined) ? null : (
          <>
            <article
              className="propertiesUniqueExercises marginBottom"
              style={{ padding: "1rem 0" }}
            >
              <p>Unique Old Workout Exercises</p>
              <p>Unique Current Workout Exercises</p>
            </article>
            {((uniqueOldWorkouts && uniqueOldWorkouts.length > 0) ||
              (uniqueCurrentWorkouts && uniqueCurrentWorkouts.length > 0)) && (
              <div className="propertiesUniqueExercises">
                <div>
                  {uniqueOldWorkouts &&
                    uniqueOldWorkouts.map((exercise, index) => (
                      <p key={index}>{exercise}</p>
                    ))}
                </div>
                <div>
                  {uniqueCurrentWorkouts &&
                    uniqueCurrentWorkouts.map((exercise, index) => (
                      <p key={index}>{exercise}</p>
                    ))}
                </div>
              </div>
            )}
          </>
        )
      ) : null}
    </div>
  );
};
export default UniqueExercises;
