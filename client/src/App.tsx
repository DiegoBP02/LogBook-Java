import { BrowserRouter, Route, Routes } from "react-router-dom";
import {
  Landing,
  Error,
  SharedLayout,
  AuthPage,
  ProtectedRoute,
} from "./pages";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route
          path="/"
          element={
            <ProtectedRoute>
              <SharedLayout />
            </ProtectedRoute>
          }
        />
        <Route path="/landing" element={<Landing />} />
        <Route path="/auth" element={<AuthPage />} />
        <Route path="*" element={<Error />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
