import { useEffect } from "react";
import { Link } from "react-router-dom";
import bicepsImg from "../assets/images/muscles/biceps.svg";
import Wrapper from "../assets/wrappers/Dashboard";
import { Loading } from ".";
import { useAppContext } from "../context/appContext";

const Dashboard = () => {
  const { isLoading, getAllMuscles, muscles } = useAppContext();

  useEffect(() => {
    getAllMuscles();
  }, []);

  return (
    <Wrapper>
      {isLoading ? (
        <Loading />
      ) : (
        <>
          <main className="muscles">
            {muscles.map((muscle, index) => {
              return (
                <Link
                  to={`/singleMuscle/${muscle}`}
                  className="singleMuscle"
                  key={index}
                >
                  {muscle}
                  <img src={bicepsImg} alt="muscle image" />
                </Link>
              );
            })}
          </main>
        </>
      )}
    </Wrapper>
  );
};

export default Dashboard;
