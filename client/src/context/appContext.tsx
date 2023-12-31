import React, { useContext, useReducer } from "react";
import reducer, { Action } from "./reducer";
import axios, { AxiosInstance } from "axios";

export interface CurrentUserProps {
  username: string;
  email: string;
  password: string;
}

export interface SetupUserProps {
  currentUser: CurrentUserProps;
  endPoint: "register" | "login";
  alertText: string;
}

export interface WorkoutProps {
  date: string;
  id: string;
  lowerRepsRange: number;
  upperRepsRange: number;
  muscle: string;
}

export interface AddWorkoutProps {
  date: string;
  muscle: string;
  lowerRepsRange: number;
  upperRepsRange: number;
}

export type InitialStateProps = {
  displayAlert: () => void;
  showAlert: boolean;
  alertText: string;
  alertType: string;
  clearAlert: () => void;
  setupUser: ({
    currentUser,
    endPoint,
    alertText,
  }: SetupUserProps) => Promise<void>;
  userToken: string;
  userLoading: boolean;
  username: string;
  isLoading: boolean;
  logoutUser: () => Promise<void>;
  getAllMuscles: () => Promise<void>;
  muscles: string[];
  getWorkoutsByMuscle: (muscle: string) => Promise<void>;
  workouts: WorkoutProps[];
  addWorkout: ({
    date,
    muscle,
    lowerRepsRange,
    upperRepsRange,
  }: AddWorkoutProps) => Promise<void>;
  authToken: AxiosInstance;
};

export const initialState: InitialStateProps = {
  displayAlert: () => {},
  showAlert: false,
  alertText: "",
  alertType: "",
  clearAlert: () => {},
  setupUser: async () => {},
  userToken: localStorage.getItem("token") || "",
  userLoading: false,
  username: localStorage.getItem("username") || "",
  isLoading: false,
  logoutUser: async () => {},
  getAllMuscles: async () => {},
  muscles: [],
  getWorkoutsByMuscle: async (muscle) => {},
  workouts: [],
  addWorkout: async ({ date, muscle, lowerRepsRange, upperRepsRange }) => {},
  authToken: axios.create(),
};

const AppContext = React.createContext<InitialStateProps>(initialState);

type AppProviderProps = {
  children: React.ReactNode;
};

const AppProvider: React.FC<AppProviderProps> = ({ children }) => {
  const [state, dispatch] = useReducer<
    React.Reducer<InitialStateProps, Action>
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
    dispatch({ type: "DISPLAY_ALERT" });
    clearAlert();
  };

  const clearAlert = () => {
    setTimeout(() => {
      dispatch({ type: "CLEAR_ALERT" });
    }, 3000);
  };

  const setupUser = async ({
    currentUser,
    endPoint,
    alertText,
  }: SetupUserProps) => {
    dispatch({ type: "SETUP_USER_BEGIN" });

    try {
      const { data } = await authFetch.post(`/auth/${endPoint}`, currentUser);
      dispatch({
        type: "SETUP_USER_SUCCESS",
        payload: { token: data, alertText, username: currentUser.username },
      });
      addToLocalStorage("token", data);
      addToLocalStorage("username", currentUser.username);
    } catch (error: any) {
      dispatch({
        type: "SETUP_USER_ERROR",
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  const addToLocalStorage = (key: string, value: string) => {
    localStorage.setItem(key, value);
  };

  const removeFromLocalStorage = (key: string) => {
    localStorage.removeItem(key);
  };

  const logoutUser = async () => {
    dispatch({ type: "LOGOUT_USER" });
    removeFromLocalStorage("token");
    removeFromLocalStorage("username");
  };

  const getAllMuscles = async () => {
    dispatch({ type: "GET_MUSCLES_BEGIN" });
    try {
      const { data } = await authToken.get("/muscles");
      dispatch({
        type: "GET_MUSCLES_SUCCESS",
        payload: data,
      });
    } catch (error: any) {
      dispatch({
        type: "GET_MUSCLES_ERROR",
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  const getWorkoutsByMuscle = async (muscle: string) => {
    dispatch({ type: "GET_WORKOUTS_BY_MUSCLE_BEGIN" });
    try {
      const { data } = await authToken.get(`/workouts/muscle/${muscle}`);
      const musclesData: WorkoutProps[] = data.map(
        ({
          date,
          id,
          lowerRepsRange,
          upperRepsRange,
          muscle,
        }: WorkoutProps) => ({
          date,
          id,
          lowerRepsRange,
          upperRepsRange,
          muscle,
        })
      );

      dispatch({
        type: "GET_WORKOUTS_BY_MUSCLE_SUCCESS",
        payload: musclesData,
      });
    } catch (error: any) {
      dispatch({
        type: "GET_WORKOUTS_BY_MUSCLE_ERROR",
        payload: { message: error.response.data.message },
      });
    }
    clearAlert();
  };

  const addWorkout = async (data: AddWorkoutProps) => {
    dispatch({ type: "ADD_WORKOUT_BEGIN" });
    try {
      await authToken.post("/workouts", data);
      dispatch({ type: "ADD_WORKOUT_SUCCESS" });
      await getWorkoutsByMuscle(data.muscle);
    } catch (error: any) {
      dispatch({
        type: "ADD_WORKOUT_ERROR",
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
        authToken,
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
