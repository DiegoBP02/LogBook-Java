import React, { useContext, useReducer } from "react";
import reducer, { ActionType } from "./reducer";
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
  GET_WORKOUTS_BY_MUSCLE_SUCCESS,
  GET_WORKOUTS_BY_MUSCLE_ERROR,
  LOGOUT_USER,
  SETUP_USER_BEGIN,
  SETUP_USER_ERROR,
  SETUP_USER_SUCCESS,
} from "./actions";
import axios from "axios";

const token = localStorage.getItem("token");

export interface WorkoutProps {
  date: string;
  id: string;
  lowerRepsRange: number;
  upperRepsRange: number;
}

export type InitialStateProps = {
  displayAlert: () => void;
  showAlert: boolean;
  alertText: string;
  alertType: string;
  clearAlert: () => void;
  setupUser: (
    currentUser: object,
    endPoint: "register" | "login",
    alertText: string
  ) => Promise<void>;
  userToken: string;
  isLoading: boolean;
  logoutUser: () => Promise<void>;
  getAllMuscles: () => Promise<void>;
  muscles: string[];
  getWorkoutsByMuscle: (muscle: string) => Promise<void>;
  workouts: WorkoutProps[];
  addWorkout: (
    date: string,
    muscle: string,
    lowerRepsRange: number,
    upperRepsRange: number
  ) => Promise<void>;
};

export const initialState: InitialStateProps = {
  displayAlert: () => {},
  showAlert: false,
  alertText: "",
  alertType: "",
  clearAlert: () => {},
  setupUser: async () => {},
  userToken: token || "",
  isLoading: false,
  logoutUser: async () => {},
  getAllMuscles: async () => {},
  muscles: [],
  getWorkoutsByMuscle: async (muscle) => {},
  workouts: [],
  addWorkout: async (date, muscle, lowerRepsRange, upperRepsRange) => {},
};

const AppContext = React.createContext<InitialStateProps>(initialState);

type AppProviderProps = {
  children: React.ReactNode;
};

const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer<
    React.Reducer<InitialStateProps, ActionType>
  >(reducer, initialState);

  const authFetch = axios.create({
    baseURL: "http://localhost:8080",
  });

  const authToken = axios.create({
    baseURL: "http://localhost:8080",
    headers: {
      Authorization: state.userToken,
    },
  });

  authFetch.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      if (error.response.status === 401) {
        console.log("Unauthorized error!");
      }
      return Promise.reject(error);
    }
  );

  authToken.interceptors.response.use(
    (response) => {
      return response;
    },
    (error) => {
      if (error.response.status === 401) {
        console.log("Unauthorized error!");
        logoutUser();
      }
      return Promise.reject(error);
    }
  );

  const displayAlert = () => {
    dispatch({ type: DISPLAY_ALERT });
    clearAlert();
  };

  const clearAlert = () => {
    setTimeout(() => {
      dispatch({ type: CLEAR_ALERT });
    }, 3000);
  };

  const setupUser = async (
    currentUser: object,
    endPoint: "register" | "login",
    alertText: string
  ) => {
    dispatch({ type: SETUP_USER_BEGIN });

    try {
      const { data } = await authFetch.post(`/auth/${endPoint}`, currentUser);
      dispatch({
        type: SETUP_USER_SUCCESS,
        payload: { token: data, alertText },
      });
      addTokenToLocalStorage(data);
    } catch (error: any) {
      dispatch({
        type: SETUP_USER_ERROR,
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  const addTokenToLocalStorage = (token: string) => {
    localStorage.setItem("token", token);
  };

  const removeTokenFromLocalStorage = () => {
    localStorage.removeItem("token");
  };

  const logoutUser = async () => {
    dispatch({ type: LOGOUT_USER });
    removeTokenFromLocalStorage();
  };

  const getAllMuscles = async () => {
    dispatch({ type: GET_MUSCLES_BEGIN });
    try {
      const { data } = await authToken.get("/muscles");
      dispatch({
        type: GET_MUSCLES_SUCCESS,
        payload: data,
      });
    } catch (error: any) {
      dispatch({
        type: GET_MUSCLES_ERROR,
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  const getWorkoutsByMuscle = async (muscle: string) => {
    dispatch({ type: GET_WORKOUTS_BY_MUSCLE_BEGIN });
    try {
      const { data } = await authToken.get(`/workouts/muscle/${muscle}`);
      const musclesData = data.map(
        ({ date, id, lowerRepsRange, upperRepsRange }: WorkoutProps) => ({
          date,
          id,
          lowerRepsRange,
          upperRepsRange,
        })
      );

      dispatch({ type: GET_WORKOUTS_BY_MUSCLE_SUCCESS, payload: musclesData });
    } catch (error: any) {
      dispatch({
        type: GET_WORKOUTS_BY_MUSCLE_ERROR,
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  const addWorkout = async (
    date: string,
    muscle: string,
    lowerRepsRange: number,
    upperRepsRange: number
  ) => {
    dispatch({ type: ADD_WORKOUT_BEGIN });
    try {
      await authToken.post("/workouts", {
        date,
        muscle,
        lowerRepsRange,
        upperRepsRange,
      });
      dispatch({ type: ADD_WORKOUT_SUCCESS });
      await getWorkoutsByMuscle(muscle);
    } catch (error: any) {
      dispatch({
        type: ADD_WORKOUT_ERROR,
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  return (
    <AppContext.Provider
      value={{
        ...state,
        displayAlert,
        clearAlert,
        setupUser,
        logoutUser,
        getAllMuscles,
        getWorkoutsByMuscle,
        addWorkout,
      }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = () => {
  return useContext(AppContext);
};

export { AppProvider };
