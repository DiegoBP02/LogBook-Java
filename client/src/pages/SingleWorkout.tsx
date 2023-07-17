import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import Wrapper from "../assets/wrappers/SingleWorkout";
import {
  AddExerciseProps,
  ExerciseProps,
  WorkoutProps,
  useAppContext,
} from "../context/appContext";
import {
  Navbar,
  Loading,
  SingleExerciseForm,
  UniqueExercises,
  GetPreviousWorkoutExercises,
  LoadingText,
} from "../components";
import { AiOutlineEdit, AiOutlineCheck } from "react-icons/ai";
import { CgPlayListRemove } from "react-icons/cg";
import { FindNearestWorkout } from "../utils";
import moment from "moment";
import RemoveWorkoutButton from "../components/RemoveWorkoutButton";

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

export const updateExerciseProps: InitialStateProps = {
  name: "",
  reps: 0,
  weight: 0,
  rir: 0,
};

const SingleWorkout = () => {
  const [editingExerciseId, setEditingExerciseId] = useState<string | null>(
    null
  );
  const [values, setValues] = useState(initialState);
  const [updateValues, setUpdateValues] = useState(updateExerciseProps);
  const [loading, setLoading] = useState(true);
  const [nearestExercises, setNearestExercises] = useState<ExerciseProps[]>();

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

  const currentWorkout = workouts.find(
    (workout) => workout.id == workoutId
  ) as WorkoutProps;

  const currentWorkoutMuscle = currentWorkout.muscle;
  const lowerRepsRange = currentWorkout.lowerRepsRange;
  const upperRepsRanges = currentWorkout.upperRepsRange;

  const processedExerciseIds: string[] = [];

  const [alreadySelectedPreviousWorkout, setAlreadySelectedPreviousWorkout] =
    useState<boolean>(false); // temporary

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setValues({ ...values, [e.target.name]: e.target.value });
  };

  const handleUpdateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    setUpdateValues({ ...updateValues, [e.target.name]: e.target.value });
  };

  const handleEditButtonClick = (exercise: ExerciseProps) => {
    setEditingExerciseId(exercise.id);
    setUpdateValues({
      name: exercise.name,
      reps: exercise.reps,
      weight: exercise.weight,
      rir: exercise.rir,
    });
  };

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
      getWorkoutsByMuscle(currentWorkoutMuscle);
    } catch (error) {
      console.log(error);
    }
  };

  const handleExerciseUpdate = async (exerciseId: string) => {
    try {
      await authToken.patch(`/exercises/${exerciseId}`, {
        ...updateValues,
        workoutId,
      });
      setEditingExerciseId(null);
      setUpdateValues(updateExerciseProps);
      getExercises(workoutId);
    } catch (error) {
      console.log(error);
    }
  };

  const getNearestWorkoutExercises = async (workoutId: string) => {
    try {
      const { data } = await authToken.get(`/workouts/${workoutId}`);
      setNearestExercises(data.exercises);
    } catch (error: any) {
      console.log(error);
    }
  };

  const nearestOldWorkout = FindNearestWorkout({
    workouts,
    currentWorkout,
  }) as WorkoutProps | undefined;

  useEffect(() => {
    async function fetchData() {
      await getExercises(workoutId);
      await getNearestWorkoutExercises(nearestOldWorkout?.id as string);

      setLoading(false);
    }
    fetchData();
  }, []);
  console.log(nearestExercises);

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
            {nearestExercises && <p>Previous</p>}
            <p>
              Reps ({lowerRepsRange} - {upperRepsRanges})
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
                let previousExercise: ExerciseProps | undefined;
                if (!processedExerciseIds.includes(exercise.id)) {
                  previousExercise = nearestExercises?.find(
                    (prevExercise: ExerciseProps) => {
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
                        {previousExercise?.name} ({previousExercise?.reps} x{" "}
                        {previousExercise?.weight})
                      </p>
                    ) : (
                      <p></p>
                    )}
                    <p
                      style={{
                        color:
                          exercise.reps < lowerRepsRange ||
                          exercise.reps > upperRepsRanges
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
                        onClick={() => handleExerciseUpdate(exercise.id)}
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
                      onClick={() => handleExerciseRemove(exercise.id)}
                    >
                      <CgPlayListRemove />
                    </p>
                  </div>
                );
              })}
          {isLoading && <LoadingText />}
        </>
      )}
      <SingleExerciseForm
        handleChange={handleChange}
        handleSubmit={handleSubmit}
        values={values}
      />
      {!alreadySelectedPreviousWorkout && (
        <GetPreviousWorkoutExercises
          workoutId={workoutId}
          nearestExercises={nearestExercises}
          nearestOldWorkout={nearestOldWorkout}
          setAlreadySelectedPreviousWorkout={setAlreadySelectedPreviousWorkout}
        />
      )}
      <UniqueExercises workouts={workouts} currentWorkout={currentWorkout} />
      <RemoveWorkoutButton
        currentWorkoutMuscle={currentWorkoutMuscle}
        handleWorkoutRemove={handleWorkoutRemove}
      />
    </Wrapper>
  );
};

export default SingleWorkout;
