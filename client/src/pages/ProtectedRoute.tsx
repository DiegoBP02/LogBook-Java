import React from "react";
import { Navigate } from "react-router-dom";
import { Loading } from "../components";
import { useAppContext } from "../context/appContext";

type ProtectedRouteProps = {
  children: React.ReactNode;
};

const ProtectedRoute: React.FC<ProtectedRouteProps> = ({ children }) => {
  const { userLoading, userToken } = useAppContext();

  if (userLoading) return <Loading />;

  if (!userToken) {
    return <Navigate to="/landing" />;
  }

  return <>{children}</>;
};

export default ProtectedRoute;
