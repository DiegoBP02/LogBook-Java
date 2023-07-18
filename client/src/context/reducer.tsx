import {
  ADD_WORKOUT_BEGIN,
  ADD_WORKOUT_ERROR,
  ADD_WORKOUT_SUCCESS,
  CLEAR_ALERT,
  DISPLAY_ALERT,
  GET_MUSCLES_BEGIN,
  GET_MUSCLES_ERROR,
  GET_MUSCLES_SUCCESS,
  GET_WORKOUTS_BY_MUSCLE_BEGIN,
  GET_WORKOUTS_BY_MUSCLE_ERROR,
  GET_WORKOUTS_BY_MUSCLE_SUCCESS,
  LOGOUT_USER,
  SETUP_USER_BEGIN,
  SETUP_USER_ERROR,
  SETUP_USER_SUCCESS,
} from "./actions";
import { initialState, InitialStateProps } from "./appContext";

export type ActionType = {
  type: string;
  payload?: any;
};

const reducer: React.Reducer<InitialStateProps, ActionType> = (
  state,
  action
) => {
  switch (action.type) {
    case DISPLAY_ALERT:
      return {
        ...state,
        showAlert: true,
        alertType: "danger",
        alertText: "Please provide all values!",
      };
    case CLEAR_ALERT:
      return {
        ...state,
        showAlert: false,
        alertType: "",
        alertText: "",
      };
    case SETUP_USER_BEGIN:
      return {
        ...state,
        userLoading: true,
      };
    case SETUP_USER_SUCCESS:
      return {
        ...state,
        userLoading: false,
        userToken: action.payload.token,
        username: action.payload.username,
        showAlert: true,
        alertType: "success",
        alertText: action.payload.alertText,
      };
    case SETUP_USER_ERROR:
      return {
        ...state,
        userLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    case LOGOUT_USER:
      return {
        ...state,
        userToken: "",
      };
    case GET_MUSCLES_BEGIN:
      return {
        ...state,
        isLoading: true,
      };

    case GET_MUSCLES_SUCCESS:
      return {
        ...state,
        isLoading: false,
        muscles: action.payload,
      };

    case GET_MUSCLES_ERROR:
      return {
        ...state,
        isLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    case GET_WORKOUTS_BY_MUSCLE_BEGIN:
      return {
        ...state,
        isLoading: true,
      };
    case GET_WORKOUTS_BY_MUSCLE_SUCCESS:
      return { ...state, isLoading: false, workouts: action.payload };
    case GET_WORKOUTS_BY_MUSCLE_ERROR:
      return {
        ...state,
        isLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };
    case ADD_WORKOUT_BEGIN:
      return {
        ...state,
        isLoading: true,
      };
    case ADD_WORKOUT_SUCCESS:
      return { ...state, isLoading: false };

    case ADD_WORKOUT_ERROR:
      return {
        ...state,
        isLoading: false,
        showAlert: true,
        alertType: "danger",
        alertText: action.payload.message,
      };

    default:
      throw new Error(`No such action :${action.type}`);
  }
};
export default reducer;
