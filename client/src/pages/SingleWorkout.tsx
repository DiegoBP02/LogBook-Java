import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import Wrapper from "../assets/wrappers/SingleWorkout";
import {
  AddExerciseProps,
  ExerciseProps,
  useAppContext,
} from "../context/appContext";
import { Navbar, Loading, SingleExerciseForm } from "../components";

export interface InitialStateProps {
  name: string;
  reps: number;
  weight: number;
  rir: number;
}

export const initialState: InitialStateProps = {
  name: "",
  reps: 0,
  weight: 0,
  rir: 0,
};

const SingleWorkout = () => {
  const [values, setValues] = useState(initialState);
  const [loading, setLoading] = useState(true);
  const { workoutId } = useParams() as { workoutId: string };
  const {
    getExercises,
    exercises,
    isLoading,
    authToken,
    getWorkoutsByMuscle,
    addExercise,
    workouts,
  } = useAppContext();

  const currentMuscleWorkout = workouts.find(
    (workout) => workout.id == workoutId
  )?.muscle as string;

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleSubmit = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();

    const exercise: AddExerciseProps = {
      workoutId: workoutId,
      name: values.name,
      reps: values.reps,
      rir: values.rir,
      weight: values.weight,
    };

    addExercise(exercise);
  };

  const handleExerciseRemove = async (id: string) => {
    try {
      await authToken.delete(`/exercises/${id}`);
      getExercises(workoutId);
    } catch (error) {
      console.log(error);
    }
  };

  const handleWorkoutRemove = async () => {
    try {
      await authToken.delete(`/workouts/${workoutId}`);
      getWorkoutsByMuscle(currentMuscleWorkout);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    async function fetchData() {
      await getExercises(workoutId);
      setLoading(false);
    }

    fetchData();
  }, []);

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
        <>
          <article
            className="properties marginBottom"
            style={{ paddingBottom: "1rem" }}
          >
            <p>Set</p>
            <p>Exercise</p>
            <p>Reps</p>
            <p>Weight</p>
            <p>RIR</p>
          </article>
          {exercises &&
            exercises.length > 0 &&
            exercises.map((exercise, index) => {
              return (
                <div key={index} className="properties">
                  <p>{index + 1}</p>
                  <p>{exercise.name}</p>
                  <p>{exercise.reps}</p>
                  <p>{exercise.weight}</p>
                  <p>{exercise.rir}</p>
                  <p
                    style={{ color: "red", cursor: "pointer" }}
                    onClick={() => handleExerciseRemove(exercise.id)}
                  >
                    X
                  </p>
                </div>
              );
            })}
          {isLoading && (
            <h5 style={{ textAlign: "center", marginTop: "0.5rem" }}>
              Loading...
            </h5>
          )}
        </>
      )}
      <SingleExerciseForm
        handleChange={handleChange}
        handleSubmit={handleSubmit}
        values={values}
        muscle={currentMuscleWorkout}
        handleWorkoutRemove={handleWorkoutRemove}
      />
    </Wrapper>
  );
};

export default SingleWorkout;
