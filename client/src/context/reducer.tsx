import { InitialStateProps, WorkoutProps } from "./appContext";

export type Action =
  | { type: "DISPLAY_ALERT" }
  | { type: "CLEAR_ALERT" }
  | { type: "SETUP_USER_BEGIN" }
  | {
      type: "SETUP_USER_SUCCESS";
      payload: { token: string; username: string; alertText: string };
    }
  | { type: "SETUP_USER_ERROR"; payload: { message: string } }
  | { type: "LOGOUT_USER" }
  | { type: "GET_MUSCLES_BEGIN" }
  | { type: "GET_MUSCLES_SUCCESS"; payload: string[] }
  | { type: "GET_MUSCLES_ERROR"; payload: { message: string } }
  | { type: "GET_WORKOUTS_BY_MUSCLE_BEGIN" }
  | { type: "GET_WORKOUTS_BY_MUSCLE_SUCCESS"; payload: WorkoutProps[] }
  | { type: "GET_WORKOUTS_BY_MUSCLE_ERROR"; payload: { message: string } }
  | { type: "ADD_WORKOUT_BEGIN" }
  | { type: "ADD_WORKOUT_SUCCESS" }
  | { type: "ADD_WORKOUT_ERROR"; payload: { message: string } };

const reducer: React.Reducer<InitialStateProps, Action> = (state, action) => {
  switch (action.type) {
    case "DISPLAY_ALERT":
      return {
        ...state,
        showAlert: true,
        alertType: "danger",
        alertText: "Please provide all values!",
      };
    case "CLEAR_ALERT":
      return {
        ...state,
        showAlert: false,
        alertType: "",
        alertText: "",
      };
    case "SETUP_USER_BEGIN":
      return {
        ...state,
        userLoading: true,
      };
    case "SETUP_USER_SUCCESS":
      return {
        ...state,
        userLoading: false,
        userToken: action.payload.token,
        username: action.payload.username,
        showAlert: true,
        alertType: "success",
        alertText: action.payload.alertText,
      };
    case "SETUP_USER_ERROR":
      return {
        ...state,
        userLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    case "LOGOUT_USER":
      return {
        ...state,
        userToken: "",
      };
    case "GET_MUSCLES_BEGIN":
      return {
        ...state,
        isLoading: true,
      };

    case "GET_MUSCLES_SUCCESS":
      return {
        ...state,
        isLoading: false,
        muscles: action.payload,
      };

    case "GET_MUSCLES_ERROR":
      return {
        ...state,
        isLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    case "GET_WORKOUTS_BY_MUSCLE_BEGIN":
      return {
        ...state,
        isLoading: true,
      };
    case "GET_WORKOUTS_BY_MUSCLE_SUCCESS":
      return { ...state, isLoading: false, workouts: action.payload };
    case "GET_WORKOUTS_BY_MUSCLE_ERROR":
      return {
        ...state,
        isLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    case "ADD_WORKOUT_BEGIN":
      return {
        ...state,
        isLoading: true,
      };
    case "ADD_WORKOUT_SUCCESS":
      return { ...state, isLoading: false };
    case "ADD_WORKOUT_ERROR":
      return {
        ...state,
        isLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    default:
      return state;
  }
};
export default reducer;
