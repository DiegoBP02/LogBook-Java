import React, { useContext, useEffect, useReducer } from "react";
import reducer, { ActionType } from "./reducer";
import {
  CLEAR_ALERT,
  DISPLAY_ALERT,
  SETUP_USER_BEGIN,
  SETUP_USER_ERROR,
  SETUP_USER_SUCCESS,
} from "./actions";
import axios from "axios";

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
};

export const initialState: InitialStateProps = {
  displayAlert: () => {},
  showAlert: false,
  alertText: "",
  alertType: "",
  clearAlert: () => {},
  setupUser: async () => {},
  userToken: "",
  isLoading: false,
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
    } catch (error: any) {
      dispatch({
        type: SETUP_USER_ERROR,
        payload: { msg: error.response.data.msg },
      });
    }
    clearAlert();
  };

  return (
    <AppContext.Provider
      value={{ ...state, displayAlert, clearAlert, setupUser }}
    >
      {children}
    </AppContext.Provider>
  );
};

export const useAppContext = () => {
  return useContext(AppContext);
};

export { AppProvider };
