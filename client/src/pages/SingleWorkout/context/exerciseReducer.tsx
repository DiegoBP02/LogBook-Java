import {
  ExerciseDBProps,
  ExerciseValuesProps,
  InitialStateProps,
} from "./exerciseContext";

export type Action =
  | { type: "GET_EXERCISES_BEGIN" }
  | { type: "GET_EXERCISES_SUCCESS"; payload: ExerciseDBProps[] }
  | { type: "GET_EXERCISES_ERROR" }
  | { type: "ADD_EXERCISE_BEGIN" }
  | { type: "ADD_EXERCISE_SUCCESS" }
  | { type: "ADD_EXERCISE_ERROR" }
  | { type: "SET_EDITING_EXERCISE_ID"; payload: string | null }
  | { type: "SET_VALUES"; payload: ExerciseValuesProps }
  | { type: "SET_UPDATE_VALUES"; payload: ExerciseValuesProps }
  | { type: "SET_LOADING"; payload: boolean }
  | { type: "SET_NEAREST_EXERCISES"; payload: ExerciseDBProps[] | undefined }
  | { type: "SET_CURRENT_WORKOUT_ID"; payload: string };

const reducer: React.Reducer<InitialStateProps, Action> = (state, action) => {
  switch (action.type) {
    case "GET_EXERCISES_BEGIN":
      return { ...state, isLoading: true };
    case "GET_EXERCISES_SUCCESS":
      return { ...state, isLoading: false, exercises: action.payload };
    case "GET_EXERCISES_ERROR":
      return { ...state, isLoading: false };
    case "ADD_EXERCISE_BEGIN":
      return { ...state, isLoading: true };
    case "ADD_EXERCISE_SUCCESS":
      return { ...state, isLoading: false };
    case "ADD_EXERCISE_ERROR":
      return { ...state, isLoading: false };
    case "SET_EDITING_EXERCISE_ID":
      return { ...state, editingExerciseId: action.payload };
    case "SET_VALUES":
      return { ...state, values: action.payload };
    case "SET_UPDATE_VALUES":
      return { ...state, updateValues: action.payload };
    case "SET_LOADING":
      return { ...state, loading: action.payload };
    case "SET_NEAREST_EXERCISES":
      return { ...state, nearestExercises: action.payload };
    case "SET_CURRENT_WORKOUT_ID":
      return { ...state, currentWorkoutId: action.payload };
    default:
      return state;
  }
};

export default reducer;
