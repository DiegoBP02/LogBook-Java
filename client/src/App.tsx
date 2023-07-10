import { BrowserRouter, Route, Routes } from "react-router-dom";
import { Landing, Error, SharedLayout, Register } from "./pages";

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<SharedLayout />} />
        <Route path="/landing" element={<Landing />} />
        <Route path="/auth" element={<Register />} />
        <Route path="*" element={<Error />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
