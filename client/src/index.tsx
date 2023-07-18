import React from "react";
import ReactDOM from "react-dom/client";
import "./index.css";
import App from "./App";
import { AppProvider } from "./context/appContext";
import { ExerciseProvider } from "./pages/SingleWorkout/context/exerciseContext";

const root = ReactDOM.createRoot(
  document.getElementById("root") as HTMLElement
);
root.render(
  <React.StrictMode>
    <AppProvider>
      <ExerciseProvider>
        <App />
      </ExerciseProvider>
    </AppProvider>
  </React.StrictMode>
);
