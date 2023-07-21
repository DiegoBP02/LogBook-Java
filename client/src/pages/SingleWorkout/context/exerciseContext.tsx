import React, { useContext, useReducer } from "react";
import reducer, { Action } from "./exerciseReducer";
import { useAppContext } from "../../../context/appContext";

export interface ExerciseValuesProps {
  name: string;
  reps: number;
  weight: number;
  rir: number;
}

export interface ExerciseDBProps extends ExerciseValuesProps {
  createdAt: number;
  id: string;
}

export interface AddWorkoutProps {
  date: string;
  muscle: string;
  lowerRepsRange: number;
  upperRepsRange: number;
}

export interface AddExerciseProps extends ExerciseValuesProps {
  workoutId: string;
}

export type InitialStateProps = {
  getExercises: (workoutId: string) => Promise<void>;
  exercises: ExerciseDBProps[];
  addExercise: ({
    workoutId,
    name,
    reps,
    weight,
    rir,
  }: AddExerciseProps) => Promise<void>;
  isLoading: boolean;
  editingExerciseId: string | null;
  values: ExerciseValuesProps;
  updateValues: ExerciseValuesProps;
  loading: boolean;
  nearestExercises?: ExerciseDBProps[];
  handleExerciseRemove: (id: string, workoutId: string) => Promise<void>;
  handleWorkoutRemove: (
    workoutId: string,
    currentWorkoutMuscle: string
  ) => Promise<void>;
  handleExerciseUpdate: (
    exerciseId: string,
    workoutId: string
  ) => Promise<void>;
  getNearestWorkoutExercises: (workoutId: string) => Promise<void>;
  setLoadingFalse: () => void;
  handleChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleUpdateChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  handleEditButtonClick: (exercise: ExerciseDBProps) => void;
  currentWorkoutId: string;
  setCurrentWorkoutId: (workoutId: string) => void;
};

export const initialState: InitialStateProps = {
  getExercises: async (workoutId) => {},
  exercises: [],
  addExercise: async ({ workoutId, name, reps, weight, rir }) => {},
  isLoading: false,
  editingExerciseId: null,
  values: {
    name: "",
    reps: 0,
    weight: 0,
    rir: 0,
  },
  updateValues: {
    name: "",
    reps: 0,
    weight: 0,
    rir: 0,
  },
  loading: true,
  nearestExercises: undefined,
  handleExerciseRemove: async (id, workoutId) => {},
  handleWorkoutRemove: async (workoutId, currentWorkoutMuscle) => {},
  handleExerciseUpdate: async (exerciseId, workoutId) => {},
  getNearestWorkoutExercises: async (workoutId) => {},
  setLoadingFalse: () => {},
  handleChange: (e) => {},
  handleUpdateChange: (e) => {},
  handleEditButtonClick: (exercise) => {},
  currentWorkoutId: "",
  setCurrentWorkoutId: (workoutId) => {},
};

const ExerciseContext = React.createContext<InitialStateProps>(initialState);

type ExerciseProviderProps = {
  children: React.ReactNode;
};

const ExerciseProvider: React.FC<ExerciseProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer<
    React.Reducer<InitialStateProps, Action>
  >(reducer, initialState);
  const { authToken, getWorkoutsByMuscle } = useAppContext();

  const getExercises = async (workoutId: string) => {
    dispatch({ type: "GET_EXERCISES_BEGIN" });
    try {
      const { data } = await authToken.get(`/workouts/${workoutId}`);
      dispatch({ type: "GET_EXERCISES_SUCCESS", payload: data.exercises });
    } catch (error: any) {
      dispatch({
        type: "GET_EXERCISES_ERROR",
        // payload: { message: error.response.data.message },
      });
    }
  };

  const addExercise = async (data: AddExerciseProps) => {
    dispatch({ type: "ADD_EXERCISE_BEGIN" });
    try {
      await authToken.post("/exercises", data);
      dispatch({ type: "ADD_EXERCISE_SUCCESS" });
      await getExercises(data.workoutId);
    } catch (error: any) {
      dispatch({
        type: "ADD_EXERCISE_ERROR",
        // payload: { message: error.response.data.message },
      });
    }
  };

  const handleExerciseRemove = async (id: string, workoutId: string) => {
    try {
      await authToken.delete(`/exercises/${id}`);
      getExercises(workoutId);
    } catch (error) {
      console.log(error);
    }
  };

  const handleWorkoutRemove = async (
    workoutId: string,
    currentWorkoutMuscle: string
  ) => {
    try {
      await authToken.delete(`/workouts/${workoutId}`);
      getWorkoutsByMuscle(currentWorkoutMuscle);
    } catch (error) {
      console.log(error);
    }
  };

  const handleExerciseUpdate = async (
    exerciseId: string,
    workoutId: string
  ) => {
    try {
      await authToken.patch(`/exercises/${exerciseId}`, {
        ...state.updateValues,
        workoutId,
      });
      dispatch({ type: "SET_EDITING_EXERCISE_ID", payload: null });
      dispatch({ type: "SET_UPDATE_VALUES", payload: state.updateValues });
      getExercises(workoutId);
    } catch (error) {
      console.log(error);
    }
  };

  const getNearestWorkoutExercises = async (workoutId: string) => {
    try {
      const { data } = await authToken.get(`/workouts/${workoutId}`);
      dispatch({ type: "SET_NEAREST_EXERCISES", payload: data.exercises });
    } catch (error: any) {
      console.log(error);
    }
  };

  const setLoadingFalse = () => {
    dispatch({ type: "SET_LOADING", payload: false });
  };

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch({
      type: "SET_VALUES",
      payload: { ...state.values, [e.target.name]: e.target.value },
    });
  };

  const handleUpdateChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    dispatch({
      type: "SET_UPDATE_VALUES",
      payload: { ...state.updateValues, [e.target.name]: e.target.value },
    });
  };

  const handleEditButtonClick = (exercise: ExerciseDBProps) => {
    dispatch({ type: "SET_EDITING_EXERCISE_ID", payload: exercise.id });
    dispatch({
      type: "SET_UPDATE_VALUES",
      payload: {
        name: exercise.name,
        reps: exercise.reps,
        weight: exercise.weight,
        rir: exercise.rir,
      },
    });
  };

  const setCurrentWorkoutId = (workoutId: string) => {
    dispatch({ type: "SET_CURRENT_WORKOUT_ID", payload: workoutId });
  };

  return (
    <ExerciseContext.Provider
      value={{
        ...state,
        getExercises,
        addExercise,
        handleExerciseRemove,
        handleWorkoutRemove,
        handleExerciseUpdate,
        getNearestWorkoutExercises,
        setLoadingFalse,
        handleChange,
        handleUpdateChange,
        handleEditButtonClick,
        setCurrentWorkoutId,
      }}
    >
      {children}
    </ExerciseContext.Provider>
  );
};

export const useExerciseContext = () => {
  return useContext(ExerciseContext);
};

export { ExerciseProvider };
