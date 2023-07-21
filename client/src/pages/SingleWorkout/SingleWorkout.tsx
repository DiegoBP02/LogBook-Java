import { useEffect } from "react";
import { Link, useParams } from "react-router-dom";
import Wrapper from "../../assets/wrappers/SingleWorkout";
import { WorkoutProps, useAppContext } from "../../context/appContext";
import { Navbar, Loading } from "../../components";
import { FindNearestWorkout } from "../../utils";
import {
  RemoveWorkoutButton,
  GetPreviousWorkoutExercises,
  UniqueExercises,
  RenderExercises,
  SingleExerciseForm,
} from "./components";
import {
  AddExerciseProps,
  useExerciseContext,
} from "./context/exerciseContext";

const SingleWorkout = () => {
  const {
    values,
    loading,
    setLoadingFalse,
    addExercise,
    exercises,
    getExercises,
    getNearestWorkoutExercises,
    setCurrentWorkoutId,
  } = useExerciseContext();

  const { workoutId } = useParams() as { workoutId: string };
  const { workouts } = useAppContext();

  const currentWorkout = workouts.find(
    (workout) => workout.id == workoutId
  ) as WorkoutProps;

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const exercise: AddExerciseProps = {
      workoutId,
      name: values.name,
      reps: values.reps,
      rir: values.rir,
      weight: values.weight,
    };

    addExercise(exercise);
  };

  const nearestOldWorkout = FindNearestWorkout({
    workouts,
    currentWorkout,
  }) as WorkoutProps | undefined;

  useEffect(() => {
    async function fetchData() {
      await getExercises(workoutId);
      await getNearestWorkoutExercises(nearestOldWorkout?.id as string);

      setLoadingFalse();
    }
    fetchData();
  }, []);

  useEffect(() => {
    setCurrentWorkoutId(workoutId);
  }, [workoutId]);

  if (loading) {
    return (
      <Wrapper>
        <Navbar />
        <Loading />
      </Wrapper>
    );
  }

  return (
    <Wrapper>
      <Navbar />
      <Link to="/" className="btn" style={{ margin: "0.5rem 0 1.5rem 0" }}>
        Back Home
      </Link>
      {exercises.length === 0 ? (
        <h5>No sets to display...</h5>
      ) : (
        <RenderExercises currentWorkout={currentWorkout} />
      )}
      <SingleExerciseForm handleSubmit={handleSubmit} />
      <GetPreviousWorkoutExercises nearestOldWorkout={nearestOldWorkout} />
      <UniqueExercises currentWorkout={currentWorkout} />
      <RemoveWorkoutButton currentWorkoutMuscle={currentWorkout.muscle} />
    </Wrapper>
  );
};

export default SingleWorkout;
