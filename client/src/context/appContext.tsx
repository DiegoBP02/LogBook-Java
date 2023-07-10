import React, { useContext, useReducer } from "react";
import reducer, { ActionType } from "./reducer";
import {
  CLEAR_ALERT,
  DISPLAY_ALERT,
  LOGOUT_USER,
  SETUP_USER_BEGIN,
  SETUP_USER_ERROR,
  SETUP_USER_SUCCESS,
} from "./actions";
import axios from "axios";

const token = localStorage.getItem("token");

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
        payload: { msg: error.response.data.message },
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

  return (
    <AppContext.Provider
      value={{ ...state, displayAlert, clearAlert, setupUser, logoutUser }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = () => {
  return useContext(AppContext);
};

export { AppProvider };
